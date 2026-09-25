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

package uk.gov.dbt.ndtp.jena.graphql.execution.ianode.graph;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.sparql.core.DatasetGraph;

import java.util.Objects;


/**
 * An execution context for GraphQL queries with the IANode GraphQL schema
 */
public class IANodeExecutionContext {

    private final DatasetGraph dsg;
    private final String authToken;

    /**
     * Creates a new execution context
     *
     * @param dsg       Dataset Graph the query executes over
     * @param authToken The users authentication token for passing onwards to other IANode services where needed
     */
    public IANodeExecutionContext(DatasetGraph dsg, String authToken) {
        Objects.requireNonNull(dsg, "DatasetGraph cannot be null");
        this.dsg = dsg;
        this.authToken = authToken;
    }

    /**
     * Gets the dataset graph the query is executing over
     *
     * @return Dataset graph
     */
    public DatasetGraph getDatasetGraph() {
        return this.dsg;
    }

    /**
     * Gets the authentication token for the user (if any)
     *
     * @return Authentication token
     */
    public String getAuthToken() {
        return this.authToken;
    }

    /**
     * Gets whether an authentication token is present
     *
     * @return True if an authentication token is present, false otherwise
     */
    public boolean hasAuthToken() {
        return StringUtils.isNotBlank(this.authToken);
    }
}
