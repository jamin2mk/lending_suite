package com.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GsonUtil {

	public static JsonObject getObjectByName(JsonObject jObject, String fieldName) throws Exception {

		/* Result for Returning */
		JsonObject jsonObject = null;

		try {
			/* Get JsonElement */
			JsonElement jsonElement = jObject.get(fieldName);

			/*
			 * Check type of ELEMENT for MAKE SURE it is an OBJECT THROW Exception if it is
			 * NOT OBJECT
			 */
			if (jsonElement.isJsonObject()) {
				jsonObject = jsonElement.getAsJsonObject();

			} else if (jsonElement.isJsonArray()) {
				throw new Exception(String.format("'%s' is an Array, not Object.", fieldName));

			} else if (jsonElement.isJsonPrimitive()) {
				throw new Exception(String.format("'%s' is an Primitive, not Object.", fieldName));
			}

		} catch (NullPointerException e) {
			throw new Exception(String.format("Can't find the property '%s' in object.", fieldName));
		}

		return jsonObject;
	}

	public static JsonArray getArrayByName(JsonObject jObject, String fieldName) throws Exception {

		/* Result for Returning */
		JsonArray jsonArray = null;

		try {
			/* Get JsonElement */
			JsonElement jsonElement = jObject.get(fieldName);

			/*
			 * Check type of ELEMENT for MAKE SURE it is an OBJECT THROW Exception if it is
			 * NOT OBJECT
			 */
			if (jsonElement.isJsonArray()) {
				jsonArray = jsonElement.getAsJsonArray();

			} else if (jsonElement.isJsonObject()) {
				throw new Exception(String.format("'%s' is an Object, not Array.", fieldName));

			} else if (jsonElement.isJsonPrimitive()) {
				throw new Exception(String.format("'%s' is an Primitive, not Array.", fieldName));
			}
		} catch (NullPointerException e) {
			throw new Exception(String.format("Can't find the property '%s' in object.", fieldName));
		}

		return jsonArray;
	}

	public static String getPrimitiveValue(JsonObject jObject, String fieldName, boolean isRequired) throws Exception {

		/* Result for Returning */
		String value = null;

		if (jObject.isJsonNull()) {
			throw new Exception(String.format("Can't get value of '%s'. Cause by the object is null.", fieldName));

		} else if (jObject.size() == 0) {
			throw new Exception(String.format(
					"Can't get value of '%s'. Cause by the object doesn't contain any property. It like {}.",
					fieldName));
		}

		try {
			/* Get JsonElement */
			JsonElement jsonElement = jObject.get(fieldName);

			/*
			 * Check type of ELEMENT for MAKE SURE it is an OBJECT THROW Exception if it is
			 * NOT OBJECT
			 */
			if (jsonElement.isJsonNull()) {
				if (isRequired) {
					throw new Exception(String.format("'%s' is null.", fieldName));
				}
			} else if (jsonElement.isJsonPrimitive()) {
				value = jsonElement.getAsString();

				if (isRequired && value.trim().isEmpty()) {
					throw new Exception(String.format("'%s' must be not empty.", fieldName));
				}

			} else if (jsonElement.isJsonArray()) {
				throw new Exception(String.format("'%s' is an Array, not Primitive Type.", fieldName));

			} else if (jsonElement.isJsonObject()) {
				throw new Exception(String.format("'%s' is an Object, not Primitive Type.", fieldName));
			}

		} catch (NullPointerException e) {
			throw new Exception(String.format("Can't find the property '%s' in object.", fieldName));
		}

		return value;
	}

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
