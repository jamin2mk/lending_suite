package com.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static Date getDateFromString(String date, String format) throws ParseException {

		Date dateResult = null;

		if (date != null & !date.trim().isEmpty() && !date.equalsIgnoreCase("0")) {
			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

				dateResult = simpleDateFormat.parse(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return dateResult;
	}

}
