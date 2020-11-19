package com.helper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.common.Const;
import com.database.DatabaseUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ru.bpmink.bpm.api.operation.Operation;

public class InstanceHelper {

	public static JsonObject insertInstMaster(JsonElement basicInfo) {

		JsonObject result = null;

		InputStream inputStream = new ByteArrayInputStream(basicInfo.toString().getBytes());
		Reader iData = new InputStreamReader(inputStream);

		result = DatabaseUtil.CallProcedure(Const.INSERT_DTL_BPM_INST_MASTER, iData);

		return result;
	}

	public static JsonObject insertInstBo(String instMasterCode, JsonElement instanceData) {

		JsonObject result = null;

		InputStream inputStream = new ByteArrayInputStream(instanceData.toString().getBytes());
		Reader iData = new InputStreamReader(inputStream);

		result = DatabaseUtil.CallProcedure(Const.INSERT_DTL_BPM_INST_BO, instMasterCode, iData);

		return result;
	}

	private static JsonObject insertTask(String instMasterCode, JsonObject taskData, JsonObject modifiedInfo) {

		JsonObject result = null;

		InputStream taskDataStream = new ByteArrayInputStream(taskData.toString().getBytes());
		Reader iData = new InputStreamReader(taskDataStream);

		Reader iInfo = null;
		if (modifiedInfo != null) {
			InputStream modifiedInfoStream = new ByteArrayInputStream(modifiedInfo.toString().getBytes());
			iInfo = new InputStreamReader(modifiedInfoStream);
		}
		result = DatabaseUtil.CallProcedure(Const.INSERT_DTL_BPM_INST_TASK, instMasterCode, iData, iInfo);

		return result;
	}

	public static JsonObject insertInst(JsonObject instanceInfo) throws Exception {

		JsonObject result = null;
		String logId = "";
		String instMasterCode;

		/** 1.1 get basic info of instance **/
		JsonObject basicInfo = getInstanceInfo(instanceInfo);
		System.out.println(basicInfo);

		/* 1.2 insert insertInstMaster */
		JsonObject insertInstResult = insertInstMaster(basicInfo);

		logId = GsonUtil.getPrimitiveValue(insertInstResult, Const.LOG_ID, true);
		if (!logId.equalsIgnoreCase("0")) {
			result = insertInstResult;
			return result;
		}

		instMasterCode = GsonUtil.getPrimitiveValue(insertInstResult, Const.INST_MASTER_CODE, true);

		/** 2.1 get data of instance **/
		JsonObject instanceData = getInstanceData(instanceInfo);

		/* 2.2 insert insertInstBo */
		JsonObject insertBoResult = insertInstBo(instMasterCode, instanceData);

		logId = GsonUtil.getPrimitiveValue(insertBoResult, Const.LOG_ID, true);
		if (!logId.equalsIgnoreCase("0")) {
			result = insertBoResult;
			return result;
		}

		/** 3.1 get task of instance **/
		JsonArray tasks = getTaskInfo(instanceInfo);

		/* 3.2 insert insertInstTask */
		for (JsonElement jsonElement : tasks) {

			JsonObject task = jsonElement.getAsJsonObject();

			// get taskInfo
			JsonObject taskInfo = new JsonObject();
			taskInfo.addProperty("instMasterCode", instMasterCode);
			taskInfo.addProperty("tkiid", GsonUtil.getPrimitiveValue(task, "tkiid", true));

			// get taskData
			JsonObject data = GsonUtil.getObjectByName(task, "data");
			JsonObject taskData = GsonUtil.getObjectByName(data, "variables");

			// get modifiedInfo
			JsonObject modifiedInfo = null;

			JsonObject insertTaskResult = insertTask(taskInfo.toString(), taskData, modifiedInfo);

			logId = GsonUtil.getPrimitiveValue(insertTaskResult, Const.LOG_ID, true);
			result = insertTaskResult;
		}

		return result;
	}

	public static JsonElement insertInstList(JsonArray instanceList) {

		JsonElement result = null;
		String csrfToken = "";

		for (JsonElement item : instanceList) {

			String instanceId = item.getAsJsonObject().get("piid").getAsString();
			JsonElement instanceInfo = getInstDetail(instanceId, csrfToken);

			try {
				result = insertInst(instanceInfo.getAsJsonObject());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("PULL INSTANCES COMPLETELY !!");
		return result;
	}

	private static JsonObject getInstanceInfo(JsonObject instanceInfo) throws Exception {

		JsonObject basicInfo = new JsonObject();

		basicInfo.addProperty("piid", GsonUtil.getPrimitiveValue(instanceInfo, "piid", false));
		basicInfo.addProperty("state", GsonUtil.getPrimitiveValue(instanceInfo, "state", false));

		basicInfo.addProperty("creationTime", GsonUtil.getPrimitiveValue(instanceInfo, "creationTime", false));
		basicInfo.addProperty("dueDate", GsonUtil.getPrimitiveValue(instanceInfo, "dueDate", false));
		basicInfo.addProperty("lastModificationTime",
				GsonUtil.getPrimitiveValue(instanceInfo, "lastModificationTime", false));

		basicInfo.addProperty("snapshotID", GsonUtil.getPrimitiveValue(instanceInfo, "snapshotID", false));
		basicInfo.addProperty("branchID", GsonUtil.getPrimitiveValue(instanceInfo, "branchID", false));
		basicInfo.addProperty("processAppID", GsonUtil.getPrimitiveValue(instanceInfo, "processAppID", false));
		basicInfo.addProperty("processTemplateName",
				GsonUtil.getPrimitiveValue(instanceInfo, "processTemplateName", false));
		basicInfo.addProperty("starterId", GsonUtil.getPrimitiveValue(instanceInfo, "starterId", false));

		return basicInfo;
	}

	private static JsonObject getInstanceData(JsonObject instanceInfo) throws Exception {

		JsonObject instanceData = GsonUtil.getObjectByName(instanceInfo, "variables");
		return instanceData;
	}

	private static JsonArray getTaskInfo(JsonObject instanceInfo) throws Exception {

		JsonArray taskInfo = GsonUtil.getArrayByName(instanceInfo, "tasks");
		return taskInfo;
	}

	public static JsonObject getInstDetail(String csrfToken, String instanceId) {

		JsonObject instanceDetail = Operation.getInstanceDetail(csrfToken, instanceId, Const.SYSTEM_ID_STRING);
		return instanceDetail;
	}

	public static JsonArray researchWithInstanceId(JsonArray instanceList, String instanceId) throws Exception {
		JsonArray result = new JsonArray();

		for (JsonElement jsonElement : instanceList) {

			JsonObject item = jsonElement.getAsJsonObject();
			String piid = GsonUtil.getPrimitiveValue(item, "piid", true);

			if (piid.contains(instanceId)) {
				System.out.println(piid);
				result.add(item);
			}
		}
		return result;
	}

	public static JsonArray researchWithCaseId(JsonArray instanceList, String caseId) throws Exception {
		JsonArray result = new JsonArray();

		for (JsonElement jsonElement : instanceList) {

			JsonObject item = jsonElement.getAsJsonObject();

			String piid = GsonUtil.getPrimitiveValue(item, "piid", true);
			String processName = GsonUtil.getPrimitiveValue(item, "bpdName", true);

			JsonObject instDetail = getInstDetail(piid, "").getAsJsonObject();
			JsonObject variables = GsonUtil.getObjectByName(instDetail, "variables");

			if (processName.equalsIgnoreCase("SALOP PROCESS")) {

				JsonObject data = GsonUtil.getObjectByName(variables, "data");
				JsonObject target = GsonUtil.getObjectByName(data, "LOS_BPM_INFORMATION");
				String code = GsonUtil.getPrimitiveValue(target, "CODE", true);

				if (caseId.equalsIgnoreCase(code)) {
					result.add(item);
				}
			} else if (processName.equalsIgnoreCase("LOS AA")) {
				JsonObject data = GsonUtil.getObjectByName(variables, "data_AA_Fac");
				JsonObject target = GsonUtil.getObjectByName(data, "INFO_PROCESS");
				String code = GsonUtil.getPrimitiveValue(target, "CODE", true);

				if (caseId.equalsIgnoreCase(code)) {
					result.add(item);
				}
			}
		}

		return result;
	}

	public static JsonArray researchWithInstanceIdAndCaseId(JsonArray instanceList, String instanceId, String caseId)
			throws Exception {
		JsonArray result = new JsonArray();

		for (JsonElement jsonElement : instanceList) {

			JsonObject item = jsonElement.getAsJsonObject();

			String piid = GsonUtil.getPrimitiveValue(item, "piid", true);
			String processName = GsonUtil.getPrimitiveValue(item, "bpdName", true);

			if (piid.contains(instanceId)) {

				JsonObject instDetail = getInstDetail(piid, "").getAsJsonObject();
				JsonObject variables = GsonUtil.getObjectByName(instDetail, "variables");

				if (processName.equalsIgnoreCase("SALOP PROCESS")) {

					JsonObject data = GsonUtil.getObjectByName(variables, "data");
					JsonObject target = GsonUtil.getObjectByName(data, "LOS_BPM_INFORMATION");
					String code = GsonUtil.getPrimitiveValue(target, "CODE", true);

					if (caseId.equalsIgnoreCase(code)) {
						result.add(item);
					}
				} else if (processName.equalsIgnoreCase("LOS AA")) {
					JsonObject data = GsonUtil.getObjectByName(variables, "data_AA_Fac");
					JsonObject target = GsonUtil.getObjectByName(data, "INFO_PROCESS");
					String code = GsonUtil.getPrimitiveValue(target, "CODE", true);

					if (caseId.equalsIgnoreCase(code)) {
						result.add(item);
					}
				}
			}
		}
		return result;
	}
}
