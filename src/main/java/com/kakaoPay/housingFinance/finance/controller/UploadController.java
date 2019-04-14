package com.kakaoPay.housingFinance.finance.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakaoPay.housingFinance.common.exception.RestApiException;
import com.kakaoPay.housingFinance.finance.service.UploadService;

@RestController
public class UploadController {
	@Autowired
	private UploadService uploadService;

	/**
	 * 1. 데이터 파일에서 각 레코드를 데이터베이스에 저장하는 API 개발
	 */
	@PutMapping("/api/institutes")
	public ResponseEntity<?> upload() {
		try {
			uploadService.upload();
			return new ResponseEntity<Void>(HttpStatus.CREATED);
		} catch (RestApiException e) {
			return new ResponseEntity<Map<String, Object>>(e.getResponse(), e.getHttpstatus());
		}
	}

}
