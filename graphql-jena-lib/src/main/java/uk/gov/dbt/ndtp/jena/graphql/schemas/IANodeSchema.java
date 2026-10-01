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

package uk.gov.dbt.ndtp.jena.graphql.schemas;

/**
 * Constants related to the GraphQL Schema
 */
public class IANodeSchema {

    /**
     * Class provides static constants so no need for instantiation
     */
    IANodeSchema() {

    }

    /**
     * Quad type
     */
    public static final String QUAD_TYPE = "Quad";
    /**
     * Triple type
     */
    public static final String TRIPLE_TYPE = "Triple";
    /**
     * Node type
     */
    public static final String NODE_TYPE = "Node";
    /**
     * Node kind field
     */
    public static final String KIND_FIELD = "kind";
    /**
     * Node value field
     */
    public static final String VALUE_FIELD = "value";
    /**
     * Node language field
     */
    public static final String LANGUAGE_FIELD = "language";
    /**
     * Node datatype field
     */
    public static final String DATATYPE_FIELD = "datatype";
    /**
     * Subject field
     */
    public static final String SUBJECT_FIELD = "subject";
    /**
     * Predicate field
     */
    public static final String PREDICATE_FIELD = "predicate";
    /**
     * Object field
     */
    public static final String OBJECT_FIELD = "object";
    /**
     * Graph field
     */
    public static final String GRAPH_FIELD = "graph";
    /**
     * Triple field
     */
    public static final String TRIPLE_FIELD = "triple";
}
