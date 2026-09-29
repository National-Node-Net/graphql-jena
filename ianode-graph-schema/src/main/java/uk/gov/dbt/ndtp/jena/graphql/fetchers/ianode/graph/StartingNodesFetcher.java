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
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.IANodeGraphSchema;
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.models.IANodeGraphNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.system.Txn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A GraphQL {@link DataFetcher} that finds the starting nodes for a query
 */
public class StartingNodesFetcher implements DataFetcher<Object> {

    private final boolean multiSelect;

    /**
     * Creates a new fetcher
     *
     * @param multiSelect Whether this fetcher should support selecting multiple starting nodes
     */
    public StartingNodesFetcher(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        IANodeExecutionContext context = environment.getLocalContext();
        DatasetGraph dsg = context.getDatasetGraph();
        String rawGraph = environment.getArgument(IANodeGraphSchema.ARGUMENT_GRAPH);
        Node graphFilter = StringUtils.isNotBlank(rawGraph) ? parseStart(rawGraph) : Node.ANY;
        List<Node> startFilters = parseStarts(multiSelect ? environment.getArgument(IANodeGraphSchema.ARGUMENT_URIS) :
                                              environment.getArgument(IANodeGraphSchema.ARGUMENT_URI));

        return Txn.calculateRead(dsg, () -> {
            List<IANodeGraphNode> nodes = startFilters.stream()
                                                        .distinct()
                                                        .filter(n -> usedAsSubjectOrObject(n, dsg, graphFilter))
                                                        .map(n -> new IANodeGraphNode(n, dsg.prefixes()))
                                                        .collect(Collectors.toList());
            return multiSelect ? nodes : (!nodes.isEmpty() ? nodes.get(0) : null);
        });
    }

    /**
     * Given a node checks whether it is used as either the subject/object of any quads in the dataset
     * @param n Node
     * @param dsg Dataset
     * @param graphFilter Graph node
     * @return True if used as a subject/object, false otherwise
     */
    public static boolean usedAsSubjectOrObject(Node n, DatasetGraph dsg, Node graphFilter) {
        return dsg.contains(graphFilter, n, Node.ANY, Node.ANY) || dsg.contains(graphFilter, Node.ANY, Node.ANY, n);
    }

    @SuppressWarnings("unchecked")
    private List<Node> parseStarts(Object rawStart) {
        if (rawStart == null) {
            throw new IllegalArgumentException(
                    "Required argument" + (this.multiSelect ? IANodeGraphSchema.ARGUMENT_URIS :
                                           IANodeGraphSchema.ARGUMENT_URI) + " missing");
        }
        if (this.multiSelect) {
            if (rawStart instanceof List<?>) {
                List<Node> starts = new ArrayList<>();
                for (String uri : (List<String>) rawStart) {
                    starts.add(parseStart(uri));
                }
                return starts;
            } else {
                throw new IllegalArgumentException(
                        "Argument " + IANodeGraphSchema.ARGUMENT_URIS + " received as wrong type, expected List but got " + rawStart.getClass()
                                                                                                                                      .getCanonicalName());
            }
        } else {
            if (rawStart instanceof String) {
                return List.of(parseStart((String) rawStart));
            } else {
                throw new IllegalArgumentException(
                        "Argument " + IANodeGraphSchema.ARGUMENT_URI + " received as wrong type, expected String but got " + rawStart.getClass()
                                                                                                                                       .getCanonicalName());
            }
        }
    }

    static Node parseStart(String uri) {
        if (uri.startsWith(IANodeGraphSchema.BLANK_NODE_PREFIX)) {
            return NodeFactory.createBlankNode(uri.substring(2));
        } else {
            return NodeFactory.createURI(uri);
        }
    }
}
