package com.kakaoPay.housingFinance.finance.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakaoPay.housingFinance.common.exception.RestApiException;
import com.kakaoPay.housingFinance.finance.service.InstituteSearchService;
import com.kakaoPay.housingFinance.finance.service.SupportSearchService;

@RestController
@RequestMapping("/api/institutes")
public class SearchController {

	@Autowired
	private SupportSearchService supportSearchService;

	@Autowired
	private InstituteSearchService instituteSearchService;

	/**
	 * 2. 주택금융 공급 금융기관(은행) 목록을 출력하는 API를 개발하세요.
	 * 
	 * @return
	 */
	@GetMapping
	public ResponseEntity<?> getAllInstitutes() {
		try {
			return new ResponseEntity<List<String>>(instituteSearchService.getAllInstitutes(), HttpStatus.OK);
		} catch (RestApiException e) {
			return new ResponseEntity<Map<String, Object>>(e.getResponse(), e.getHttpstatus());
		}
	}

	/**
	 * 3. 년도별 각 금융기관의 지원금액 합계를 출력하는 API를 개발하세요.
	 * 
	 * @return
	 */
	@GetMapping("/supportAmounts")
	public ResponseEntity<?> getInstituteSupportAmounts() {
		try {
			return new ResponseEntity<List<Map<String, Object>>>(supportSearchService.getInstituteSupportAmounts(),
					HttpStatus.OK);
		} catch (RestApiException e) {
			return new ResponseEntity<Map<String, Object>>(e.getResponse(), e.getHttpstatus());
		}
	}

	/**
	 * 4. 각 년도별 각 기관의 전체 지원금액 중에서 가장 큰 금액의 기관명을 출력하는 API 개발
	 * 
	 * @return
	 */
	@GetMapping("/most/supportAmounts")
	public ResponseEntity<Map<String, Object>> getInstituteByMostSupportAmounts() {
		try {
			return new ResponseEntity<Map<String, Object>>(supportSearchService.getInstituteByMostSupportAmounts(),
					HttpStatus.OK);
		} catch (Exception e) {
			System.out.println("[SearchController] getInstituteByMostSupportAmounts unknown exception");
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 5. 전체 년도(2005~2016)에서 외환은행의 지원금액 평균 중에서 가장 작은 금액과 큰 금액을 출력하는 API 개발
	 * 
	 * @param bank  은행명
	 * @param start 시작년도
	 * @param end   끝년도
	 * @return
	 */
	@GetMapping("/minAndMax/averageAmounts")
	public ResponseEntity<Map<String, Object>> getMinAndMax(String bank, int start, int end) {
		try {
			return new ResponseEntity<Map<String, Object>>(supportSearchService.getMinAndMaxAmounts(bank, start, end),
					HttpStatus.OK);
		} catch (RestApiException e) {
			return new ResponseEntity<Map<String, Object>>(e.getResponse(), e.getHttpstatus());
		}
	}
}
