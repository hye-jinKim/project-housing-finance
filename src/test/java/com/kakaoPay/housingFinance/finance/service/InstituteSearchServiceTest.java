package com.kakaoPay.housingFinance.finance.service;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.kakaoPay.housingFinance.common.exception.RestApiException;
import com.kakaoPay.housingFinance.finance.repository.InstituteRepository;

import antlr.collections.List;

@RunWith(MockitoJUnitRunner.class)
public class InstituteSearchServiceTest {

	@InjectMocks
	private InstituteSearchService service;

	@Mock
	private InstituteRepository instituteRepository;
	@Test
	public void success() {
		try {
			doReturn(Lists.newArrayList()).when(instituteRepository).findAllName();
			service.getAllInstitutes();
		} catch (RestApiException e) {
			fail();
		}
	}
}
