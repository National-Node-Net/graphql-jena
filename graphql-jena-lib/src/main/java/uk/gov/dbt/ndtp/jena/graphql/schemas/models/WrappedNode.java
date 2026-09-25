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

package uk.gov.dbt.ndtp.jena.graphql.schemas.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.gov.dbt.ndtp.jena.graphql.schemas.IANodeSchema;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.vocabulary.RDF;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A wrapper around a Jena {@link Node} that maps it into a format suitable for use with our GraphQL IANode Schema
 */
public class WrappedNode {
    private final Node node;
    private final NodeKind kind;

    /**
     * Creates a new wrapper around a Jena node
     *
     * @param n Node to wrap
     */
    public WrappedNode(Node n) {
        Objects.requireNonNull(n, "Node cannot be null");
        this.node = n;
        if (n.isURI()) {
            this.kind = NodeKind.URI;
        } else if (n.isBlank()) {
            this.kind = NodeKind.BLANK;
        } else if (n.isVariable()) {
            this.kind = NodeKind.VARIABLE;
        } else if (n.isNodeTriple()) {
            this.kind = NodeKind.TRIPLE;
        } else if (n.isLiteral()) {
            if (StringUtils.isNotBlank(n.getLiteralLanguage())) {
                this.kind = NodeKind.LANGUAGE_LITERAL;
            } else if (StringUtils.isNotBlank(n.getLiteralDatatypeURI())) {
                this.kind = NodeKind.TYPED_LITERAL;
            } else {
                this.kind = NodeKind.PLAIN_LITERAL;
            }
        } else {
            throw new IllegalArgumentException("Cannot wrap an unrecognised Node type");
        }
    }

    /**
     * Turns GraphQL output back into a Wrapped Node
     * <p>
     * Since the output may only select some fields the converted Node may not be exactly the same as the original Node
     * that was output.
     * </p>
     *
     * @param map GraphQL Output
     */
    @SuppressWarnings("unchecked")
    public WrappedNode(Map<String, Object> map) {
        Objects.requireNonNull(map, "Map cannot be null");
        if (!map.containsKey(IANodeSchema.KIND_FIELD)) {
            throw new IllegalArgumentException(
                    "Cannot convert from a Map to a WrappedNode if the kind field is not present");
        }
        this.kind = NodeKind.valueOf((String) map.get(IANodeSchema.KIND_FIELD));
        this.node = switch (this.kind) {
            case URI -> NodeFactory.createURI((String) map.get(IANodeSchema.VALUE_FIELD));
            case BLANK -> NodeFactory.createBlankNode((String) map.get(IANodeSchema.VALUE_FIELD));
            case PLAIN_LITERAL -> NodeFactory.createLiteralString((String) map.get(IANodeSchema.VALUE_FIELD));
            case LANGUAGE_LITERAL ->
                    NodeFactory.createLiteralLang((String) map.get(IANodeSchema.VALUE_FIELD),
                                                  (String) map.get(IANodeSchema.LANGUAGE_FIELD));
            case TYPED_LITERAL -> NodeFactory.createLiteral((String) map.get(IANodeSchema.VALUE_FIELD), null,
                                                            TypeMapper.getInstance()
                                                                      .getTypeByName(
                                                                              (String) map.get(
                                                                                      IANodeSchema.DATATYPE_FIELD)));
            case VARIABLE -> NodeFactory.createVariable((String) map.get(IANodeSchema.VALUE_FIELD));
            case TRIPLE -> {
                if (!map.containsKey(IANodeSchema.TRIPLE_FIELD)) {
                    throw new IllegalArgumentException("The triple field is required to recreate a Triple Node");
                }
                map = (Map<String, Object>) map.get(IANodeSchema.TRIPLE_FIELD);
                if (!map.containsKey(IANodeSchema.SUBJECT_FIELD) || !map.containsKey(
                        IANodeSchema.PREDICATE_FIELD) || !map.containsKey(IANodeSchema.OBJECT_FIELD)) {
                    throw new IllegalArgumentException("Insufficient fields to recreate a Triple Node");
                }
                WrappedNode s = new WrappedNode((Map<String, Object>) map.get(IANodeSchema.SUBJECT_FIELD));
                WrappedNode p = new WrappedNode((Map<String, Object>) map.get(IANodeSchema.PREDICATE_FIELD));
                WrappedNode o = new WrappedNode((Map<String, Object>) map.get(IANodeSchema.OBJECT_FIELD));
                yield NodeFactory.createTripleNode(s.node, p.node, o.node);
            }
        };
    }

    /**
     * Gets the kind of the Node
     *
     * @return Node kind
     */
    public NodeKind getKind() {
        return this.kind;
    }

    /**
     * Gets the value of the node, unless this is of kind {@link NodeKind#TRIPLE}
     *
     * @return Value, or {@code null} if a Triple Node
     */
    public String getValue() {
        return switch (this.kind) {
            case URI -> this.node.getURI();
            case BLANK -> this.node.getBlankNodeLabel();
            case PLAIN_LITERAL, TYPED_LITERAL, LANGUAGE_LITERAL -> this.node.getLiteralLexicalForm();
            case VARIABLE -> this.node.getName();
            default -> null;
        };
    }

    /**
     * Gets the triple, if and only if, this is of kind {@link NodeKind#TRIPLE}
     *
     * @return Triple, or {@code null} if different node kind
     */
    public Triple getTriple() {
        return this.kind == NodeKind.TRIPLE ? this.node.getTriple() : null;
    }

    /**
     * Gets the language, if and only if, this is of kind {@link NodeKind#LANGUAGE_LITERAL}
     *
     * @return Language, or {@code null} if different node kind
     */
    public String getLanguage() {
        return this.kind == NodeKind.LANGUAGE_LITERAL ? this.node.getLiteralLanguage() : null;
    }

    /**
     * Gets the datatype, if and only if, this is a typed literal
     *
     * @return Datatype, or {@code null} if different node kind
     */
    public String getDatatype() {
        return switch (this.kind) {
            case TYPED_LITERAL -> this.node.getLiteralDatatypeURI();
            case LANGUAGE_LITERAL -> RDF.dtLangString.getURI();
            default -> null;
        };
    }

    /**
     * Gets the underlying Jena {@link Node} that is being wrapped
     *
     * @return Underlying node
     */
    @JsonIgnore
    public Node getNode() {
        return this.node;
    }

    /**
     * Gets the map representation of this node such that it can be used as an argument/variable for filtering
     *
     * @return Map representation
     */
    @JsonIgnore
    public Map<String, Object> toMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put(IANodeSchema.KIND_FIELD, this.kind.name());
        if (this.kind != NodeKind.TRIPLE) {
            map.put(IANodeSchema.VALUE_FIELD, this.getValue());
        }
        if (this.kind == NodeKind.LANGUAGE_LITERAL) {
            map.put(IANodeSchema.LANGUAGE_FIELD, this.getLanguage());
        }
        if (this.kind == NodeKind.LANGUAGE_LITERAL || this.kind == NodeKind.TYPED_LITERAL) {
            map.put(IANodeSchema.DATATYPE_FIELD, this.getDatatype());
        }
        if (this.kind == NodeKind.TRIPLE) {
            map.put(IANodeSchema.TRIPLE_FIELD, toMap(this.getTriple()));
        }
        return map;
    }

    /**
     * Converts a triple into map representation such that it can be used as an argument/variable for filtering
     *
     * @param t Triple
     * @return Map representation thereof
     */
    private static Map<String, Object> toMap(Triple t) {
        if (t == null) {
            return null;
        }
        //@formatter:off
        return Map.of(IANodeSchema.SUBJECT_FIELD, new WrappedNode(t.getSubject()).toMap(),
                      IANodeSchema.PREDICATE_FIELD, new WrappedNode(t.getPredicate()).toMap(),
                      IANodeSchema.OBJECT_FIELD, new WrappedNode(t.getObject()).toMap());
        //@formatter:on
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WrappedNode that = (WrappedNode) o;
        return kind == that.kind && Objects.equals(node, that.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, node);
    }
}
