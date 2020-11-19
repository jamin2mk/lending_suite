package ru.bpmink.bpm.api.operation;

import java.util.Date;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helper.InstanceHelper;
import com.helper.StringHelper;

public class Tracking {

	public static JsonArray searchInstances(String hCsrfToken, Date qModifiedAfter, Date qModifiedBefore,
			String qProjectFilter, String qSeachFilter, String qUserFilter, String qStatusFilter, String qInstanceId,
			String caseId) {

		JsonArray instances = Operation.searchInstances(hCsrfToken, qModifiedAfter, qModifiedBefore, qProjectFilter,
				qSeachFilter, qUserFilter, qStatusFilter);

		try {
			if (StringHelper.hasContent(qInstanceId) && StringHelper.hasContent(caseId)) {
				instances = InstanceHelper.researchWithInstanceIdAndCaseId(instances, qInstanceId, caseId); //search both 'instanceId' & 'caseId'
			} else {				
				if (StringHelper.hasContent(qInstanceId)) {
					instances = InstanceHelper.researchWithInstanceId(instances, qInstanceId); //search by 'instanceId'
				} 
				else if (StringHelper.hasContent(caseId)) {
					instances = InstanceHelper.researchWithCaseId(instances, caseId); //search both 'instanceId' & 'caseId'
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return instances;
	}

	public static JsonObject getInstanceDetail(String csrfToken, String instanceId) {

		JsonObject instanceDetail = InstanceHelper.getInstDetail(csrfToken, instanceId);
		return instanceDetail;
	}
}
