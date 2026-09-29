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

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import uk.gov.dbt.ndtp.jena.graphql.execution.ianode.graph.IANodeExecutionContext;
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.IANodeGraphSchema;
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.models.State;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.system.Txn;
import org.apache.jena.vocabulary.RDF;

import java.util.List;

/**
 * A GraphQL {@link DataFetcher} that finds the {@code rdf:type}'s for a state
 */
public class StateTypeFetcher implements DataFetcher<String> {
    /**
     * Creates a new fetcher that finds the types for states
     */
    public StateTypeFetcher() {
        // Stateless class
    }

    @Override
    public String get(DataFetchingEnvironment environment) {
        IANodeExecutionContext context = environment.getLocalContext();
        DatasetGraph dsg = context.getDatasetGraph();
        State state = environment.getSource();

        return Txn.calculateRead(dsg, () -> {
            List<Node> types = dsg.stream(Node.ANY, state.getStateNode(), RDF.type.asNode(), Node.ANY)
                                  .map(Quad::getObject)
                                  .filter(t -> t.isURI() || t.isBlank())
                                  .toList();
            if (types.isEmpty()) {
                throw new IllegalStateException("No types available for state " + state.getUri());
            }
            Node primaryType = types.get(0);
            if (primaryType.isURI()) {
                return primaryType.getURI();
            } else {
                return IANodeGraphSchema.BLANK_NODE_PREFIX + primaryType.getBlankNodeLabel();
            }
        });
    }
}
