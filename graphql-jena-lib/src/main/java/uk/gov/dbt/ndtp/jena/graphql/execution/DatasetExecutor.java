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

import graphql.schema.idl.NaturalEnumValuesProvider;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeDefinitionRegistry;
import uk.gov.dbt.ndtp.jena.graphql.schemas.models.NodeKind;
import uk.gov.dbt.ndtp.jena.graphql.fetchers.QuadsFetcher;
import uk.gov.dbt.ndtp.jena.graphql.schemas.DatasetSchema;
import uk.gov.dbt.ndtp.jena.graphql.schemas.GraphQLJenaSchemas;
import org.apache.jena.sparql.core.DatasetGraph;

import java.io.IOException;

/**
 * Provides the ability to execute GraphQL queries using our simple Dataset schema
 */
public class DatasetExecutor extends AbstractDatasetExecutor {

    /**
     * Creates a new execution engine using the Dataset schema and the given {@link DatasetGraph} as the underlying data
     * source
     *
     * @param dsg Dataset Graph
     * @throws IOException Thrown if the schema cannot be loaded
     */
    public DatasetExecutor(DatasetGraph dsg) throws IOException {
        super(dsg);
    }

    @Override
    protected TypeDefinitionRegistry loadRawSchema() throws IOException {
        return GraphQLJenaSchemas.loadDatasetSchema();
    }

    @Override
    protected RuntimeWiring.Builder buildRuntimeWiring() {
        //@formatter:off
        NaturalEnumValuesProvider<NodeKind> nodeKinds = new NaturalEnumValuesProvider<>(NodeKind.class);
        return RuntimeWiring.newRuntimeWiring()
                               .type(DatasetSchema.QUADS_QUERY_TYPE,
                                     t -> t.dataFetcher(DatasetSchema.QUADS_FIELD, new QuadsFetcher()).enumValues(nodeKinds));
        //@formatter:on
    }

}
