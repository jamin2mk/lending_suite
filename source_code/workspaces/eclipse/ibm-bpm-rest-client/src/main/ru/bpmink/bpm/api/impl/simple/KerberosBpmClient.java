package ru.bpmink.bpm.api.impl.simple;

import com.google.common.collect.Lists;
import com.google.common.io.Closeables;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import ru.bpmink.bpm.api.client.AuthenticationClient;
import ru.bpmink.bpm.api.client.BpmClient;
import ru.bpmink.bpm.api.client.ExposedClient;
import ru.bpmink.bpm.api.client.ProcessAppsClient;
import ru.bpmink.bpm.api.client.ProcessClient;
import ru.bpmink.bpm.api.client.QueryClient;
import ru.bpmink.bpm.api.client.ServiceClient;
import ru.bpmink.bpm.api.client.TaskClient;
import ru.bpmink.util.SafeUriBuilder;

import javax.annotation.concurrent.Immutable;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;

/**
 * Simple implementation of {@link ru.bpmink.bpm.api.client.BpmClient} which
 * supports {@link org.apache.http.impl.auth.KerberosScheme} authentication.
 */
//TODO: Broken implementation. Rewrite and retest needed.
@Immutable
@SuppressWarnings("deprecation")
@SuppressFBWarnings("JCIP_FIELD_ISNT_FINAL_IN_IMMUTABLE_CLASS")
public final class KerberosBpmClient implements BpmClient {

	private static final int TOTAL_CONN = 20;
	private static final int ROUTE_CONN = 10;

	private static final String ROOT_ENDPOINT = "ops";
	private static final String SYSTEM_ENDPOINT = "system";
	private static final String LOGIN_ENDPOINT = "login";
	private static final String EXPOSED_ENDPOINT = "exposed";
	private static final String PROCESS_ENDPOINT = "process";
	private static final String TASK_ENDPOINT = "task";
	private static final String SERVICE_ENDPOINT = "service";
	private static final String TASKS_QUERY_ENDPOINT = "tasks";
	private static final String TASKS_TEMPLATE_QUERY_ENDPOINT = "taskTemplates";
	private static final String PROCESS_QUERY_ENDPOINT = "processes";
	private static final String PROCESS_APPS_ENDPOINT = "processApps";

	private ExposedClient exposedClient;
	private ProcessClient processClient;
	private TaskClient taskClient;
	private ServiceClient serviceClient;
	private ProcessAppsClient processAppsClient;
	private AuthenticationClient authenticationClient;

	private QueryClient taskQueryClient;
	private QueryClient taskTemplateQueryClient;
	private QueryClient processQueryClient;

	private static Logger logger = LoggerFactory.getLogger(KerberosBpmClient.class.getName());
	private final CloseableHttpClient httpClient;
	private final URI rootUri;

	/**
	 * Creates instance of {@link ru.bpmink.bpm.api.impl.simple.KerberosBpmClient}.
	 *
	 * @param serverUri is a absolute server host/port path.
	 * @param user      is a login by which the actions will be performed.
	 * @param password  is a user password.
	 * @param domain    is an identification string that defines a realm of
	 *                  administrative autonomy, authority or control.
	 * @param kdc       key distribution center ({@literal KDC}) is part of a
	 *                  cryptosystem intended to reduce the risks
	 */
	public KerberosBpmClient(URI serverUri, String user, String password, String domain, String kdc) {
		logger.info("Start creating bpm client.");
		this.rootUri = new SafeUriBuilder(serverUri).addPath(ROOT_ENDPOINT).build();
		this.httpClient = createClient(user, password, domain, kdc);
		logger.info("Bpm client created.");
	}

	private CloseableHttpClient createClient(String user, String password, String domain, String kdc) {
		return new KerberosHttpClient(user, password, domain, kdc);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExposedClient getExposedClient() {
		if (exposedClient == null) {
			final URI uri = new SafeUriBuilder(rootUri).addPath(EXPOSED_ENDPOINT).build();
			exposedClient = new ExposedClientImpl(uri, httpClient);
		}
		return exposedClient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProcessClient getProcessClient() {
		if (processClient == null) {
			final URI uri = new SafeUriBuilder(rootUri).addPath(PROCESS_ENDPOINT).build();
			processClient = new ProcessClientImpl(uri, httpClient);
		}
		return processClient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaskClient getTaskClient() {
		if (taskClient == null) {
			final URI uri = new SafeUriBuilder(rootUri).addPath(TASK_ENDPOINT).build();
			taskClient = new TaskClientImpl(uri, httpClient);
		}
		return taskClient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServiceClient getServiceClient() {
		if (serviceClient == null) {
			final URI uri = new SafeUriBuilder(rootUri).addPath(SERVICE_ENDPOINT).build();
			serviceClient = new ServiceClientImpl(uri, httpClient);
		}
		return serviceClient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProcessAppsClient getProcessAppsClient() {
		if (processAppsClient == null) {
			final URI uri = new SafeUriBuilder(rootUri).addPath(PROCESS_APPS_ENDPOINT).build();
			processAppsClient = new ProcessAppsClientImpl(uri, httpClient);
		}
		return processAppsClient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthenticationClient getAuthenticationClient() {
		if (authenticationClient == null) {
			final URI uri = new SafeUriBuilder(rootUri).addPath(SYSTEM_ENDPOINT).addPath(LOGIN_ENDPOINT).build();
			authenticationClient = new AuthenticationClientImpl(uri, httpClient, null);
		}
		return authenticationClient;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryClient getTaskQueryClient() {
		if (taskQueryClient == null) {
			final URI uri = new SafeUriBuilder(rootUri).addPath(TASKS_QUERY_ENDPOINT).build();
			taskQueryClient = new QueryClientImpl(uri, httpClient);
		}
		return taskQueryClient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryClient getTaskTemplateQueryClient() {
		if (taskTemplateQueryClient == null) {
			final URI uri = new SafeUriBuilder(rootUri).addPath(TASKS_TEMPLATE_QUERY_ENDPOINT).build();
			taskTemplateQueryClient = new QueryClientImpl(uri, httpClient);
		}
		return taskTemplateQueryClient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryClient getProcessQueryClient() {
		if (processQueryClient == null) {
			final URI uri = new SafeUriBuilder(rootUri).addPath(PROCESS_QUERY_ENDPOINT).build();
			processQueryClient = new QueryClientImpl(uri, httpClient);
		}
		return processQueryClient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		Closeables.close(httpClient, true);
	}

	/**
	 * Creates a temporary configuration. {@code
	 * krb5.conf [libdefaults] default_realm = <domain> [realms] snb.ch = {kdc =
	 * <kdc> admin_server = <kdc>} }
	 */
	private static File createKrb5Configuration(String domain, String kdc) throws IOException {
		File tempFile = File.createTempFile("krb5", "kdc");
		ArrayList<String> lines = Lists.newArrayList();
		lines.add("[libdefaults]");
		lines.add("\tdefault_realm = " + domain);
		lines.add("[realms]");
		lines.add("\t" + domain + " = {");
		lines.add("\t\tkdc = " + kdc);
		lines.add("\t\tadmin_server = " + kdc);
		lines.add("\t}");

		try (Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8");
				PrintWriter printWriter = new PrintWriter(writer)) {

			for (String line : lines) {
				printWriter.println(line);
			}
		}
		return tempFile;
	}

	private static File createLoginConfiguration() throws IOException {
		File tempFile = File.createTempFile("krb5", "login");
		ArrayList<String> lines = Lists.newArrayList();
		lines.add("krb5.login { com.sun.security.auth.module.Krb5LoginModule required doNotPrompt=false debug=true "
				+ "useTicketCache=false; };");
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8");
				PrintWriter printWriter = new PrintWriter(writer)) {

			for (String line : lines) {
				printWriter.println(line);
			}
		}
		return tempFile;
	}

	private static class KerberosCallBackHandler implements CallbackHandler {

		private final String user;
		private final String password;

		KerberosCallBackHandler(String user, String password) {
			this.user = user;
			this.password = password;
		}

		public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			for (Callback callback : callbacks) {
				if (callback instanceof NameCallback) {
					NameCallback nc = (NameCallback) callback;
					nc.setName(user);
				} else if (callback instanceof PasswordCallback) {
					PasswordCallback pc = (PasswordCallback) callback;
					pc.setPassword(password.toCharArray());
				} else {
					throw new UnsupportedCallbackException(callback, "Unknown Callback");
				}
			}
		}
	}

	private class KerberosHttpClient extends CloseableHttpClient {

		private final CloseableHttpClient client;
		private final LoginContext loginContext;
		private final HttpClientContext httpContext;
		private static final boolean SKIP_PORT_AT_KERBEROS_DATABASE_LOOKUP = true;

		private KerberosHttpClient(String user, String password, String domain, String kdc) {
			try {
				File krb5Config = createKrb5Configuration(domain, kdc);
				File loginConfig = createLoginConfiguration();

				System.setProperty("java.security.auth.login.config", loginConfig.toURI().toString());
				System.setProperty("java.security.krb5.conf", krb5Config.toURI().toString());
				System.setProperty("sun.security.krb5.debug", "false");
				// Change this property to true, if you want debug output.
				System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");

				RegistryBuilder<AuthSchemeProvider> registryBuilder = RegistryBuilder.create();
				String id = AuthSchemes.SPNEGO;
				SPNegoSchemeFactory schemeFactory = new SPNegoSchemeFactory(SKIP_PORT_AT_KERBEROS_DATABASE_LOOKUP);
				Lookup<AuthSchemeProvider> authSchemeRegistry = registryBuilder.register(id, schemeFactory).build();

				client = HttpClients.custom().setDefaultAuthSchemeRegistry(authSchemeRegistry)
						.setConnectionManager(createConnectionManager()).build();
				httpContext = getHttpContext();
				loginContext = getLoginContext(user, password);

				// without it, authentication will be failed.
				final Subject subject = loginContext.getSubject();
				final HttpGet get = new HttpGet(KerberosBpmClient.this.rootUri);
				Subject.doAs(subject, this.privilegedExecute(get, httpContext));
			} catch (Exception e) {
				logger.error("Can't create Kerberos client!");
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		private HttpClientConnectionManager createConnectionManager() {
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
			cm.setMaxTotal(TOTAL_CONN);
			cm.setDefaultMaxPerRoute(ROUTE_CONN);
			return cm;
		}

		private LoginContext getLoginContext(String user, String password) throws LoginException {
			LoginContext context = new LoginContext("krb5.login", new KerberosCallBackHandler(user, password));
			context.login();
			return context;
		}

		private HttpClientContext getHttpContext() {
			HttpClientContext context = HttpClientContext.create();
			BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			Credentials credentials = new Credentials() {

				public String getPassword() {
					return null;
				}

				public Principal getUserPrincipal() {
					return null;
				}
			};
			credentialsProvider.setCredentials(new AuthScope(null, -1, null), credentials);
			context.setCredentialsProvider(credentialsProvider);
			return context;
		}

		@Override
		@Deprecated
		public HttpParams getParams() {
			return client.getParams();
		}

		@Override
		@Deprecated
		public ClientConnectionManager getConnectionManager() {
			return client.getConnectionManager();
		}

		@Override
		public void close() throws IOException {
			client.close();
		}

		@Override
		protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) {
			throw new UnsupportedOperationException("doExecute(HttpHost target, HttpRequest request, "
					+ "HttpContext context) unsupported for this client.");
		}

		@Override
		public CloseableHttpResponse execute(HttpHost target, HttpRequest request, HttpContext context)
				throws IOException {
			return client.execute(target, request, context);
		}

		@Override
		public CloseableHttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException {
			return client.execute(request, context);
		}

		@Override
		public CloseableHttpResponse execute(final HttpUriRequest request) throws IOException {
			return client.execute(request);
		}

		@Override
		public CloseableHttpResponse execute(HttpHost target, HttpRequest request) throws IOException {
			return client.execute(target, request);
		}

		@Override
		public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException {
			return client.execute(request, responseHandler);
		}

		@Override
		public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context)
				throws IOException {
			return client.execute(request, responseHandler, context);
		}

		@Override
		public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler)
				throws IOException {
			return client.execute(target, request, responseHandler);
		}

		@Override
		public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler,
				HttpContext context) throws IOException {
			return client.execute(target, request, responseHandler, context);
		}

		private PrivilegedAction<CloseableHttpResponse> privilegedExecute(final HttpUriRequest request,
				final HttpContext context) {
			return new PrivilegedAction<CloseableHttpResponse>() {

				@Override
				public CloseableHttpResponse run() {
					try {
						return client.execute(request, context);
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				}
			};
		}
	}

	@Override
	public AuthenticationClient getAuthenticationClient2(String container, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthenticationClient getAuthenticationClient3(String container, String version, String targetVersion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthenticationClient getAuthenticationClient4(String container) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthenticationClient getAuthenticationClient5(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthenticationClient getAuthenticationClient6(String container) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessAppsClient getClient() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LogClient getLogClient() {
		// TODO Auto-generated method stub
		return null;
	}

}
