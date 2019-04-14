package com.kakaoPay.housingFinance.finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.kakaoPay.housingFinance.common.exception.RestApiException;
import com.kakaoPay.housingFinance.finance.repository.InstituteRepository;

@Service
public class InstituteSearchService {

	@Autowired
	private InstituteRepository instituteRepository;

	public List<String> getAllInstitutes() throws RestApiException {
		try {
			return instituteRepository.findAllName();
		} catch (Exception e) {
			System.out.println("[InstituteSearchService] getAllInstitutes unknown exception");
			e.printStackTrace();
			throw new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
