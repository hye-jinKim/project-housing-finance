package com.kakaoPay.housingFinance.finance.service;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kakaoPay.housingFinance.common.exception.RestApiException;
import com.kakaoPay.housingFinance.finance.model.Institute;
import com.kakaoPay.housingFinance.finance.model.Support;
import com.kakaoPay.housingFinance.finance.repository.InstituteRepository;
import com.kakaoPay.housingFinance.finance.repository.SupportRepository;
import com.opencsv.CSVReader;

@Service
public class UploadService {
	@Autowired
	private SupportRepository supportRepository;

	@Autowired
	private InstituteRepository instituteRepository;

	public void upload() throws RestApiException {

		CSVReader reader = null;

		try {
			
			// CSV File 경로
			// 파일명, 경로 변동 시 file_path 수정 필요함
			String file_path = "files/2019경력공채_개발_사전과제3_주택금융신용보증_금융기관별_공급현황.csv";
			String file = new ClassPathResource(file_path).getURI().getPath();

			List<Support> supportList = Lists.newArrayList();
			List<Institute> instituteList = Lists.newArrayList();
			
			// OpenCSV 활용 및 한글 깨짐 방지를 위한 CharacterSet EUC-KR적용 
			reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "EUC-KR"));
			
			// CSV Row당 데이터를 담기 위한 String 배열
			String[] line;
			Set<Integer> alreadyUsedCodeSet = Sets.newHashSet();

			boolean firstLine = true;
			while ((line = reader.readNext()) != null) {
				if (!firstLine) {
					for (int i = 2; i < line.length; i++) {
						if (!line[i].equals("")) {
							Support support = new Support();
							support.setYear(Integer.parseInt(line[0]));
							support.setMonth(Integer.parseInt(line[1]));
							support.setAmount(Long.parseLong(line[i].replaceAll(",", "")));
							support.setBank(String.valueOf(instituteList.get(i - 2).getName()));
							supportList.add(support);
						}
					}
				} else {
					for (int i = 2; i < line.length; i++) {
						if (!line[i].equals("")) {
							int code = getRandomCode(alreadyUsedCodeSet);
							Institute institute = new Institute();
							institute.setName(line[i].replaceAll("1\\)\\(억원\\)", "").replaceAll("\\(억원\\)", ""));
							institute.setCode("bnk" + code);
							instituteList.add(institute);

							alreadyUsedCodeSet.add(code);
						}
					}

					firstLine = false;
				}
			}
			
			// 은행 이름, 은행 코드 전체 데이터 초기화 후 저장
			instituteRepository.deleteAll();
			instituteRepository.saveAll(instituteList);
			
			// 금융기관 별 공급 현황 전체 데이터 초기화 후 저장
			supportRepository.deleteAll();
			supportRepository.saveAll(supportList);

		} catch (Exception e) {
			System.out.println("[UploadService] upload unknown exception");
			e.printStackTrace();
			throw new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
	
	/**
	 * 은행코드 뒤에 난수를 입력하기 위한 Random 숫자 생성
	 * @param randomCodeSet
	 * @return
	 */
	private Integer getRandomCode(Set<Integer> randomCodeSet) {
		Random random = new Random();
		int code = random.nextInt(1000);
		while (randomCodeSet.contains(code)) {
			code = random.nextInt(1000);
		}
		randomCodeSet.add(code);
		return code;
	}
}
