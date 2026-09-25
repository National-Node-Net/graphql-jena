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

import graphql.schema.PropertyDataFetcher;
import graphql.schema.idl.NaturalEnumValuesProvider;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeDefinitionRegistry;
import uk.gov.dbt.ndtp.jena.graphql.execution.AbstractDatasetExecutor;
import uk.gov.dbt.ndtp.jena.graphql.fetchers.ianode.graph.*;
import uk.gov.dbt.ndtp.jena.graphql.schemas.models.EdgeDirection;
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.IANodeGraphSchema;
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.models.SearchType;
import org.apache.jena.sparql.core.DatasetGraph;

import java.io.IOException;
import java.util.Map;

/**
 * A GraphQL Execution over a {@link DatasetGraph} using the {@link IANodeGraphSchema}
 */
public class IANodeGraphExecutor extends AbstractDatasetExecutor {

    /**
     * Creates a new execution
     *
     * @param dsg The default dataset over which the execution operates
     * @throws IOException Thrown if the schema cannot be loaded
     */
    public IANodeGraphExecutor(DatasetGraph dsg) throws IOException {
        super(dsg);
    }

    @Override
    protected TypeDefinitionRegistry loadRawSchema() throws IOException {
        return IANodeGraphSchema.loadIANodeGraphSchema();
    }

    @Override
    protected boolean extendsIANodeSchema() {
        return false;
    }

    @Override
    protected RuntimeWiring.Builder buildRuntimeWiring() {
        final StatePeriodFetcher periodFetcher = new StatePeriodFetcher();
        NaturalEnumValuesProvider<SearchType> nodeKinds = new NaturalEnumValuesProvider<>(SearchType.class);
        //@formatter:off
        return RuntimeWiring.newRuntimeWiring()
                            .type("Query",
                                  t -> t.dataFetcher(IANodeGraphSchema.QUERY_SINGLE_NODE, new StartingNodesFetcher(false))
                                        .dataFetcher(IANodeGraphSchema.QUERY_MULTIPLE_NODES, new StartingNodesFetcher(true))
                                        .dataFetcher(IANodeGraphSchema.QUERY_SEARCH, new StartingSearchFetcher())
                                        .dataFetcher(IANodeGraphSchema.QUERY_SEARCH_WITH_METADATA, new StartingSearchWithMetadataFetcher()).enumValues(nodeKinds)
                                        .dataFetcher(IANodeGraphSchema.QUERY_STATES, new StartingStatesFetcher())
                                        .dataFetcher(IANodeGraphSchema.QUERY_GET_ALL_ENTITIES, new AllEntitiesFetcher()))
                            .type(IANodeGraphSchema.TYPE_NODE,
                                  t -> t.dataFetcher(IANodeGraphSchema.FIELD_TYPES, new NodeTypesFetcher())
                                        .dataFetcher(IANodeGraphSchema.FIELD_PROPERTIES, new LiteralPropertiesFetcher())
                                        .dataFetcher(IANodeGraphSchema.FIELD_INBOUND_RELATIONSHIPS, new RelationshipsFetcher(EdgeDirection.IN))
                                        .dataFetcher(IANodeGraphSchema.FIELD_OUTBOUND_RELATIONSHIPS, new RelationshipsFetcher(EdgeDirection.OUT))
                                        .dataFetcher(IANodeGraphSchema.FIELD_INSTANCES, new InstancesFetcher()))
                            .type(IANodeGraphSchema.TYPE_RELATIONSHIP,
                                  // The IANode Graph schema uses underscores in these property names which defeats
                                  // graphql-java's default logic of looking for an equivalent Java property name so
                                  // have to explicitly declare the fetchers for these
                                  t -> t.dataFetcher(IANodeGraphSchema.FIELD_DOMAIN_ID, new PropertyDataFetcher<String>("domainId"))
                                        .dataFetcher(IANodeGraphSchema.FIELD_RANGE_ID, new PropertyDataFetcher<String>("rangeId")))
                            .type(IANodeGraphSchema.TYPE_STATE,
                                  t -> t.dataFetcher(IANodeGraphSchema.FIELD_TYPE, new StateTypeFetcher())
                                        .dataFetcher(IANodeGraphSchema.FIELD_RELATIONS, new StateRelationshipsFetcher())
                                        .dataFetcher(IANodeGraphSchema.FIELD_START, periodFetcher)
                                        .dataFetcher(IANodeGraphSchema.FIELD_END, periodFetcher)
                                        .dataFetcher(IANodeGraphSchema.FIELD_PERIOD, periodFetcher)
                            );
        //@formatter:on
    }

    @Override
    protected Object createLocalContext(DatasetGraph dsg, Map<String, Object> extensions) {
        // Get the auth token for the request (if any)
        String authToken = (String) extensions.get(IANodeGraphSchema.EXTENSION_AUTH_TOKEN);
        return new IANodeExecutionContext(dsg, authToken);
    }
}
