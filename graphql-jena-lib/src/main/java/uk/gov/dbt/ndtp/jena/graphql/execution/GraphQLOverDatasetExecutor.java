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

package uk.gov.dbt.ndtp.jena.graphql.execution;

import graphql.ExecutionResult;

import java.util.Map;

import uk.gov.dbt.ndtp.jena.graphql.server.model.GraphQLRequest;
import org.apache.jena.sparql.core.DatasetGraph;

/**
 * Provides the ability to execute GraphQL queries over {@link DatasetGraph} instances
 */
public interface GraphQLOverDatasetExecutor {
    /**
     * Executes the provided query over the given {@link DatasetGraph}
     *
     * @param dsg     Dataset Graph to query
     * @param request GraphQL Request
     * @return Execution Result
     */
    ExecutionResult execute(DatasetGraph dsg, GraphQLRequest request);

    /**
     * Executes the provided query over the given {@link DatasetGraph}
     *
     * @param dsg           Dataset Graph to query
     * @param query         Query
     * @param operationName Operation name indicating an operation within the query document to execute
     * @param variables     Variables to make available to the query
     * @param extensions    Vendor extensions to make available to the query
     * @return Execution Result
     */
    ExecutionResult execute(DatasetGraph dsg, String query, String operationName, Map<String, Object> variables,
                            Map<String, Object> extensions);
}
