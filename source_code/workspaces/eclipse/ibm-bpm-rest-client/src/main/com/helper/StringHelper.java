package com.helper;

public class StringHelper {

	public static boolean hasContent(String param) {

		boolean result = false;

		if (param != null && !param.trim().isEmpty()) {
			result = true;
		}

		return result;
	}
}
