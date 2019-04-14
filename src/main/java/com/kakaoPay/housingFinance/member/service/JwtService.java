package com.kakaoPay.housingFinance.member.service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.kakaoPay.housingFinance.common.exception.RestApiException;
import com.kakaoPay.housingFinance.member.repository.MemberRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

	@Autowired
	private MemberRepository memberRepository;

	private static final String SECRET_KEY = "HyeJinSecretKey";
	private static final int EXPIRE_TIME = 1000 * 3600 * 24;

	public String createToken(String id) throws RestApiException {
		try {
			Date expireDate = new Date();
			// 만료시간을 24시간으로 설정
			expireDate.setTime(expireDate.getTime() + EXPIRE_TIME);
			Map<String, Object> userInfo = Maps.newHashMap();
			userInfo.put("id", id);

			// id 정보를 담은 claim과 만료시간으로 jwt 생성
			String jwt = Jwts.builder().setHeaderParam("typ", "JWT").setClaims(userInfo).setExpiration(expireDate)
					.signWith(SignatureAlgorithm.HS256, generateKey()).compact();

			// 발급된 jwt를 db에 저장
			memberRepository.update(id, jwt);
			return jwt;
		} catch (Exception e) {
			System.out.println("[JwtService] createToken unknown exception");
			e.printStackTrace();
			throw new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String reissueToken(String oldToken) throws RestApiException {
		try {
			// 토큰이 Bearer type이 아니라면 실패
			if (!isBearerType(oldToken)) {
				throw new RestApiException(HttpStatus.BAD_REQUEST, "token is not bearer type");
			}
			
			// 토큰 유효성 검증 후, 신규 토큰 발급
			// 잘못된 토큰은 error를 내려준다.
			return createToken(getIdWithValidCheck(oldToken, true).toString());
		} catch (ExpiredJwtException e) {
			throw new RestApiException(HttpStatus.BAD_REQUEST, "token is expired, please login");
		} catch (RestApiException e) {
			throw e;
		} catch (Exception e) {
			System.out.println("[JwtService] reissueToken unknown exception");
			e.printStackTrace();
			throw new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public boolean isUsable(String jwt) {
		try {
			// 토큰이 유효한지 검증한 후, id 조회
			String id = String.valueOf(getIdWithValidCheck(jwt, false));
			return StringUtils.isNotBlank(memberRepository.findByIdAndToken(id, jwt));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@VisibleForTesting
	Object getIdWithValidCheck(String oldToken, boolean wannaRemoveBearer) throws RestApiException {
		// Bearer type일 경우, Bearer 제거
		String token = wannaRemoveBearer ? removeBearer(oldToken) : oldToken;
		Claims claims = Jwts.parser().setSigningKey(generateKey()).parseClaimsJws(token).getBody();
		Object id = claims.get("id");
		
		// id 유효성 체크
		validId(id);
		return id;
	}

	private void validId(Object id) throws RestApiException {
		if (id == null) {
			throw new RestApiException(HttpStatus.UNAUTHORIZED, "authorization fail");
		}

		// 존재하지 않는 회원이면 실패
		if (!memberRepository.existsById(id.toString())) {
			throw new RestApiException(HttpStatus.UNAUTHORIZED, "authorization fail");
		}
	}

	private String removeBearer(String token) {
		String[] tokens = StringUtils.split(token, " ");
		return tokens[1];
	}

	private boolean isBearerType(String token) {
		String[] tokens = StringUtils.split(token, " ");
		if (tokens.length < 2) {
			return false;
		}

		return StringUtils.equals(tokens[0], "Bearer");
	}

	/**
	 * HS256 Key 생성
	 * @return
	 */
	private Key generateKey() {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
		return new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
	}

}
