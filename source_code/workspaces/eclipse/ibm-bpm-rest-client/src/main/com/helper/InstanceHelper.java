package com.helper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import ru.bpmink.bpm.api.operation.LogOperation;

public class InstanceHelper {

	public static final String SYSTEM_ID = "1c19c7dc-b7bd-48e7-9d0b-632b857c8992";
	
	public static JsonElement insertInst(JsonElement instanceInfo) {
		
		JsonElement result = null;
		
		
		return result;
	}
	
	public static JsonElement insertInstList(JsonArray instanceList) {
		
		JsonElement result = null;
		String csrfToken = "";
		
		for (JsonElement item : instanceList) {
			
			String instanceId = item.getAsJsonObject().get("piid").getAsString();
			JsonElement instanceInfo = getInstDetail(instanceId, csrfToken);
			insertInst(instanceInfo);			
		}		
		
		return result;
	}
	
	public static JsonElement getInstDetail(String instanceId, String csrfToken) {
		JsonElement result = null;
		
		LogOperation operation = new LogOperation();
		JsonElement instanceDetail = operation.getInstanceDetail(csrfToken, instanceId, SYSTEM_ID);
		
		return result;
	}
}
