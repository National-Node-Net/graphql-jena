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

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingEnvironmentImpl;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.sparql.core.DatasetGraph;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import uk.gov.dbt.ndtp.jena.graphql.execution.ianode.graph.IANodeExecutionContext;
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.IANodeGraphSchema;
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.models.SearchType;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TestAbstractSearchFetcher {

    private final DatasetGraph dsg = TestStartingSearchFetcher.createPagedSearchTestDataset();

    @Test
    public void givenMinimalArguments_whenFormingSearchUrl_thenMinimalUrl() {
        // Given
        IANodeExecutionContext context = new IANodeExecutionContext(dsg, "");
        DataFetchingEnvironment environment = DataFetchingEnvironmentImpl.newDataFetchingEnvironment()
                                                                         .localContext(context)
                                                                         .arguments(
                                                                                 Map.of(IANodeGraphSchema.ARGUMENT_SEARCH_TERM,
                                                                                        "test"))
                                                                         .build();

        // When
        URI searchUrl =
                AbstractSearchFetcher.buildSearchApiRequestUri(
                    AbstractSearchFetcher.DEFAULT_SEARCH_API_URL, "test",
                                                               environment);

        // Then
        Assert.assertEquals(searchUrl.getPath(), "/documents");
        Assert.assertEquals(searchUrl.getQuery(), "query=test");
    }

    @DataProvider(name = "searchArguments")
    public Object[][] searchArguments() {
        return new Object[][] {
                { Map.of(IANodeGraphSchema.ARGUMENT_SEARCH_TYPE, SearchType.TERM), Map.of("type", "term") },
                {
                        Map.of(IANodeGraphSchema.ARGUMENT_LIMIT, 10, IANodeGraphSchema.ARGUMENT_OFFSET, 100),
                        Map.of(IANodeGraphSchema.ARGUMENT_LIMIT, "1", IANodeGraphSchema.ARGUMENT_OFFSET, "100")
                },
                {
                        Map.of(IANodeGraphSchema.ARGUMENT_LIMIT, 100),
                        Map.of(IANodeGraphSchema.ARGUMENT_LIMIT, "100")
                },
                {
                        Map.of(IANodeGraphSchema.ARGUMENT_TYPE_FILTER, "Person"),
                        Map.of("type-filter",
                               Base64.encodeBase64URLSafeString("Person".getBytes(StandardCharsets.UTF_8)),
                               "is-type-filter-base64", "true")
                }
        };
    }

    @Test(dataProvider = "searchArguments")
    public void givenValidArguments_whenFormingSearchUrl_thenReflectedInUrl(Map<String, Object> arguments,
                                                                            Map<String, String> expectedQuerystringParameters) {
        // Given
        IANodeExecutionContext context = new IANodeExecutionContext(dsg, "");
        DataFetchingEnvironment environment = DataFetchingEnvironmentImpl.newDataFetchingEnvironment()
                                                                         .localContext(context)
                                                                         .arguments(arguments)
                                                                         .build();

        // When
        URI searchUrl = AbstractSearchFetcher.buildSearchApiRequestUri("https://some-deployment/api/search", "test",
                                                                       environment);

        // Then
        Assert.assertEquals(searchUrl.getPath(), "/api/search/documents");
        Assert.assertTrue(StringUtils.contains(searchUrl.getQuery(), "query=test"));
        for (Map.Entry<String, String> entry : expectedQuerystringParameters.entrySet()) {
            Assert.assertTrue(StringUtils.contains(searchUrl.getQuery(),
                                                   String.format("&%s=%s", entry.getKey(), entry.getValue())),
                              "Expected querystring parameter " + entry.getKey() + " was not present in generated URL");
        }
    }
}
