package ru.bpmink.bpm.api.impl.simple;

import com.google.common.io.Closeables;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
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

import java.io.IOException;
import java.net.URI;
import javax.annotation.concurrent.Immutable;

/**
 * Simple implementation of {@link ru.bpmink.bpm.api.client.BpmClient} which supports
 * {@link org.apache.http.impl.auth.BasicScheme} authentication.
 */
@Immutable
@SuppressFBWarnings("JCIP_FIELD_ISNT_FINAL_IN_IMMUTABLE_CLASS")
public final class SimpleBpmClient implements BpmClient {

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

    private static Logger logger = LoggerFactory.getLogger(SimpleBpmClient.class.getName());
    private final CloseableHttpClient httpClient;
    private final URI rootUri;
    private HttpClientContext httpContext;

    /**
     * Creates instance of {@link ru.bpmink.bpm.api.impl.simple.SimpleBpmClient}.
     *
     * @param serverUri is a absolute server host/port path.
     * @param user      is a login by which the actions will be performed.
     * @param password  is a user password.
     */
    public SimpleBpmClient(URI serverUri, String user, String password) {
        logger.info("Start creating bpm client.");
        this.rootUri = new SafeUriBuilder(serverUri).addPath(ROOT_ENDPOINT).build();
        this.httpClient = createClient(user, password);
        logger.info("Bpm client created.");
    }

    private CloseableHttpClient createClient(String user, String password) {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(TOTAL_CONN);
        cm.setDefaultMaxPerRoute(ROUTE_CONN);

        logger.info("Pooling connection manager created.");

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
        logger.info("Default credentials provider created.");

        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();

        authCache.put(new HttpHost(rootUri.getHost(), rootUri.getPort(), rootUri.getScheme()), basicAuth);
        logger.info("Auth cache created.");

        httpContext = HttpClientContext.create();
        httpContext.setCredentialsProvider(credentialsProvider);
        httpContext.setAuthCache(authCache);
        logger.info("HttpContext filled with Auth cache.");

        return HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).setConnectionManager(cm)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExposedClient getExposedClient() {
        if (exposedClient == null) {
            final URI uri = new SafeUriBuilder(rootUri).addPath(EXPOSED_ENDPOINT).build();
            exposedClient = new ExposedClientImpl(uri, httpClient, httpContext);
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
            processClient = new ProcessClientImpl(uri, httpClient, httpContext);
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
            taskClient = new TaskClientImpl(uri, httpClient, httpContext);
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
            serviceClient = new ServiceClientImpl(uri, httpClient, httpContext);
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
            processAppsClient = new ProcessAppsClientImpl(uri, httpClient, httpContext);
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
            taskQueryClient = new QueryClientImpl(uri, httpClient, httpContext);
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
            taskTemplateQueryClient = new QueryClientImpl(uri, httpClient, httpContext);
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
            processQueryClient = new QueryClientImpl(uri, httpClient, httpContext);
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
