package com.kakaoPay.housingFinance.finance.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kakaoPay.housingFinance.common.exception.RestApiException;
import com.kakaoPay.housingFinance.common.util.MapConvertUtil;
import com.kakaoPay.housingFinance.finance.model.Support;
import com.kakaoPay.housingFinance.finance.repository.SupportRepository;

@Service
public class SupportSearchService {

	@Autowired
	private SupportRepository supportRepository;

	public List<Map<String, Object>> getInstituteSupportAmounts() throws RestApiException {
		try {
			// 년도별 전체 지원금액 조회
			List<Support> amountList = supportRepository.findTotalAmountByYear(Sort.by("year"));
			List<Map<String, Object>> resultList = Lists.newArrayList();
			final String YEAR_STRING = "년";
			for (Support support : amountList) {
				LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
				data.put("year", String.valueOf(support.getYear()) + YEAR_STRING);
				data.put("total_amount", support.getTotalAmount());

				// 년도별 각 은행의 지원금액 조회
				List<Support> list = supportRepository.findDetailAmountByYear(support.getYear());
				data.put("detail_amount", makeDetailAmount(list));
				resultList.add(data);
			}
			return resultList;
		} catch (Exception e) {
			System.out.println("[SearchService] getInstituteSupportAmounts unknown exception");
			e.printStackTrace();
			throw new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	/**
	 * detail_amount 형식에 맞게 변환한다.
	 * list -> map
	 * @param supportList
	 * @return
	 */
	private Map<String, Long> makeDetailAmount(List<Support> supportList) {
		Map<String, Long> detailAmountMap = Maps.newHashMap();
		for (Support support : supportList) {
			detailAmountMap.put(support.getBank(), support.getTotalAmount());
		}
		return detailAmountMap;
	}

	public Map<String, Object> getInstituteByMostSupportAmounts() {
		// 지원금액이 가장 큰 은행 조회
		List<Support> supports = supportRepository.findInstituteByMostSupportAmounts(PageRequest.of(0, 1));
		if (CollectionUtils.isEmpty(supports)) {
			return Collections.EMPTY_MAP;
		}
		return MapConvertUtil.toMap(supports.get(0));
	}

	public Map<String, Object> getMinAndMaxAmounts(String bank, int start, int end) throws RestApiException {
		validParameter(bank, start, end);
		try {
			// 특정 은행의 년도별 평균 지원금액 조회(금액순으로 오름차순)
			List<Support> amountList = supportRepository.findAverageAmountBy(bank, start, end);
			if(CollectionUtils.isEmpty(amountList)) {
				return Collections.singletonMap("message", "not exist data, please check parameter(bank or start or end)");
			}
			
			List<Map<String, Object>> minAndMaxAmounts = Lists.newArrayList();

			// 지원금액 평균이 가장 작은 년도의 정보
			minAndMaxAmounts.add(makeAverageAmount(amountList.get(0)));
			// 지원금액 평균이 가장 큰 년도의 정보
			minAndMaxAmounts.add(makeAverageAmount(amountList.get(amountList.size() - 1)));

			Map<String, Object> resultMap = Maps.newHashMap();
			resultMap.put("bank", bank);
			resultMap.put("support_amount", minAndMaxAmounts);
			return resultMap;
		} catch (Exception e) {
			System.out.println("[SearchService] getMinAndMaxAmounts unknown exception");
			e.printStackTrace();
			throw new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * parameter validation check
	 * @param bank
	 * @param start
	 * @param end
	 * @throws RestApiException
	 */
	private void validParameter(String bank, int start, int end) throws RestApiException {
		if (StringUtils.isBlank(bank)) {
			throw new RestApiException(HttpStatus.BAD_REQUEST, "bank is required field");
		}

		if (start < 1 || end < 1 || start > end) {
			throw new RestApiException(HttpStatus.BAD_REQUEST, "start or end is wrong");
		}
	}

	private Map<String, Object> makeAverageAmount(Support support) {
		Map<String, Object> averageAmount = MapConvertUtil.toMap(support);
		averageAmount.put("amount", support.getAverageAmount());
		averageAmount.remove("averageAmount");
		return averageAmount;
	}
}
