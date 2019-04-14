package com.kakaoPay.housingFinance.common.util;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * object to map converter
 * @author hyejin
 */
public class MapConvertUtil {

	private static ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * object to map
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(Object object) {
		try {
			Map<String, Object> instance = MAPPER.convertValue(object, Map.class);
			return instance;
		} catch (Exception e) {
			System.out.println("[MapConvertUtil] converting error! object : " + object);
			e.printStackTrace();
			return Collections.EMPTY_MAP;
		}
	}
}
