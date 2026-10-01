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

package uk.gov.dbt.ndtp.jena.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;
import java.util.stream.Collectors;

import uk.gov.dbt.ndtp.jena.graphql.schemas.TraversalSchema;
import uk.gov.dbt.ndtp.jena.graphql.schemas.models.TraversalNode;
import uk.gov.dbt.ndtp.jena.graphql.utils.NodeFilter;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.system.Txn;

/**
 * A {@link DataFetcher} that generates the starting point of a Traversal GraphQL schema query
 */
public class TraversalStartsFetcher implements DataFetcher<List<TraversalNode>> {

    /**
     * Creates a traversal starts fetcher that fetches the traversal nodes from which a traversal begins
     */
    public TraversalStartsFetcher() {
        // Stateless class
    }

    @Override
    public List<TraversalNode> get(DataFetchingEnvironment environment) {
        DatasetGraph dsg = environment.getLocalContext();
        List<Node> startFilters = NodeFilter.parseList(environment.getArgument(TraversalSchema.STARTS_ARGUMENT));

        return Txn.calculateRead(dsg, () -> startFilters.stream()
                                                        .distinct()
                                                        .flatMap(n -> dsg.stream(Node.ANY, n, Node.ANY, Node.ANY))
                                                        .map(Quad::getSubject)
                                                        .distinct()
                                                        .map(TraversalNode::of)
                                                        .collect(Collectors.toList()));
    }
}
