package ru.bpmink.bpm.api.client;

import ru.bpmink.bpm.model.auth.Authentication;
import ru.bpmink.bpm.model.common.RestRootEntity;

public interface AuthenticationClient {
	
	Authentication login();
	String retrieveEnvironmentVariables(String csrfToken);
	String syncEnvironmentVariables(String csrfToken);
	String retrieveSnapshots(String csrfToken);
	String setEnvironmentVariables(String csrfToken, String env_vars);
//	String checkSyncProgress(String csrfToken);
	String getWithToken(String csrfToken);
}
