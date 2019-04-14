package com.kakaoPay.housingFinance.member.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import com.google.common.collect.Maps;
import com.kakaoPay.housingFinance.common.exception.RestApiException;
import com.kakaoPay.housingFinance.member.repository.MemberRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RunWith(MockitoJUnitRunner.class)
public class JwtServiceTest {

	@InjectMocks
	@Spy
	private JwtService service;

	@Mock
	private MemberRepository memberRepository;

	private String jwt = "";

	@Before
	public void init() {
		jwt = makeJwt(ID, null);
	}

	private static final String ID = "testId";
	private static final String SECRET_KEY = "HyeJinSecretKey";

	@Test
	public void createToken_success() throws RestApiException {
		service.createToken(ID);
	}

	@Test
	public void reissueToken_success() throws RestApiException {
		doReturn(true).when(memberRepository).existsById(ID);

		String result = service.reissueToken("Bearer " + jwt);
		assertThat(getId(result), is(ID));

		verify(memberRepository, times(1)).existsById(ID);
	}

	@Test
	public void reissueToken_fail_is_not_bearer_type() {
		try {
			service.reissueToken("asdf" + jwt);
			fail();
		} catch (RestApiException e) {
			assertThat(e.getHttpstatus(), is(HttpStatus.BAD_REQUEST));
		}
	}

	@Test
	public void reissueToken_fail_invalid_id() {
		doReturn(false).when(memberRepository).existsById("nothing");
		try {
			service.reissueToken("Bearer " + makeJwt("nothing", null));
		} catch (RestApiException e) {
			assertThat(e.getHttpstatus(), is(HttpStatus.UNAUTHORIZED));
		}
		verify(memberRepository, times(1)).existsById("nothing");
	}

	@Test
	public void reissueToken_fail_expired_token() {
		try {
			service.reissueToken("Bearer " + makeJwt("nothing", 1));
		} catch (RestApiException e) {
			assertThat(e.getHttpstatus(), is(HttpStatus.BAD_REQUEST));
			assertThat(MapUtils.getString(e.getResponse(), "detail"), is("token is expired"));
		}
	}

	@Test
	public void reissueToken_fail_internal_server_error() {
		try {
			doReturn(null).when(service).getIdWithValidCheck("Bearer " + jwt, true);
			service.reissueToken("Bearer " + jwt);
		} catch (RestApiException e) {
			assertThat(e.getHttpstatus(), is(HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	@Test
	public void isUsable_success() throws RestApiException {
		doReturn(true).when(memberRepository).existsById(ID);
		doReturn(ID).when(memberRepository).findByIdAndToken(ID, jwt);

		boolean result = service.isUsable(jwt);

		assertThat(result, is(true));
		verify(memberRepository, times(1)).existsById(ID);
		verify(memberRepository, times(1)).findByIdAndToken(ID, jwt);
	}

	@Test
	public void isUsable_fail_expired_token() {
		String oldToken = makeJwt(ID, 1);
		boolean result = service.isUsable(oldToken);
		assertThat(result, is(false));
		
		verify(memberRepository, times(0)).findByIdAndToken(ID, oldToken);

	}
	
	@Test
	public void isUsable_fail_invalid_id() {
		// db에 존재하지 않는 id
		String id = "nothing";
		String oldToken = makeJwt(id, null);
		doReturn(false).when(memberRepository).existsById(id);

		boolean result = service.isUsable(oldToken);
		assertThat(result, is(false));
		verify(memberRepository, times(1)).existsById(id);
		
		// blank id
		id = StringUtils.EMPTY;
		oldToken = makeJwt(id, null);
		doReturn(false).when(memberRepository).existsById(id);

		result = service.isUsable(oldToken);
		assertThat(result, is(false));
		verify(memberRepository, times(1)).existsById(id);
	}

	private String getId(String token) {
		Claims claims = Jwts.parser().setSigningKey(this.generateKey()).parseClaimsJws(token).getBody();
		Object id = claims.get("id");
		return String.valueOf(id);
	}

	private String makeJwt(String id, Integer time) {
		Integer defaultExpireTime = 1000 * 3600 * 24;
		Integer expireTime = time != null ? time : defaultExpireTime;
		Date expireDate = new Date();
		expireDate.setTime(expireDate.getTime() + expireTime);
		Map<String, Object> userInfo = Maps.newHashMap();
		userInfo.put("id", id);
		String jwt = Jwts.builder().setHeaderParam("typ", "JWT").setClaims(userInfo).setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS256, generateKey()).compact();

		return jwt;
	}

	private Key generateKey() {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
		return new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
	}
}
