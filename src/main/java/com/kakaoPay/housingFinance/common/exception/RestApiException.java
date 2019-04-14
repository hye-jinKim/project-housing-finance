package com.kakaoPay.housingFinance.common.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.google.common.collect.Maps;
/**
 * RestApi 공용 exception
 * @author hyejin
 */
public class RestApiException extends Exception {
	private static final long serialVersionUID = 1L;
	private HttpStatus httpstatus;
	private Map<String, Object> response;

	public RestApiException(HttpStatus httpStatus) {
		this.httpstatus = httpStatus;
		response = Maps.newHashMap();
		response.put("code", httpStatus.value());
		response.put("message", httpStatus.getReasonPhrase());
	}

	public RestApiException(HttpStatus httpStatus, String detailMessage) {
		this.httpstatus = httpStatus;
		response = Maps.newHashMap();
		response.put("code", httpStatus.value());
		response.put("message", httpStatus.getReasonPhrase());
		response.put("detail", detailMessage);
	}

	public HttpStatus getHttpstatus() {
		return httpstatus;
	}

	public void setHttpstatus(HttpStatus httpstatus) {
		this.httpstatus = httpstatus;
	}

	public Map<String, Object> getResponse() {
		return response;
	}

	public void setResponse(Map<String, Object> response) {
		this.response = response;
	}

}
