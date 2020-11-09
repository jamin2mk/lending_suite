package ru.bpmink.bpm.api.impl.simple;

import java.net.URI;

import javax.annotation.concurrent.Immutable;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.google.gson.reflect.TypeToken;

import ru.bpmink.bpm.api.client.AuthenticationClient;
import ru.bpmink.bpm.model.auth.Authentication;
import ru.bpmink.bpm.model.common.RestRootEntity;

@Immutable
final class AuthenticationClientImpl extends BaseClient implements AuthenticationClient {

	private final URI rootUri;
	private final HttpClient httpClient;
	private final HttpContext httpContext;

	AuthenticationClientImpl(URI rootUri, HttpClient httpClient, HttpContext httpContext) {
		super();
		this.rootUri = rootUri;
		this.httpClient = httpClient;
		this.httpContext = httpContext;
	}
	
	AuthenticationClientImpl(URI rootUri, HttpClient httpClient) {
        this(rootUri, httpClient, null);
    }

	@Override
	public Authentication login() {
		return login(httpClient, httpContext, rootUri, new TypeToken<RestRootEntity<Authentication>>() {});
	}

	@Override
	public String retrieveEnvironmentVariables(String csrfToken) {		
		return retrieveEnvironmentVariable(httpClient, httpContext, rootUri, csrfToken);
	}

	@Override
	public String syncEnvironmentVariables(String csrfToken) {
		return syncEnvironmentVariable(httpClient, httpContext, rootUri, csrfToken);
	}

	@Override
	public String retrieveSnapshots(String csrfToken) {
		return retrieveSnapshots(httpClient, httpContext, rootUri, csrfToken);
	}
	
	@Override
	public String setEnvironmentVariables(String csrfToken, String env_vars) {
		return setEnvironmentVariables(httpClient, httpContext, rootUri, csrfToken, env_vars);
	}

	@Override
	public String getWithToken(String csrfToken) {
		return makeGetWithToken(httpClient, httpContext, rootUri, csrfToken);
	}

//	@Override
//	public String checkSyncProgress(String csrfToken) {
//		return chec
//	}
}
