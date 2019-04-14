package com.kakaoPay.housingFinance.member.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import com.kakaoPay.housingFinance.common.exception.RestApiException;
import com.kakaoPay.housingFinance.common.util.EncodeUtil;
import com.kakaoPay.housingFinance.member.model.Member;
import com.kakaoPay.housingFinance.member.repository.MemberRepository;

@RunWith(MockitoJUnitRunner.class)
public class MemberServiceTest {

	@InjectMocks
	private MemberService service;

	@Mock
	private MemberRepository memberRepository;

	private final static String ID = "testId";
	private final static String PASSWORD = "testpw";

	@Test
	public void signup_success() throws RestApiException {
		doReturn(false).when(memberRepository).existsById(ID);
		service.signup(ID, PASSWORD);
		verify(memberRepository, times(1)).existsById(ID);
	}

	@Test
	public void signup_fail_duplicated_id() {
		doReturn(true).when(memberRepository).existsById(ID);
		try {
			service.signup(ID, PASSWORD);
		} catch (RestApiException e) {
			assertThat(e.getHttpstatus(), is(HttpStatus.CONFLICT));
		}
		verify(memberRepository, times(1)).existsById(ID);
	}

	@Test
	public void signup_fail_invalid_parameter() {
		// id empty
		try {
			service.signup(null, PASSWORD);
			fail();
		} catch (RestApiException e) {
			assertThat(e.getHttpstatus(), is(HttpStatus.BAD_REQUEST));
		}
		verify(memberRepository, times(0)).existsById(ID);

		// password empty
		try {
			service.signup(ID, null);
			fail();
		} catch (RestApiException e) {
			assertThat(e.getHttpstatus(), is(HttpStatus.BAD_REQUEST));
		}
		verify(memberRepository, times(0)).existsById(ID);
	}

	@Test
	public void signin_success() throws RestApiException {
		doReturn(ID).when(memberRepository).findMemberByIdPassword(ID, EncodeUtil.encodeBase64(PASSWORD));
		service.signin(ID, PASSWORD);
		verify(memberRepository, times(1)).findMemberByIdPassword(ID, EncodeUtil.encodeBase64(PASSWORD));
	}

	@Test
	public void signin_fail_not_found_member() {
		doReturn(null).when(memberRepository).findMemberByIdPassword(ID, EncodeUtil.encodeBase64(PASSWORD));
		try {
			service.signin(ID, PASSWORD);
			fail();
		} catch (RestApiException e) {
			assertThat(e.getHttpstatus(), is(HttpStatus.UNAUTHORIZED));
		}
		verify(memberRepository, times(1)).findMemberByIdPassword(ID, EncodeUtil.encodeBase64(PASSWORD));
	}

	@Test
	public void signin_fail_invalid_parameter() {
		try {
			service.signin(ID, null);
			fail();
		} catch (RestApiException e) {
			assertThat(e.getHttpstatus(), is(HttpStatus.BAD_REQUEST));
		}
		verify(memberRepository, times(0)).findMemberByIdPassword(ID, PASSWORD);
		
		try {
			service.signin(null, ID);
			fail();
		} catch (RestApiException e) {
			assertThat(e.getHttpstatus(), is(HttpStatus.BAD_REQUEST));
		}
		verify(memberRepository, times(0)).findMemberByIdPassword(ID, PASSWORD);

	}

}
