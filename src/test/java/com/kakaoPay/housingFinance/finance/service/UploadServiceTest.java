package com.kakaoPay.housingFinance.finance.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.kakaoPay.housingFinance.common.exception.RestApiException;
import com.kakaoPay.housingFinance.finance.repository.InstituteRepository;
import com.kakaoPay.housingFinance.finance.repository.SupportRepository;

@RunWith(MockitoJUnitRunner.class)
public class UploadServiceTest {

	@InjectMocks
	private UploadService service;

	@Mock
	private InstituteRepository instituteRepository;
	@Mock
	private SupportRepository supportRepository;
	
	@Test
	public void success() throws RestApiException {
		service.upload();
	}

}
