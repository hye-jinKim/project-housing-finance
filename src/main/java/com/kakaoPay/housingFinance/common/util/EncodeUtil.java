package com.kakaoPay.housingFinance.common.util;

import org.apache.tomcat.util.codec.binary.Base64;

public class EncodeUtil {

	public static String encodeBase64(String value) {
		byte[] encodedBytes = Base64.encodeBase64(value.getBytes());
		return new String(encodedBytes);
	}
}
