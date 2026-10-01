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
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.models.IANodeGraphNode;
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.models.LiteralProperty;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.system.Txn;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A GraphQL {@link DataFetcher} that fetches the literal properties of a node
 */
public class LiteralPropertiesFetcher implements DataFetcher<List<LiteralProperty>> {

    /**
     * Creates a fetcher that finds the literal properties associated with a node
     */
    public LiteralPropertiesFetcher() {
        // Stateless class
    }

    @Override
    public List<LiteralProperty> get(DataFetchingEnvironment environment) {
        IANodeExecutionContext context = environment.getLocalContext();
        DatasetGraph dsg = context.getDatasetGraph();
        IANodeGraphNode node = environment.getSource();

        return Txn.calculateRead(dsg, () -> findLiterals(dsg, node));
    }

    private static List<LiteralProperty> findLiterals(DatasetGraph dsg, IANodeGraphNode node) {
        return dsg.stream(Node.ANY, node.getNode(), Node.ANY, Node.ANY)
                  .filter(q -> q.getObject().isLiteral())
                  .map(q -> new LiteralProperty(q.getPredicate(), q.getObject(),
                                                dsg.prefixes()))
                  .collect(Collectors.toList());
    }
}
