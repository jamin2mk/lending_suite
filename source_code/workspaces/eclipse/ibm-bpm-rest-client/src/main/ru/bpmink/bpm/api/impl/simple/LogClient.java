package ru.bpmink.bpm.api.impl.simple;

import java.util.Date;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface LogClient {

	JsonArray getInstances(String hCsrfToken, Date qModifiedAfter, Date qModifiedBefore, String qProjectFilter,
			String qSeachFilter, String qUserFilter, String qStatusFilter);

	JsonObject getInstanceDetail(String hCsrfToken, String pPiid, String qSystemId);
}
