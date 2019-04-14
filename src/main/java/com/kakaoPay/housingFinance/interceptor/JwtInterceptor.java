package com.kakaoPay.housingFinance.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kakaoPay.housingFinance.member.service.JwtService;

/**
 * Interceptor configuration 참고{@link InterceptorConfiguration}
 * api 호출시 토큰검증 인터셉터
 * @author hyejin
 */
@Component
public class JwtInterceptor extends HandlerInterceptorAdapter {

	private static final String HEADER_AUTH = "Authorization";

	@Autowired
	private JwtService jwtService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		final String token = request.getHeader(HEADER_AUTH);
		// 유효한 토큰인지 검증
		if (token != null && jwtService.isUsable(token)) {
			return super.preHandle(request, response, handler);
		}
		response.sendError(HttpStatus.UNAUTHORIZED.value(), "invalid token");
		return false;
	}
}
