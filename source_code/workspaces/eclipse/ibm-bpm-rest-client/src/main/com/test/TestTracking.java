package com.test;

import java.text.ParseException;
import java.util.Date;

import com.common.Const;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helper.DateUtil;

import ru.bpmink.bpm.api.operation.Tracking;

public class TestTracking {

	public static void main(String[] args) {

//		testSearchInst();
		testGetInstDetail();
//		testInsertInst();
//		testInsertInstList();

	}

	public static void testSearchInst() {
//		String tk = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDU2MTQ2NDAsInN1YiI6ImJwbWFkbWluIn0.lzVmnbfv8sU5pICD0StMSkLLZ9g9d6-xDS74e9grrXA";
		try {
			Date qModifiedAfter = DateUtil.getDateFromString("2020-11-10T00:00:00Z", Const.BPM_DATE_FORMAT_STRING);
			Date dateBefore = DateUtil.getDateFromString("2020-11-19T23:59:59Z", Const.BPM_DATE_FORMAT_STRING);
			String qProjectFilter = "LOSBIDV";
			String qSeachFilter = null;
			String qUserFilter = null;
			String qStatusFilter = null;
			String qInstanceId = null;
			String caseId = null;

			JsonArray instanceList = Tracking.searchInstances("", qModifiedAfter, dateBefore, qProjectFilter,
					qSeachFilter, qUserFilter, qStatusFilter, qInstanceId, caseId);

			System.out.println(instanceList);
			System.out.println("Size: " + instanceList.size());

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static void testGetInstDetail() {
		JsonObject result = Tracking.getInstanceDetail("", "24253");
		System.out.println(result.get("name").getAsString());
	}
}
