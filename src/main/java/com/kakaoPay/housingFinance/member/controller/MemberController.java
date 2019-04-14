package com.kakaoPay.housingFinance.member.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kakaoPay.housingFinance.common.exception.RestApiException;
import com.kakaoPay.housingFinance.member.service.JwtService;
import com.kakaoPay.housingFinance.member.service.MemberService;

@RestController
@RequestMapping("/member")
public class MemberController {

	@Autowired
	private MemberService memberService;

	@Autowired
	private JwtService jwtService;

	@PutMapping("/signup")
	public ResponseEntity<?> signup(@RequestParam(required = false) String userId,
			@RequestParam(required = false) String password) {
		try {
			memberService.signup(userId, password);
			String token = jwtService.createToken(userId);
			return new ResponseEntity<String>(token, HttpStatus.OK);
		} catch (RestApiException e) {
			return new ResponseEntity<Map<String, Object>>(e.getResponse(), e.getHttpstatus());
		}
	}

	@PostMapping("/signin")
	public ResponseEntity<?> singin(@RequestParam(required = false) String userId,
			@RequestParam(required = false) String password) {
		try {
			String id = memberService.signin(userId, password);
			String token = jwtService.createToken(id);
			return new ResponseEntity<String>(token, HttpStatus.OK);
		} catch (RestApiException e) {
			return new ResponseEntity<Map<String, Object>>(e.getResponse(), e.getHttpstatus());
		}
	}

	@GetMapping("/refresh")
	public ResponseEntity<?> refresh(HttpServletRequest request) {
		try {
			String token = jwtService.reissueToken(request.getHeader("Authorization"));
			return new ResponseEntity<String>(token, HttpStatus.OK);
		} catch (RestApiException e) {
			return new ResponseEntity<Map<String, Object>>(e.getResponse(), e.getHttpstatus());
		}
	}

}
