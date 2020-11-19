package com.test;

import java.text.ParseException;
import java.util.Date;

import com.common.Const;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helper.DateUtil;
import com.helper.InstanceHelper;

import ru.bpmink.bpm.api.operation.Operation;

public class TestMain {

	public static void main(String[] args) {

//		testSearchInst();
//		testGetInstDetail();
//		testInsertInst();
//		testInsertInstList();

	}

	public static void testInsertInst() {

		String tk = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDU2MTQ2NDAsInN1YiI6ImJwbWFkbWluIn0.lzVmnbfv8sU5pICD0StMSkLLZ9g9d6-xDS74e9grrXA";
		JsonElement entity = Operation.getInstanceDetail(tk, "24214", Const.SYSTEM_ID_STRING);

		try {
			JsonObject result = InstanceHelper.insertInst(entity.getAsJsonObject());
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static JsonArray testSearchInst() {
//		String tk = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDU2MTQ2NDAsInN1YiI6ImJwbWFkbWluIn0.lzVmnbfv8sU5pICD0StMSkLLZ9g9d6-xDS74e9grrXA";
		JsonArray instanceList = null;

		try {
			Date dateAfter = DateUtil.getDateFromString("2020-11-19T00:00:00Z", Const.BPM_DATE_FORMAT_STRING);
			Date dateBefore = DateUtil.getDateFromString("2020-11-19T23:59:59Z", Const.BPM_DATE_FORMAT_STRING);
			instanceList = Operation.searchInstances("", dateAfter, dateBefore, "LOSBIDV", "", null, null);

			System.out.println(instanceList);
			System.out.println("Size: " + instanceList.size());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return instanceList;
	}

	public static void testGetInstDetail() {
		JsonElement result = InstanceHelper.getInstDetail("24214", Const.SYSTEM_ID_STRING);
		System.out.println(result.getAsJsonObject().get("piid").getAsString());
	}

	public static void testInsertInstList() {
		JsonArray instanceList = testSearchInst();
		InstanceHelper.insertInstList(instanceList);
	}
}
