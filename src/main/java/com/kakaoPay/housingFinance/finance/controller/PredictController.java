package com.kakaoPay.housingFinance.finance.controller;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kakaoPay.housingFinance.finance.service.PredictService;

@Controller
public class PredictController {
	@Autowired
	private PredictService predictService;

	/**
	 * 특정 은행의 2018년 지원 금액 예측 API
	 * 
	 * @param bank
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/api/prediction")
	public ResponseEntity<Map<String, Object>> getPrediction(@RequestBody Map<String, Object> params) throws Exception {
		return new ResponseEntity<Map<String, Object>>(
				predictService.getPrediction(MapUtils.getString(params, "bank"), MapUtils.getInteger(params, "month")),
				HttpStatus.OK);
	}
}
