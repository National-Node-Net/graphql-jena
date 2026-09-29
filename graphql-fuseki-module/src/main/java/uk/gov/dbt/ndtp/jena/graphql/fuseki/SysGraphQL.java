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

import uk.gov.dbt.ndtp.jena.graphql.execution.DatasetExecutor;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.jena.fuseki.Fuseki;
import org.apache.jena.fuseki.server.Operation;
import org.apache.jena.fuseki.server.OperationRegistry;
import org.apache.jena.fuseki.servlets.ActionService;
import org.apache.jena.sparql.core.DatasetGraphFactory;

/**
 * Provides global registration of the GraphQL operation within Fuseki
 */
public class SysGraphQL {

    private SysGraphQL() {}

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    /**
     * The GraphQL operation that may be associated with Fuseki endpoints
     */
    public static final Operation
            OP_GRAPHQL =
            Operation.alloc(VocabGraphQL.OPERATION, "graphql",
                            "GraphQL Query using a configurable GraphQLExecutor");

    /**
     * Ensures that the GraphQL module is properly initialised
     */
    public static void init() {
        boolean initialized = INITIALIZED.getAndSet(true);
        if (initialized) {
            return;
        }
        // GraphQL Actions for Fuseki
        try {
            ActionService graphQL = new ActionGraphQL(new DatasetExecutor(DatasetGraphFactory.empty()));
            OperationRegistry operationRegistry = OperationRegistry.get();
            operationRegistry.register(OP_GRAPHQL, graphQL);
        } catch (IOException e) {
            Fuseki.configLog.warn("Failed to register Fuseki GraphQL Operation");
        }
    }
}
