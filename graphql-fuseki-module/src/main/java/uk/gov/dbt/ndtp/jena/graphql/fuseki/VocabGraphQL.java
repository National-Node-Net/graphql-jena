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

package uk.gov.dbt.ndtp.jena.graphql.fuseki;

import uk.gov.dbt.ndtp.jena.graphql.execution.GraphQLOverDatasetExecutor;
import org.apache.jena.sparql.util.Symbol;

/**
 * Constants related to the GraphQL URIs and symbols needed for this module
 */
public class VocabGraphQL {
    private VocabGraphQL() {}
    /**
     * The namespace URI for this modules configuration data
     */
    public static final String NS = "https://ndtp.co.uk/fuseki/modules/graphql#";

    /**
     * The URI used to identify the GraphQL operation when defining Fuseki endpoints in a configuration file
     */
    public static final String OPERATION = NS + "graphql";

    /**
     * Context symbol used to define the {@link GraphQLOverDatasetExecutor}
     * implementation to use for a GraphQL endpoint
     */
    public static final Symbol EXECUTOR = Symbol.create("graphql:executor");
}
