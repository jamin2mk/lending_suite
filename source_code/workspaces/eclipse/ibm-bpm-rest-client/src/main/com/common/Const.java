package com.common;

import java.text.SimpleDateFormat;

public class Const {
	public static final String INSERT_INSTANCE_SP = "PROCESS_INSTANCE.INSERT_INSTANCE";

	public static final String INSERT_DTL_BPM_INST_MASTER = "PROCESS_INSTANCE.INSERT_DTL_BPM_INST_MASTER";
	public static final String INSERT_DTL_BPM_INST_BO = "PROCESS_INSTANCE.INSERT_DTL_BPM_INST_BO";
	public static final String INSERT_DTL_BPM_INST_TASK = "PROCESS_INSTANCE.INSERT_DTL_BPM_INST_TASK";

	public static final String ERROR_INFO = "errorInfo";
	public static final String ERROR_CODE = "errorCode";
	public static final String MESSAGE = "message";
	public static final String STACK_TRACE = "stackTrace";
	
	public static final String SUCCESS_CODE = "00";
	public static final String SUCCESS_MESSAGE = "Success";
	
	/* DB */
	public static final String LOG_ID = "LOG_ID";
	public static final String LOG_MESSAGE = "LOG_MESSAGE";
	public static final String INST_MASTER_CODE = "INST_MASTER_CODE";
	
//	public static final String BPM_DATE_FORMAT_STRING = "YYYY-MM-DD'T'HH24:MI:SS'Z'";
	
	public static final String SEARCH_INSTANCE_ENDPOINT = "rest/bpm/wle/v1/processes/search";
	public static final String INSTANCE_DETAIL_ENDPOINT = "rest/bpm/federated/bfm/v1/process";
	
	public static final String SYSTEM_ID_STRING = "1c19c7dc-b7bd-48e7-9d0b-632b857c8992";
	public static final String SYSTEM_ID_QUERY = "systemID";	
	
	public static final String PROJECT_FILTER_QUERY = "projectFilter";
	public static final String SEARCH_FILTER_QUERY = "searchFilter";
	public static final String USER_FILTER_QUERY = "userFilter";
	public static final String STATUS_FILTER_QUERY = "statusFilter";
	public static final String MODIFIED_AFTER_QUERY = "modifiedAfter";
	public static final String MODIFIED_BEFORE_QUERY = "modifiedBefore";
	
	public static final String BPM_DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final SimpleDateFormat BPM_DATE_FORMAT = new SimpleDateFormat(BPM_DATE_FORMAT_STRING);
}
