package com.kakaoPay.housingFinance.finance.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import com.google.common.collect.Lists;
import com.kakaoPay.housingFinance.common.exception.RestApiException;
import com.kakaoPay.housingFinance.finance.model.Support;
import com.kakaoPay.housingFinance.finance.repository.SupportRepository;

@RunWith(MockitoJUnitRunner.class)
public class SupportSearchServiceTest {

	@InjectMocks
	private SupportSearchService service;

	@Mock
	private SupportRepository supportRepository;

	private static final int YEAR1 = 2005;
	private static final int YEAR2 = 2006;

	@Test
	public void getInstituteSupportAmounts_success() throws RestApiException {
		doReturn(makeAmountList()).when(supportRepository).findTotalAmountByYear(Sort.by("year"));
		doReturn(makeDetailAmountList("국민은행", 500L)).when(supportRepository).findDetailAmountByYear(eq(YEAR1));
		doReturn(makeDetailAmountList("국민은행", 1000L)).when(supportRepository).findDetailAmountByYear(eq(YEAR2));

		List<Map<String, Object>> resultList = service.getInstituteSupportAmounts();

		assertThat(resultList.size(), is(2));
		assertThat(resultList.get(0).get("year"), is(String.valueOf(YEAR1) + "년"));
		assertThat(resultList.get(1).get("total_amount"), is(2000L));
		assertThat(((Map) resultList.get(0).get("detail_amount")).get("국민은행"), is(500L));

		verify(supportRepository, times(1)).findTotalAmountByYear(Sort.by("year"));
		verify(supportRepository, times(1)).findDetailAmountByYear(eq(YEAR1));
		verify(supportRepository, times(1)).findDetailAmountByYear(eq(YEAR2));
	}

	@Test
	public void getInstituteSupportAmounts_return_empty_list_if_select_empty_amount_list() throws RestApiException {
		doReturn(Collections.EMPTY_LIST).when(supportRepository).findTotalAmountByYear(Sort.by("year"));

		List<Map<String, Object>> resultList = service.getInstituteSupportAmounts();
		assertThat(resultList.size(), is(0));

		verify(supportRepository, times(1)).findTotalAmountByYear(Sort.by("year"));
		verify(supportRepository, times(0)).findDetailAmountByYear(eq(YEAR1));
		verify(supportRepository, times(0)).findDetailAmountByYear(eq(YEAR2));
	}

	@Test
	public void getInstituteSupportAmounts_return_year_and_total_if_select_empty_detail_amount()
			throws RestApiException {
		doReturn(makeAmountList()).when(supportRepository).findTotalAmountByYear(Sort.by("year"));
		doReturn(Collections.EMPTY_LIST).when(supportRepository).findDetailAmountByYear(YEAR1);
		doReturn(Collections.EMPTY_LIST).when(supportRepository).findDetailAmountByYear(YEAR2);

		List<Map<String, Object>> resultList = service.getInstituteSupportAmounts();
		assertThat(resultList.size(), is(2));
		assertThat(((Map) resultList.get(0).get("detail_amount")).size(), is(0));
		assertThat(((Map) resultList.get(1).get("detail_amount")).size(), is(0));

		verify(supportRepository, times(1)).findTotalAmountByYear(Sort.by("year"));
		verify(supportRepository, times(1)).findDetailAmountByYear(eq(YEAR1));
		verify(supportRepository, times(1)).findDetailAmountByYear(eq(YEAR2));
	}

	@Test
	public void getInstituteSupportAmounts_fail_exception_case() {
		doReturn(makeAmountList()).when(supportRepository).findTotalAmountByYear(Sort.by("year"));
		doReturn(null).when(supportRepository).findDetailAmountByYear(YEAR1);

		try {
			service.getInstituteSupportAmounts();
			fail();
		} catch (RestApiException e) {
			assertThat(e.getHttpstatus(), is(HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	@Test
	public void getInstituteByMostSupportAmounts_success() {
		Support support = new Support(YEAR1, "testBank");
		List<Support> list = Lists.newArrayList(support);
		doReturn(list).when(supportRepository).findInstituteByMostSupportAmounts(PageRequest.of(0, 1));

		Map<String, Object> resultMap = service.getInstituteByMostSupportAmounts();

		assertThat(resultMap.size(), is(2));
		assertThat(MapUtils.getInteger(resultMap, "year"), is(YEAR1));

		verify(supportRepository, times(1)).findInstituteByMostSupportAmounts(PageRequest.of(0, 1));
	}

	@Test
	public void getInstituteByMostSupportAmounts_if_select_empty_MostSupportAmounts_institutes() {
		doReturn(Collections.EMPTY_LIST).when(supportRepository)
				.findInstituteByMostSupportAmounts(PageRequest.of(0, 1));

		Map<String, Object> resultMap = service.getInstituteByMostSupportAmounts();

		assertThat(resultMap.size(), is(0));
		assertThat(MapUtils.getString(resultMap, "year"), nullValue());

		verify(supportRepository, times(1)).findInstituteByMostSupportAmounts(PageRequest.of(0, 1));
	}

	@Test
	public void getMinAndMaxAmounts_success() throws RestApiException {
		String bank = "testbank";
		int start = YEAR1;
		int end = YEAR2;

		List<Support> amountList = makeAmountList();
		doReturn(amountList).when(supportRepository).findAverageAmountBy(bank, start, end);

		Map<String, Object> resultMap = service.getMinAndMaxAmounts(bank, start, end);

		assertThat(MapUtils.getString(resultMap, "bank"), is(bank));
		Map<String, Object> supportAmount = ((List<Map<String, Object>>) MapUtils.getObject(resultMap,
				"support_amount")).get(0);
		assertThat(MapUtils.getString(supportAmount, "amount"), is(amountList.get(0).getAverageAmount()));

		verify(supportRepository, times(1)).findAverageAmountBy(bank, start, end);
	}

	@Test
	public void getMinAndMaxAmounts_succes_but_empty_result() throws RestApiException {
		String bank = "testbank";
		int start = YEAR1;
		int end = YEAR2;

		doReturn(null).when(supportRepository).findAverageAmountBy(bank, start, end);

		Map<String, Object> resultMap = service.getMinAndMaxAmounts(bank, start, end);

		assertThat(MapUtils.getString(resultMap, "message"),
				is("not exist data, please check parameter(bank or start or end)"));
		verify(supportRepository, times(1)).findAverageAmountBy(bank, start, end);

	}

	@Test
	public void getMinAndMaxAmounts_fail_parameter() {
		// empty bank
		try {
			String bank = StringUtils.EMPTY;
			service.getMinAndMaxAmounts(bank, YEAR1, YEAR2);
		} catch (RestApiException e) {
			assertThat(e.getHttpstatus(), is(HttpStatus.BAD_REQUEST));
		}

		// invalid year : 년도가 0
		try {
			String bank = "testBank";
			int start = 0;
			int end = 2005;
			service.getMinAndMaxAmounts(bank, start, end);
		} catch (RestApiException e) {
			assertThat(e.getHttpstatus(), is(HttpStatus.BAD_REQUEST));
		}

		// invalid year : start년도가 end년도보다 큼
		try {
			String bank = "testBank";
			int start = 2005;
			int end = 2003;
			service.getMinAndMaxAmounts(bank, start, end);
		} catch (RestApiException e) {
			assertThat(e.getHttpstatus(), is(HttpStatus.BAD_REQUEST));
		}
	}

	private List<Support> makeAmountList() {
		List<Support> list = Lists.newArrayList();
		Support support = new Support(2005, 1000L);
		Support support2 = new Support(2006, 2000L);
		list.add(support);
		list.add(support2);
		return list;
	}

	private List<Support> makeDetailAmountList(String bank, Long amount) {
		List<Support> list = Lists.newArrayList();
		Support support = new Support(bank, amount);
		Support support2 = new Support(bank, amount);
		list.add(support);
		list.add(support2);
		return list;
	}
}
