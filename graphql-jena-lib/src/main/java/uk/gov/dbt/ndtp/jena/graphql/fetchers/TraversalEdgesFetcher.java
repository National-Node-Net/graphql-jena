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

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import uk.gov.dbt.ndtp.jena.graphql.schemas.IANodeSchema;
import uk.gov.dbt.ndtp.jena.graphql.schemas.models.EdgeDirection;
import uk.gov.dbt.ndtp.jena.graphql.schemas.models.NodeKind;
import uk.gov.dbt.ndtp.jena.graphql.schemas.models.TraversalEdge;
import uk.gov.dbt.ndtp.jena.graphql.schemas.models.TraversalNode;
import uk.gov.dbt.ndtp.jena.graphql.utils.NodeFilter;
import uk.gov.dbt.ndtp.jena.graphql.schemas.TraversalSchema;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.system.Txn;

/**
 * A {@link DataFetcher} that fetches the incoming/outgoing edges from a node as part of answering a Traversal GraphQL
 * schema query
 */
public class TraversalEdgesFetcher implements DataFetcher<List<TraversalEdge>> {

    /**
     * Creates a traversal edges fetcher that finds the incoming/outgoing edges for a traversal node
     */
    public TraversalEdgesFetcher() {
        // Stateless class
    }

    @Override
    public List<TraversalEdge> get(DataFetchingEnvironment environment) {
        DatasetGraph dsg = environment.getLocalContext();
        TraversalNode node = environment.getSource();

        // Determine which edges we are traversing and which edges we care about
        List<Node> predicateFilters = NodeFilter.parseList(environment.getArgument(IANodeSchema.PREDICATE_FIELD));
        EnumSet<NodeKind> kinds = NodeFilter.parseKinds(environment.getArgument(TraversalSchema.KINDS_ARGUMENT));

        return Txn.calculateRead(dsg, () -> {
            List<TraversalEdge> edges = switch (environment.getField().getName()) {
                //@formatter:off
            case TraversalSchema.INCOMING_FIELD ->
                    predicateFilters
                          .stream()
                          .distinct()
                          .flatMap(p -> dsg.stream(Node.ANY, Node.ANY, p, node.getNode().getNode())
                          .map(q -> TraversalEdge.of(q.getPredicate(), EdgeDirection.IN, q.getSubject())))
                          .filter(e -> kinds.contains(e.getTarget().getNode().getKind()))
                          .collect(Collectors.toList());
            case TraversalSchema.OUTGOING_FIELD ->
                    predicateFilters
                          .stream()
                          .distinct()
                          .flatMap(p -> dsg.stream(Node.ANY, node.getNode().getNode(), p, Node.ANY))
                          .map(q -> TraversalEdge.of(q.getPredicate(), EdgeDirection.OUT, q.getObject()))
                          .filter(e -> kinds.contains(e.getTarget().getNode().getKind()))
                          .collect(Collectors.toList());
            //@formatter:on
                default -> throw new IllegalArgumentException("Unrecognised field " + environment.getField().getName());
            };
            return !edges.isEmpty() ? edges : null;
        });
    }
}
