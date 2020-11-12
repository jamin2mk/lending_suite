package ru.bpmink.bpm.api.impl.simple;

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;
import org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID;

import com.google.gson.JsonElement;

import ru.bpmink.bpm.model.other.exposed.Item;
import ru.bpmink.util.SafeUriBuilder;

public class LogClientImpl extends BaseClient implements LogClient {

	private final URI rootUri;
	private final HttpClient httpClient;
	private final HttpContext httpContext;
	
	private static final String SEARCH_INSTANCE_ENDPOINT = "rest/bpm/wle/v1/processes/search";
	private static final String INSTANCE_DETAIL_ENDPOINT = "rest/bpm/federated/bfm/v1/process";
	
	private static final String PROJECT_FILTER_QUERY = "projectFilter";
	private static final String MODIFIED_AFTER_QUERY = "modifiedAfter";
	private static final String MODIFIED_BEFORE_QUERY = "modifiedBefore";	
	private static final String SYSTEM_ID_QUERY = "systemID";

	LogClientImpl(URI rootUri, HttpClient httpClient, HttpContext httpContext) {
		this.httpClient = httpClient;
		this.rootUri = rootUri;
		this.httpContext = httpContext;
	}

	LogClientImpl(URI rootUri, HttpClient httpClient) {
		this(rootUri, httpClient, null);
	}

	@Override
	public JsonElement getInstances(String hCsrfToken, String qModifiedAfter, String qModifiedBefore, String qProjectFilter,
			String qUserFilter, String qSearchFilter, String qStatusFilter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonElement getInstances(String hCsrfToken, String qModifiedAfter, String qModifiedBefore,
			String qProjectFilter) {
		
//		URI uri = new SafeUriBuilder(rootUri).addPath(SEARCH_INSTANCE_ENDPOINT).addParameter(MODIFIED_AFTER_QUERY, qModifiedAfter).addParameter(MODIFIED_BEFORE_QUERY, qModifiedBefore).build();
		URI uri = new SafeUriBuilder(rootUri).addPath(SEARCH_INSTANCE_ENDPOINT).addParameter(PROJECT_FILTER_QUERY, qProjectFilter).addParameter("statusFilter", "Completed").build();
		return makeGet(httpClient, httpContext, uri, hCsrfToken);
	}

	@Override
	public JsonElement getInstanceDetail(String hCsrfToken, String pPiid, String qSystemId) {
		
		URI uri = new SafeUriBuilder(rootUri).addPath(INSTANCE_DETAIL_ENDPOINT).addPath(pPiid).addParameter(SYSTEM_ID_QUERY, qSystemId).build();
		return makeGet(httpClient, httpContext, uri, hCsrfToken);
	}
}
