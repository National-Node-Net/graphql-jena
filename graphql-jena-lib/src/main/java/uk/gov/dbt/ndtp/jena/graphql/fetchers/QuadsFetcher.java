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
import uk.gov.dbt.ndtp.jena.graphql.schemas.IANodeSchema;
import uk.gov.dbt.ndtp.jena.graphql.utils.NodeFilter;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.system.Txn;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A GraphQL Data Fetcher that fetches quads
 */
public class QuadsFetcher implements DataFetcher<List<Object>> {

    /**
     * Creates a new Quads Fetcher that fetches quads from an underlying RDF dataset
     */
    public QuadsFetcher() {
        // Stateless class
    }

    @Override
    public List<Object> get(DataFetchingEnvironment environment) {
        Node subject = NodeFilter.parse(environment.getArgument(IANodeSchema.SUBJECT_FIELD));
        Node predicate = NodeFilter.parse(environment.getArgument(IANodeSchema.PREDICATE_FIELD));
        Node object = NodeFilter.parse(environment.getArgument(IANodeSchema.OBJECT_FIELD));
        Node graph = NodeFilter.parse(environment.getArgument(IANodeSchema.GRAPH_FIELD));

        boolean includesSubject = environment.getSelectionSet().contains(IANodeSchema.SUBJECT_FIELD + "/**");
        boolean includesPredicate = environment.getSelectionSet().contains(
            IANodeSchema.PREDICATE_FIELD + "/**");
        boolean includesObject = environment.getSelectionSet().contains(IANodeSchema.OBJECT_FIELD + "/**");
        boolean includesGraph = environment.getSelectionSet().contains(IANodeSchema.GRAPH_FIELD + "/**");
        boolean includesAll = includesSubject && includesPredicate && includesObject && includesGraph;
        boolean includesTriple = includesSubject && includesPredicate && includesObject;

        DatasetGraph dsg = environment.getLocalContext();

        return Txn.calculateRead(dsg, () -> {
            if (!includesAll) {
                if (includesTriple) {
                    return dsg.stream(graph, subject, predicate, object)
                              .map(Quad::asTriple)
                              .collect(Collectors.toList());
                } else {
                    return dsg.stream(graph, subject, predicate, object)
                              .map(q -> map(q, includesSubject, includesPredicate, includesObject, includesGraph))
                              .collect(
                                      Collectors.toList());
                }
            }
            return dsg.stream(graph, subject, predicate, object).collect(Collectors.toList());
        });
    }

    private Object map(Quad q, boolean includesSubject, boolean includesPredicate, boolean includesObject,
                       boolean includesGraph) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        if (includesSubject) {
            map.put(IANodeSchema.SUBJECT_FIELD, q.getSubject());
        }
        if (includesPredicate) {
            map.put(IANodeSchema.PREDICATE_FIELD, q.getPredicate());
        }
        if (includesObject) {
            map.put(IANodeSchema.OBJECT_FIELD, q.getObject());
        }
        if (includesGraph) {
            map.put(IANodeSchema.GRAPH_FIELD, q.getObject());
        }
        return map;
    }
}
