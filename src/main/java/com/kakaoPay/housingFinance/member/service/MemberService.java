package com.kakaoPay.housingFinance.member.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.kakaoPay.housingFinance.common.exception.RestApiException;
import com.kakaoPay.housingFinance.common.util.EncodeUtil;
import com.kakaoPay.housingFinance.member.model.Member;
import com.kakaoPay.housingFinance.member.repository.MemberRepository;

@Service
public class MemberService {

	@Autowired
	private MemberRepository memberRepository;

	public void signup(String userId, String password) throws RestApiException {
		try {
			// id, password 유효성 검증
			validParameter(userId, password);
			
			// password base64로 encoding하여 저장
			Member member = new Member(userId, EncodeUtil.encodeBase64(password));
			
			// id 중복 check
			if (memberRepository.existsById(userId)) {
				throw new RestApiException(HttpStatus.CONFLICT);
			}
			memberRepository.save(member);
		} catch (RestApiException e) {
			throw e;
		} catch (Exception e) {
			System.out.println("[MemberService] signup unknown exception");
			e.printStackTrace();
			throw new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String signin(String userId, String password) throws RestApiException {
		try {
			// id, password 유효성 검증
			validParameter(userId, password);
			
			// login 성공 유무 확인
			String id = memberRepository.findMemberByIdPassword(userId, EncodeUtil.encodeBase64(password));
			if (StringUtils.isBlank(id)) {
				throw new RestApiException(HttpStatus.UNAUTHORIZED);
			}
			return id;
		} catch (RestApiException e) {
			throw e;
		} catch (Exception e) {
			System.out.println("[MemberService] signin unknown exception");
			e.printStackTrace();
			throw new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private void validParameter(String userId, String password) throws RestApiException {
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(password)) {
			throw new RestApiException(HttpStatus.BAD_REQUEST, "id or password are empty");
		}
	}
}
