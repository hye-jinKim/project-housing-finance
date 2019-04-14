package com.kakaoPay.housingFinance.finance.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaoPay.housingFinance.finance.controller.SearchController;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchControllerTest {
	@Autowired
	private SearchController controller;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void getAllInstitutes() throws JsonProcessingException {
		print(controller.getAllInstitutes());
	}

	@Test
	public void getInstituteSupportAmounts() {
		print(controller.getInstituteSupportAmounts());
	}

	@Test
	public void getInstituteByMostSupportAmounts() {
		print(controller.getInstituteByMostSupportAmounts());
	}

	@Test
	public void getMinAndMax() {
		print(controller.getMinAndMax("외환은행", 2005, 2016));
	}

	private void print(Object result) {
		try {
			System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
