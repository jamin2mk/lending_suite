package ru.bpmink.bpm.api.impl.simple;

public interface LogClient {
	
	String getInstances(String hCsrfToken, String qModifiedAfter, String qModifiedBefore, String qProjectFilter, String qUserFilter,
			String qSearchFilter, String qStatusFilter);
	
	String getInstances(String hCsrfToken, String qModifiedAfter, String qModifiedBefore, String qProjectFilter);

	String getInstanceDetail(String hCsrfToken, String pPiid, String qSystemId);
}
