package ru.bpmink.bpm.api.impl.simple;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.gson.reflect.TypeToken;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

import ru.bpmink.bpm.api.client.QueryClient;
import ru.bpmink.bpm.model.common.RestRootEntity;
import ru.bpmink.bpm.model.query.InteractionFilter;
import ru.bpmink.bpm.model.query.Query;
import ru.bpmink.bpm.model.query.QueryAttribute;
import ru.bpmink.bpm.model.query.QueryAttributes;
import ru.bpmink.bpm.model.query.QueryKind;
import ru.bpmink.bpm.model.query.QueryList;
import ru.bpmink.bpm.model.query.QueryResultSet;
import ru.bpmink.bpm.model.query.QueryResultSetCount;
import ru.bpmink.bpm.model.query.SortAttribute;
import ru.bpmink.util.SafeUriBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.net.URI;
import java.util.List;

@Immutable
final class QueryClientImpl extends BaseClient implements QueryClient {

	private final URI rootUri;
	private final HttpClient httpClient;
	private final HttpContext httpContext;

	// Endpoint additional paths
	private static final String QUERIES = "queries";
	private static final String QUERY = "query";

	// Request parameters constants
	private static final String PROCESS_APP_NAME = "processAppName";
	private static final String KIND = "kind";
	private static final String CONTENT = "content";
	private static final String COUNT = "count";
	private static final String ATTRIBUTES = "attributes";
	private static final String SELECTED_ATTRIBUTES = "selectedAttributes";
	private static final String SORT_ATTRIBUTES = "sortAttributes";
	private static final String INTERACTION_FILTER = "interactionFilter";
	private static final String FILTER_BY_CURRENT_USER = "filterByCurrentUser";
	private static final String SIZE = "size";

	QueryClientImpl(URI rootUri, HttpClient httpClient, HttpContext httpContext) {
		this.httpClient = httpClient;
		this.rootUri = rootUri;
		this.httpContext = httpContext;
	}

	QueryClientImpl(URI rootUri, HttpClient httpClient) {
		this(rootUri, httpClient, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestRootEntity<QueryList> listQueries() {
		return this.listQueries(null, null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestRootEntity<QueryList> listQueries(@Nullable String processAppName, @Nullable QueryKind kind,
			@Nullable List<QueryAttribute> content) {

		SafeUriBuilder uri = new SafeUriBuilder(rootUri).addPath(QUERIES);

		if (processAppName != null) {
			uri.addParameter(PROCESS_APP_NAME, processAppName);
		}
		if (kind != null) {
			uri.addParameter(KIND, kind.name());
		}
		if (content != null) {
			uri.addParameter(CONTENT, Joiner.on(DEFAULT_SEPARATOR).skipNulls()
					.join(Collections2.transform(content, new ContentFunction())));
		}

		return makeGet(httpClient, httpContext, uri.build(), new TypeToken<RestRootEntity<QueryList>>() {
		});
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws IllegalArgumentException {@inheritDoc}
	 */
	@Override
	public RestRootEntity<QueryResultSet> queryEntities(@Nonnull Query query,
			@Nullable List<QueryAttribute> selectedAttributes, @Nullable InteractionFilter interactionFilter,
			@Nullable String processAppName, @Nullable List<SortAttribute> sortAttributes, @Nullable Integer size,
			@Nullable Boolean filterByCurrentUser) {

		query = Args.notNull(query, "Search query");
		String querySearch = Args.notNull(query.getName(), "Search query name");

		SafeUriBuilder uri = new SafeUriBuilder(rootUri).addPath(QUERY).addPath(querySearch);

		if (selectedAttributes != null) {
			uri.addParameter(SELECTED_ATTRIBUTES, Joiner.on(DEFAULT_SEPARATOR).skipNulls()
					.join(Collections2.transform(selectedAttributes, new NameFunction())));
		}
		if (interactionFilter != null) {
			uri.addParameter(INTERACTION_FILTER, interactionFilter.name());
		}
		if (processAppName != null) {
			uri.addParameter(PROCESS_APP_NAME, processAppName);
		}
		if (sortAttributes != null) {
			uri.addParameter(SORT_ATTRIBUTES, Joiner.on(DEFAULT_SEPARATOR).skipNulls()
					.join(Collections2.transform(sortAttributes, new SortFunction())));
		}
		if (size != null) {
			uri.addParameter(SIZE, size);
		}
		if (filterByCurrentUser != null) {
			uri.addParameter(FILTER_BY_CURRENT_USER, filterByCurrentUser);
		}

		return makeGet(httpClient, httpContext, uri.build(), new TypeToken<RestRootEntity<QueryResultSet>>() {
		});
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws IllegalArgumentException {@inheritDoc}
	 */
	@Override
	public RestRootEntity<QueryResultSetCount> queryEntitiesCount(@Nonnull Query query,
			@Nullable InteractionFilter interactionFilter, @Nullable String processAppName,
			@Nullable Boolean filterByCurrentUser) {

		query = Args.notNull(query, "Search query");
		String querySearch = Args.notNull(query.getName(), "Search query name");

		SafeUriBuilder uri = new SafeUriBuilder(rootUri).addPath(QUERY).addPath(querySearch).addPath(COUNT);

		if (interactionFilter != null) {
			uri.addParameter(INTERACTION_FILTER, interactionFilter.name());
		}
		if (processAppName != null) {
			uri.addParameter(PROCESS_APP_NAME, processAppName);
		}
		if (filterByCurrentUser != null) {
			uri.addParameter(FILTER_BY_CURRENT_USER, filterByCurrentUser);
		}

		return makeGet(httpClient, httpContext, uri.build(), new TypeToken<RestRootEntity<QueryResultSetCount>>() {
		});
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws IllegalArgumentException {@inheritDoc}
	 */
	@Override
	public RestRootEntity<QueryAttributes> queryAttributes(@Nonnull Query query, String processAppName) {
		query = Args.notNull(query, "Search query");
		String querySearch = Args.notNull(query.getName(), "Search query name");

		SafeUriBuilder uri = new SafeUriBuilder(rootUri).addPath(QUERY).addPath(querySearch).addPath(ATTRIBUTES);

		if (processAppName != null) {
			uri.addParameter(PROCESS_APP_NAME, processAppName);
		}

		return makeGet(httpClient, httpContext, uri.build(), new TypeToken<RestRootEntity<QueryAttributes>>() {
		});
	}

	private static class ContentFunction implements Function<QueryAttribute, String> {

		@Override
		public String apply(QueryAttribute input) {
			return input.getContent();
		}
	}

	private static class NameFunction implements Function<QueryAttribute, String> {

		@Override
		public String apply(QueryAttribute input) {
			return input.getName();
		}
	}

	private static class SortFunction implements Function<SortAttribute, String> {

		@Override
		public String apply(SortAttribute input) {
			return input.getName() + " " + input.getSortOrder().name();
		}
	}

	@Override
	public RestRootEntity<QueryResultSetCount> queryEntitiesCount() {

		
		SafeUriBuilder uri = new SafeUriBuilder(rootUri).addPath(COUNT);

		
		return makeGet(httpClient, httpContext, uri.build(), new TypeToken<RestRootEntity<QueryResultSetCount>>() {
		});
	}

}
