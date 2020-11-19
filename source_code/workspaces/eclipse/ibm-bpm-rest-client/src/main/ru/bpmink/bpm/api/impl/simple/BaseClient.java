package ru.bpmink.bpm.api.impl.simple;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.database.DatabaseUtil;
import com.google.common.base.MoreObjects;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.io.gzip.Gzip;

import ru.bpmink.bpm.model.auth.Authentication;
import ru.bpmink.bpm.model.common.Describable;
import ru.bpmink.bpm.model.common.RestRootEntity;
import ru.bpmink.util.Utils;

/**
 * Base parent class, which contains some configuration constants and common
 * methods.
 */
@SuppressWarnings("WeakerAccess")
abstract class BaseClient {

	private static Logger logger = LoggerFactory.getLogger(BaseClient.class.getName());

	private static final RequestConfig DEFAULT_CONFIG = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT)
			.setTargetPreferredAuthSchemes(Collections.singletonList(AuthSchemes.BASIC))
			.setProxyPreferredAuthSchemes(Collections.singletonList(AuthSchemes.BASIC)).build();

	private static final String JSON_CONTENT_TYPE = "application/json";
	private static final String FORM_URL_CONTENT_TYPE = "application/x-www-form-urlencoded";
	protected static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	protected static final String DEFAULT_SEPARATOR = ",";
	protected static final int DEFAULT_TIMEOUT = 120000; // 120 seconds
	protected static final String BPM_CSRF_TOKEN = "BPMCSRFToken"; // 120 seconds

	protected void setRequestTimeOut(HttpRequestBase request, int timeOut) {
		RequestConfig requestConfig = RequestConfig.copy(DEFAULT_CONFIG).setSocketTimeout(timeOut)
				.setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut).build();
		request.setConfig(requestConfig);
	}

	protected void setHeadersGet(HttpRequestBase request) {
		request.addHeader(HttpHeaders.CONTENT_TYPE, JSON_CONTENT_TYPE);
		request.setHeader(HttpHeaders.ACCEPT, JSON_CONTENT_TYPE);
	}

	protected void setHeadersPut(HttpRequestBase request) {
		// The same one
		setHeadersGet(request);
	}

	protected void setHeadersPost(HttpRequestBase request) {
		request.addHeader(HttpHeaders.CONTENT_TYPE, JSON_CONTENT_TYPE);
		request.setHeader(HttpHeaders.ACCEPT, JSON_CONTENT_TYPE);
	}

	@SuppressWarnings("SameParameterValue")
	protected void logRequest(HttpRequest request, String body) {
		logger.info("Prepared Request for uri: " + request.getRequestLine().getUri());
		logger.info("HTTP Request headers: " + Arrays.toString(request.getAllHeaders()));
		if (logger.isDebugEnabled()) {
			logger.debug("Request body: " + body);
		}
	}

	protected void logResponse(HttpResponse response, String body) {
		logger.info("HTTP Response had a " + response.getStatusLine().getStatusCode() + " status code.");
		logger.info("Reason: " + response.getStatusLine().getReasonPhrase());
		if (logger.isDebugEnabled()) {
			logger.debug("Response headers: " + Arrays.toString(response.getAllHeaders()));
			logger.debug("Response: " + response);
			logger.debug("Response body: " + body);
		}
	}

	// GET - CsrfToken
	protected JsonElement makeGet(@Nonnull HttpClient httpClient, @Nullable HttpContext httpContext,
			@Nonnull URI endpoint, @Nonnull String csrfToken) {
		try {

			HttpGet request = new HttpGet(endpoint);
			setRequestTimeOut(request, DEFAULT_TIMEOUT);
			setHeadersGet(request);
//			request.setHeader(BPM_CSRF_TOKEN, csrfToken);

			logRequest(request, null);

			HttpResponse response = httpClient.execute(request, httpContext);

			InputStream content = response.getEntity().getContent();
			InputStreamReader reader = new InputStreamReader(content);
			JsonElement result = new Gson().fromJson(reader, JsonElement.class);

//			File file = new File("processDetail1.json");
//		    FileUtils.copyInputStreamToFile(content, file);
//			String result = Utils.inputStreamToString(content);			

			request.releaseConnection();

			return result;
		} catch (IOException e) {
			logger.error("Can't update Entity object from Server with uri " + endpoint, e);
			e.printStackTrace();
			throw new RuntimeException("Can't update Entity object from Server with uri " + endpoint, e);
		}
	}

	protected <T extends Describable> RestRootEntity<T> makeGet(@Nonnull HttpClient httpClient,
			@Nullable HttpContext httpContext, @Nonnull URI endpoint, @Nonnull TypeToken<RestRootEntity<T>> typeToken) {
		try {
			HttpGet request = new HttpGet(endpoint);
			setRequestTimeOut(request, DEFAULT_TIMEOUT);
			setHeadersGet(request);

			logRequest(request, null);

			HttpResponse response = httpClient.execute(request, httpContext);
//            logger.info("Response: " + Utils.inputStreamToString(response.getEntity().getContent()));
			RestRootEntity<T> entity = makeEntity(response, typeToken);
			request.releaseConnection();

			return entity;
		} catch (IOException e) {
			logger.error("Can't get Entity object from Server with uri: " + endpoint, e);
			e.printStackTrace();
			throw new RuntimeException("Can't get Entity object from Server with uri: " + endpoint, e);
		}
	}

	protected <T extends Describable> RestRootEntity<T> makeGet(@Nonnull HttpClient httpClient,
			@Nullable HttpContext httpContext, @Nonnull URI endpoint, @Nonnull TypeToken<RestRootEntity<T>> typeToken,
			@Nonnull String csrfToken) {
		try {
			HttpGet request = new HttpGet(endpoint);
			setRequestTimeOut(request, DEFAULT_TIMEOUT);
			setHeadersGet(request);
			request.setHeader(BPM_CSRF_TOKEN, csrfToken);

			logRequest(request, null);

			HttpResponse response = httpClient.execute(request, httpContext);
//            logger.info("Response: " + Utils.inputStreamToString(response.getEntity().getContent()));
//			RestRootEntity<T> entity = makeEntity(response, typeToken);

			InputStream content = response.getEntity().getContent();
			System.out.println(Gzip.compress(ByteStreams.toByteArray(content)).length);

//			File file = new File("processDetail.json");						 
//		    FileUtils.copyInputStreamToFile(content, file);
//		    
			Reader reader = new InputStreamReader(content);
			JsonElement tree = new Gson().fromJson(reader, JsonElement.class);
			String string = tree.getAsJsonObject().get("variables").getAsJsonObject().toString();
			logger.info(string);
			InputStream inputStream = IOUtils.toInputStream(string);
			Reader variables = new InputStreamReader(inputStream);
//			DatabaseUtil.CallProcedure("PROCESS_INSTANCE.INSERT_BPM_INSTANCES", variables);

			request.releaseConnection();

			return null;
		} catch (IOException e) {
			logger.error("Can't get Entity object from Server with uri: " + endpoint, e);
			e.printStackTrace();
			throw new RuntimeException("Can't get Entity object from Server with uri: " + endpoint, e);
		}
	}

	/**
	 * Process {@literal POST} request for given endpoint with specified in
	 * {@literal httpClient} and {@literal httpContext} configuration. Given
	 * {@literal typeToken} determines the response entity generic type.
	 *
	 * @param httpClient  {@link org.apache.http.client.HttpClient} instance,
	 *                    configured for request.
	 * @param httpContext {@link org.apache.http.protocol.HttpContext} instance,
	 *                    configured for request. It's optional parameter. If
	 *                    {@literal null} passed as {@literal httpContext}, default
	 *                    instance of
	 *                    {@link org.apache.http.protocol.BasicHttpContext} will be
	 *                    created.
	 * @param endpoint    Endpoint uri {@link java.net.URI}
	 * @param typeToken   Represents a generic type that will be returned as
	 *                    {@link ru.bpmink.bpm.model.common.RestRootEntity} generic
	 *                    parameter.
	 * @param <T>         {@link ru.bpmink.bpm.model.common.RestRootEntity} generic
	 *                    parameter class.
	 * @return {@link ru.bpmink.bpm.model.common.RestRootEntity} instance, which
	 *         holds entity of specified by {@literal <T>} type.
	 */
	protected <T extends Describable> RestRootEntity<T> makePost(@Nonnull HttpClient httpClient,
			@Nullable HttpContext httpContext, @Nonnull URI endpoint, @Nonnull TypeToken<RestRootEntity<T>> typeToken) {
		try {
			HttpPost request = new HttpPost(endpoint);

			JsonObject jo = new JsonObject();
			jo.addProperty("refresh-groups", false);
			jo.addProperty("requested-lifetime", 7200);
			request.setEntity(new StringEntity(jo.toString()));

			setRequestTimeOut(request, DEFAULT_TIMEOUT);
			setHeadersPost(request);

			logRequest(request, null);

			HttpResponse response = httpClient.execute(request, httpContext);
			RestRootEntity<T> entity = makeEntity(response, typeToken);

			request.releaseConnection();

			return entity;
		} catch (IOException e) {
			logger.error("Can't update Entity object from Server with uri " + endpoint, e);
			e.printStackTrace();
			throw new RuntimeException("Can't update Entity object from Server with uri " + endpoint, e);
		}
	}

	protected Authentication login(@Nonnull HttpClient httpClient, @Nullable HttpContext httpContext,
			@Nonnull URI endpoint, @Nonnull TypeToken<RestRootEntity<Authentication>> typeToken) {
		try {
			HttpPost request = new HttpPost(endpoint);

			JsonObject jo = new JsonObject();
			jo.addProperty("refresh-groups", false);
			jo.addProperty("requested-lifetime", 7200);
			request.setEntity(new StringEntity(jo.toString()));

			setRequestTimeOut(request, DEFAULT_TIMEOUT);
			setHeadersPost(request);

			logRequest(request, null);

			HttpResponse response = httpClient.execute(request, httpContext);
//            RestRootEntity<T> entity = makeEntity(response, typeToken);

			Gson gson = new GsonBuilder().setDateFormat(DATE_TIME_FORMAT).create();
			String body = Utils.inputStreamToString(response.getEntity().getContent());

			Authentication entity = gson.fromJson(body, Authentication.class);

			request.releaseConnection();

			return entity;
		} catch (IOException e) {
			logger.error("Can't update Entity object from Server with uri " + endpoint, e);
			e.printStackTrace();
			throw new RuntimeException("Can't update Entity object from Server with uri " + endpoint, e);
		}
	}

	protected String setEnvironmentVariables(@Nonnull HttpClient httpClient, @Nullable HttpContext httpContext,
			@Nonnull URI endpoint, @Nonnull String csrfToken, @Nonnull String env_vars) {
		try {
			HttpPost request = new HttpPost(endpoint);

			request.setEntity(new StringEntity(env_vars));

			setRequestTimeOut(request, DEFAULT_TIMEOUT);
			setHeadersPost(request);
			request.setHeader(BPM_CSRF_TOKEN, csrfToken);

			logRequest(request, null);

			HttpResponse response = httpClient.execute(request, httpContext);
//            RestRootEntity<T> entity = makeEntity(response, typeToken);

			String result = Utils.inputStreamToString(response.getEntity().getContent());

			request.releaseConnection();

			return result;
		} catch (IOException e) {
			logger.error("Can't update Entity object from Server with uri " + endpoint, e);
			e.printStackTrace();
			throw new RuntimeException("Can't update Entity object from Server with uri " + endpoint, e);
		}
	}

	protected String retrieveEnvironmentVariable(@Nonnull HttpClient httpClient, @Nullable HttpContext httpContext,
			@Nonnull URI endpoint, @Nonnull String csrfToken) {
		try {

			HttpGet request = new HttpGet(endpoint);
			setRequestTimeOut(request, DEFAULT_TIMEOUT);
			setHeadersGet(request);
			request.setHeader(BPM_CSRF_TOKEN, csrfToken);

			// logRequest(request, null);

			HttpResponse response = httpClient.execute(request, httpContext);
			String result = Utils.inputStreamToString(response.getEntity().getContent());

			request.releaseConnection();

			return result;
		} catch (IOException e) {
			logger.error("Can't update Entity object from Server with uri " + endpoint, e);
			e.printStackTrace();
			throw new RuntimeException("Can't update Entity object from Server with uri " + endpoint, e);
		}
	}

	protected String syncEnvironmentVariable(@Nonnull HttpClient httpClient, @Nullable HttpContext httpContext,
			@Nonnull URI endpoint, @Nonnull String csrfToken) {
		try {

			HttpPost request = new HttpPost(endpoint);
			setRequestTimeOut(request, DEFAULT_TIMEOUT);
			setHeadersPost(request);
			request.setHeader(BPM_CSRF_TOKEN, csrfToken);

			logRequest(request, null);

			HttpResponse response = httpClient.execute(request, httpContext);
			String result = Utils.inputStreamToString(response.getEntity().getContent());

			request.releaseConnection();

			return result;
		} catch (IOException e) {
			logger.error("Can't update Entity object from Server with uri " + endpoint, e);
			e.printStackTrace();
			throw new RuntimeException("Can't update Entity object from Server with uri " + endpoint, e);
		}
	}

	protected String retrieveSnapshots(@Nonnull HttpClient httpClient, @Nullable HttpContext httpContext,
			@Nonnull URI endpoint, @Nonnull String csrfToken) {
		try {

			HttpGet request = new HttpGet(endpoint);
			setRequestTimeOut(request, DEFAULT_TIMEOUT);
			setHeadersGet(request);
			request.setHeader(BPM_CSRF_TOKEN, csrfToken);

			logRequest(request, null);

			HttpResponse response = httpClient.execute(request, httpContext);

			InputStream content = response.getEntity().getContent();

			String result = Utils.inputStreamToString(content);

			request.releaseConnection();

			return result;
		} catch (IOException e) {
			logger.error("Can't update Entity object from Server with uri " + endpoint, e);
			e.printStackTrace();
			throw new RuntimeException("Can't update Entity object from Server with uri " + endpoint, e);
		}
	}

	protected String checkSyncProgress(@Nonnull HttpClient httpClient, @Nullable HttpContext httpContext,
			@Nonnull URI endpoint, @Nonnull String csrfToken) {
		try {

			HttpGet request = new HttpGet(endpoint);
			setRequestTimeOut(request, DEFAULT_TIMEOUT);
			setHeadersGet(request);
			request.setHeader(BPM_CSRF_TOKEN, csrfToken);

			logRequest(request, null);

			HttpResponse response = httpClient.execute(request, httpContext);
			String result = Utils.inputStreamToString(response.getEntity().getContent());

			request.releaseConnection();

			return result;
		} catch (IOException e) {
			logger.error("Can't update Entity object from Server with uri " + endpoint, e);
			e.printStackTrace();
			throw new RuntimeException("Can't update Entity object from Server with uri " + endpoint, e);
		}
	}

	protected String makeGetWithToken(@Nonnull HttpClient httpClient, @Nullable HttpContext httpContext,
			@Nonnull URI endpoint, @Nonnull String csrfToken) {
		try {

			HttpGet request = new HttpGet(endpoint);
			setRequestTimeOut(request, DEFAULT_TIMEOUT);
			setHeadersGet(request);
			request.setHeader(BPM_CSRF_TOKEN, csrfToken);

			logRequest(request, null);

			HttpResponse response = httpClient.execute(request, httpContext);
			String result = Utils.inputStreamToString(response.getEntity().getContent());

			request.releaseConnection();

			return result;
		} catch (IOException e) {
			logger.error("Can't update Entity object from Server with uri " + endpoint, e);
			e.printStackTrace();
			throw new RuntimeException("Can't update Entity object from Server with uri " + endpoint, e);
		}
	}

	/**
	 * Create {@link ru.bpmink.bpm.model.common.RestRootEntity} instance, which
	 * holds entity of specified by {@literal typeToken} parameter.
	 *
	 * @param response  Http response {@link org.apache.http.HttpResponse}, obtained
	 *                  by {@code HttpClient.execute(HttpRequest, HttpContext)}
	 *                  call.
	 * @param typeToken Represents a generic type that will be returned as
	 *                  {@link ru.bpmink.bpm.model.common.RestRootEntity} generic
	 *                  parameter.
	 * @param <T>       {@link ru.bpmink.bpm.model.common.RestRootEntity} generic
	 *                  parameter class.
	 * @return {@link ru.bpmink.bpm.model.common.RestRootEntity} instance, which
	 *         holds entity of specified by {@literal <T>} type.
	 */
	private <T extends Describable> RestRootEntity<T> makeEntity(@Nonnull HttpResponse response,
			@Nonnull TypeToken<RestRootEntity<T>> typeToken) {
		try {
			Gson gson = new GsonBuilder().setDateFormat(DATE_TIME_FORMAT).create();

			InputStream content = response.getEntity().getContent();

			File file = new File("processDetail.json");

			FileUtils.copyInputStreamToFile(content, file);

			Reader reader = new InputStreamReader(content);

			DatabaseUtil.CallProcedure("PROCESS_INSTANCE.INSERT_BPM_INSTANCES", reader);

			String body = Utils.inputStreamToString(content);
			logger.info("RESPONSE: " + body);
			logResponse(response, body);

			RestRootEntity<T> entity = gson.fromJson(reader, typeToken.getType());
			// In case of system / communication errors body will be empty.
			// I.e. if provided credentials was wrong we will receive 401 code -
			// Unauthorized.
			// So we set correct status code in to the entity, and it's payload will be
			// empty.
			entity = MoreObjects.firstNonNull(entity, new RestRootEntity<T>());

			// In error case status field updated by 'error' value. So just replace it by
			// actual status code.
			entity.setStatus(String.valueOf(response.getStatusLine().getStatusCode()));
			return entity;
		} catch (IOException e) {
			logger.error("Can't create response Entity object with type: " + typeToken.getType(), e);
			e.printStackTrace();
			throw new RuntimeException("Can't create response Entity object with type: " + typeToken.getType(), e);
		}
	}

}
