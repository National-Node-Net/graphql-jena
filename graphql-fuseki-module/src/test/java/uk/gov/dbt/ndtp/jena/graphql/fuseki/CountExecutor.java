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

import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeDefinitionRegistry;
import uk.gov.dbt.ndtp.jena.graphql.execution.AbstractDatasetExecutor;


import java.io.IOException;

import uk.gov.dbt.ndtp.jena.graphql.schemas.GraphQLJenaSchemas;
import org.apache.jena.sparql.core.DatasetGraph;

public class CountExecutor extends AbstractDatasetExecutor {
    /**
     * Creates a new execution
     *
     * @param dsg Default Dataset Graph over which queries will execute
     * @throws IOException Thrown if there is a problem reading in the underlying GraphQL schema
     */
    public CountExecutor(DatasetGraph dsg) throws IOException {
        super(dsg);
    }

    @Override
    protected TypeDefinitionRegistry loadRawSchema() throws IOException {
        return GraphQLJenaSchemas.loadSchema("/count.graphqls");
    }

    @Override
    protected RuntimeWiring.Builder buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring().type("Count", t -> t.dataFetcher("count", new CountFetcher()));
    }

    @Override
    protected boolean extendsIANodeSchema() {
        return false;
    }
}
