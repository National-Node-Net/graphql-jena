// SPDX-License-Identifier: Apache-2.0
// Originally developed by Telicent Ltd.; subsequently adapted, enhanced, and maintained by the National Digital Twin Programme.
/*
 *  Copyright (c) Telicent Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/*
 *  Modifications made by the National Digital Twin Programme (NDTP)
 *  © Crown Copyright 2026. This work has been developed by the National Digital Twin Programme
 *  and is legally attributed to the UK's Department for Business, Innovation, Science and Trade (BIST) as the governing entity.
 */

package uk.gov.dbt.ndtp.jena.graphql.fetchers.ianode.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import uk.gov.dbt.ndtp.jena.graphql.execution.ianode.graph.IANodeExecutionContext;
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.IANodeGraphSchema;
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.models.SearchType;
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.models.IANodeGraphNode;
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.models.IANodeSearchResults;
import uk.gov.dbt.ndtp.jena.graphql.server.model.GraphQLOverHttp;
import uk.gov.dbt.ndtp.servlet.auth.jwt.JwtHttpConstants;
import uk.gov.dbt.ndtp.servlet.auth.jwt.verifier.aws.AwsConstants;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.system.Txn;
import org.apache.jena.web.HttpSC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An abstract GraphQL fetcher for Search based queries
 *
 * @param <T> Returned model type
 */
public abstract class AbstractSearchFetcher<T> implements DataFetcher<T> {
    /**
     * The environment variable/system property used to configure the Search API URL
     */
    public static final String ENV_SEARCH_API_URL = "SEARCH_API_URL";
    /**
     * The default Search API URL used if none was supplied
     */
    public static final String DEFAULT_SEARCH_API_URL = "http://localhost:8181";
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSearchFetcher.class);
    private static final ObjectMapper JSON = new ObjectMapper();
    private final HttpClient client = HttpClient.newBuilder().build();
    private String searchApiUrl = null;

    /**
     * Creates a new abstract search fetcher
     */
    public AbstractSearchFetcher() {

    }

    /**
     * Builds the API used to issue Search Requests to the underlying Search API
     *
     * @param searchApiUrl Base Search API URL
     * @param searchTerm   Search Term
     * @param environment  Data Fetching Environment
     * @return Search API Request URL
     */
    public static URI buildSearchApiRequestUri(String searchApiUrl, String searchTerm,
                                               DataFetchingEnvironment environment) {
        StringBuilder builder = new StringBuilder();
        builder.append(searchApiUrl)
               .append("/documents?query=")
               .append(URLEncoder.encode(searchTerm, StandardCharsets.UTF_8));

        // Add optional parameters
        SearchType searchType = environment.getArgument(IANodeGraphSchema.ARGUMENT_SEARCH_TYPE);
        if (searchType != null) {
            builder.append("&type=").append(searchType.name().toLowerCase(Locale.ROOT));
        }
        Integer limit = environment.getArgument(IANodeGraphSchema.ARGUMENT_LIMIT);
        if (limit != null && limit > 0) {
            builder.append("&limit=").append(limit);
        }
        Integer offset = environment.getArgument(IANodeGraphSchema.ARGUMENT_OFFSET);
        if (offset != null && offset >= 1) {
            builder.append("&offset=").append(offset);
        }
        String typeFilter = environment.getArgument(IANodeGraphSchema.ARGUMENT_TYPE_FILTER);
        if (StringUtils.isNotBlank(typeFilter)) {
            builder.append("&type-filter=")
                   .append(Base64.encodeBase64URLSafeString(typeFilter.getBytes(StandardCharsets.UTF_8)))
                   .append("&is-type-filter-base64=true");
        }
        return URI.create(builder.toString());
    }

    private static String findSearchApiUrl() {
        // Try the environment variable first
        String envValue = System.getenv(ENV_SEARCH_API_URL);
        if (StringUtils.isNotBlank(envValue)) {
            return envValue;
        }
        // Try the system property second, falling back to the default value if not available
        return System.getProperty(ENV_SEARCH_API_URL, DEFAULT_SEARCH_API_URL);
    }

    /**
     * Implements the common functionality of dispatching a search request off to the configured IANode Search API
     * service and the basic translation of Search Results back into GraphQL model classes.  Derived {@link DataFetcher}
     * implementations are expected to call this method and then use the search results to produce the actual model
     * classes that they return.
     *
     * @param environment Data Fetching environment
     * @return IANode Search Results
     */
    @SuppressWarnings("unchecked")
    protected IANodeSearchResults searchCommon(DataFetchingEnvironment environment) {
        configureSearchApiUrl();

        IANodeExecutionContext context = environment.getLocalContext();
        DatasetGraph dsg = context.getDatasetGraph();
        String rawGraph = environment.getArgument(IANodeGraphSchema.ARGUMENT_GRAPH);
        Node graphFilter = StringUtils.isNotBlank(rawGraph) ? StartingNodesFetcher.parseStart(rawGraph) : Node.ANY;

        // Make a search to populate the starts filter
        String searchTerm = environment.getArgument(IANodeGraphSchema.ARGUMENT_SEARCH_TERM);
        if (StringUtils.isBlank(searchTerm)) {
            throw new IllegalArgumentException("Failed to make query as no 'searchTerm' argument provided");
        }

        List<Node> startFilters = new ArrayList<>();
        HttpRequest.Builder request = HttpRequest.newBuilder(
                AbstractSearchFetcher.buildSearchApiRequestUri(this.searchApiUrl, searchTerm, environment)).GET();
        if (context.hasAuthToken()) {
            // If we have an authentication token need to copy it to our search request so that our request reflects the
            // requesting users data access
            // Note that since this code doesn't know its deployment context we don't know how the token originally
            // was provided to us so when talking to another HTTP service within the platform pass it in the two main
            // ways that service might expect to receive it. In an AWS context this might mean that the requests pass
            // back via the ELB which is applying the necessary token(s) anyway but no harm in manually adding it
            // ourselves.
            request = request.header(JwtHttpConstants.HEADER_AUTHORIZATION,
                                     JwtHttpConstants.AUTH_SCHEME_BEARER + " " + context.getAuthToken())
                             .header(AwsConstants.HEADER_DATA, context.getAuthToken());
        }

        IANodeSearchResults iaNodeResults = null;

        HttpResponse<InputStream> response;
        Map<String, Object> results;

        try {
            response = client.send(request.build(), HttpResponse.BodyHandlers.ofInputStream());
            results = JSON.readValue(response.body(), GraphQLOverHttp.GENERIC_MAP_TYPE);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SearchFetcherException("SearchCommon operation interrupted", e);
        }
        catch (Exception e) {
            throw new SearchFetcherException("Failed to make query for search term " + searchTerm + ".  Search service may be unavailable in your environment.", e);
        }

        if (response.statusCode() != HttpSC.OK_200) {
            throw new SearchFetcherException("Failed to make query for search term " + searchTerm + ", received status " + response.statusCode());
        }

        iaNodeResults = IANodeSearchResults.fromMap(results);

        if (!results.containsKey("results")) {
            return iaNodeResults;
        }

        List<Map<String, Object>> searchResults = (List<Map<String, Object>>) results.get("results");

        for (Map<String, Object> result : searchResults) {
            if (!result.containsKey("document")) {
                continue;
            }

            String docUri = (String) ((Map<String, Object>) result.get("document")).get("uri");

            if (!StringUtils.isBlank(docUri)) {
                startFilters.add(StartingNodesFetcher.parseStart(docUri));
            }
        }

        List<IANodeGraphNode> nodes = Txn.calculateRead(dsg, () -> startFilters.stream()
                                                                                 .distinct()
                                                                                 .filter(n -> StartingNodesFetcher.usedAsSubjectOrObject(
                                                                                         n, dsg, graphFilter))
                                                                                 .map(n -> new IANodeGraphNode(n,
                                                                                                                 dsg.prefixes()))
                                                                                 .toList());
        iaNodeResults.setNodes(nodes);
        return iaNodeResults;
    }

    private void configureSearchApiUrl() {
        // Get the Search API URL trimming off any trailing slash
        this.searchApiUrl = AbstractSearchFetcher.findSearchApiUrl();
        if (this.searchApiUrl.endsWith("/")) {
            this.searchApiUrl = this.searchApiUrl.substring(0, this.searchApiUrl.length() - 1);
        }
        LOGGER.info("Configured Search API URL as {}", this.searchApiUrl);
    }
}
