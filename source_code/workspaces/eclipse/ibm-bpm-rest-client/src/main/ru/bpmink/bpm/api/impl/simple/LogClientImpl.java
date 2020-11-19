package ru.bpmink.bpm.api.impl.simple;

import java.net.URI;
import java.util.Date;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.common.Const;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ru.bpmink.util.SafeUriBuilder;

public class LogClientImpl extends BaseClient implements LogClient {

	private final URI rootUri;
	private final HttpClient httpClient;
	private final HttpContext httpContext;

	LogClientImpl(URI rootUri, HttpClient httpClient, HttpContext httpContext) {
		this.httpClient = httpClient;
		this.rootUri = rootUri;
		this.httpContext = httpContext;
	}

	LogClientImpl(URI rootUri, HttpClient httpClient) {
		this(rootUri, httpClient, null);
	}

	@Override
	public JsonArray getInstances(String hCsrfToken, Date qModifiedAfter, Date qModifiedBefore, String qProjectFilter,
			String qSeachFilter, String qUserFilter, String qStatusFilter) {

		SafeUriBuilder safeUriBuilder = new SafeUriBuilder(rootUri).addPath(Const.SEARCH_INSTANCE_ENDPOINT);

		if (qModifiedAfter != null) {
			safeUriBuilder.addParameter(Const.MODIFIED_AFTER_QUERY, qModifiedAfter, Const.BPM_DATE_FORMAT);
		}
		if (qModifiedBefore != null) {
			safeUriBuilder.addParameter(Const.MODIFIED_BEFORE_QUERY, qModifiedBefore, Const.BPM_DATE_FORMAT);
		}
		if (qProjectFilter != null && !qProjectFilter.trim().isEmpty()) {
			safeUriBuilder.addParameter(Const.PROJECT_FILTER_QUERY, qProjectFilter);
		}
		if (qSeachFilter != null && !qSeachFilter.trim().isEmpty()) {
			safeUriBuilder.addParameter(Const.SEARCH_FILTER_QUERY, qSeachFilter);
		}
		if (qUserFilter != null && !qUserFilter.trim().isEmpty()) {
			safeUriBuilder.addParameter(Const.USER_FILTER_QUERY, qUserFilter);
		}
		if (qStatusFilter != null && !qStatusFilter.trim().isEmpty()) {
			safeUriBuilder.addParameter(Const.STATUS_FILTER_QUERY, qStatusFilter);
		}

		URI uri = safeUriBuilder.build();
		JsonElement entity = makeGet(httpClient, httpContext, uri, hCsrfToken);

		JsonObject data = entity.getAsJsonObject().get("data").getAsJsonObject();
		JsonArray instanceList = data.get("processes").getAsJsonArray();

		return instanceList;
	}

	@Override
	public JsonObject getInstanceDetail(String hCsrfToken, String pPiid, String qSystemId) {

		URI uri = new SafeUriBuilder(rootUri).addPath(Const.INSTANCE_DETAIL_ENDPOINT).addPath(pPiid)
				.addParameter(Const.SYSTEM_ID_QUERY, qSystemId).build();

		JsonObject entity = makeGet(httpClient, httpContext, uri, hCsrfToken).getAsJsonObject();
		return entity;
	}
}
