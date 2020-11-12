package ru.bpmink.bpm.api.impl.simple;

import com.google.gson.JsonElement;

public interface LogClient {
	
	JsonElement getInstances(String hCsrfToken, String qModifiedAfter, String qModifiedBefore, String qProjectFilter, String qUserFilter,
			String qSearchFilter, String qStatusFilter);
	
	JsonElement getInstances(String hCsrfToken, String qModifiedAfter, String qModifiedBefore, String qProjectFilter);

	JsonElement getInstanceDetail(String hCsrfToken, String pPiid, String qSystemId);
}
