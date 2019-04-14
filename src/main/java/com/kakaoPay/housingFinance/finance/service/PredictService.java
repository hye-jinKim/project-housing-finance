package com.kakaoPay.housingFinance.finance.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.kakaoPay.housingFinance.common.exception.RestApiException;
import com.kakaoPay.housingFinance.finance.model.Support;
import com.kakaoPay.housingFinance.finance.repository.InstituteRepository;
import com.kakaoPay.housingFinance.finance.repository.SupportRepository;

import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;

@Service
public class PredictService {

	@Autowired
	private SupportRepository supportRepository;

	@Autowired
	private InstituteRepository instituteRepository;

	public Map<String, Object> getPrediction(String bank, int month) throws Exception {

		String path = new ClassPathResource("").getURI().getPath();
		String filePath = path + "files/prediction.arff";
		FileWriter fw = null;
		String nextLine = System.getProperty("line.separator");
		List<Support> list = supportRepository.findAllInnerCode();

		String bankCodeString = StringUtils.replace(instituteRepository.findCodeByName(bank), "bnk", "");
		if (StringUtils.isBlank(bankCodeString)) {
			throw new RestApiException(HttpStatus.BAD_REQUEST);
		}

		int code = Integer.parseInt(bankCodeString);
		int year = 2018;

		try {
			fw = new FileWriter(new File(filePath));

			fw.write("@relation prediction");
			fw.write(nextLine);
			fw.write(nextLine);
			fw.write("@attribute bank numeric");
			fw.write(nextLine);
			fw.write("@attribute year numeric");
			fw.write(nextLine);
			fw.write("@attribute month {1,2,3,4,5,6,7,8,9,10,11,12}");
			fw.write(nextLine);
			fw.write("@attribute amount numeric");
			fw.write(nextLine);
			fw.write(nextLine);
			fw.write("@data");
			fw.write(nextLine);

			for (Support support : list) {
				fw.write(StringUtils.replace(support.getBank(), "bnk", ""));
				fw.write(",");
				fw.write(String.valueOf(support.getYear()));
				fw.write(",");
				fw.write(String.valueOf(support.getMonth()));
				fw.write(",");
				fw.write(String.valueOf(support.getAmount()));
				fw.write(nextLine);
			}

			fw.write(String.valueOf(code));
			fw.write(",");
			fw.write(String.valueOf(year));
			fw.write(",");
			fw.write(String.valueOf(month));
			fw.write(",");
			fw.write("?");

			fw.flush();
			fw.close();

			// load data
			Instances data = new Instances(new BufferedReader(new FileReader(filePath)));
			data.setClassIndex(data.numAttributes() - 1);

			// build model
			LinearRegression model = new LinearRegression();
			model.buildClassifier(data);

			// classify the last instance
			Instance myHouse = data.lastInstance();
			int predictAmount = (int) model.classifyInstance(myHouse);

			return makeResultMap(code, year, month, predictAmount);
		} catch (RestApiException e) {
			throw e;
		} catch (Exception e) {
			System.out.println("[PredictService] getPrediction unknown exception");
			e.printStackTrace();
			throw new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Map<String, Object> makeResultMap(int code, int year, int month, int predictAmount) {
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("bank", "bnk" + code);
		resultMap.put("year", year);
		resultMap.put("month", month);
		resultMap.put("amount", predictAmount);

		return resultMap;
	}
}
