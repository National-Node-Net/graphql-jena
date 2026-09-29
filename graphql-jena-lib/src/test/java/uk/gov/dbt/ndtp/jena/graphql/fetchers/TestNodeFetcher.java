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

import static java.util.Collections.emptyMap;
import static org.apache.jena.graph.NodeFactory.createBlankNode;

import graphql.execution.MergedField;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingEnvironmentImpl;

import java.util.Map;

import uk.gov.dbt.ndtp.jena.graphql.utils.UtilConstants;
import uk.gov.dbt.ndtp.jena.graphql.schemas.models.WrappedNode;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Quad;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestNodeFetcher {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_get_defaultNullParent() {
        // given
        NodeFetcher fetcher = new NodeFetcher();
        DataFetchingEnvironment environment = DataFetchingEnvironmentImpl.newDataFetchingEnvironment().build();
        // when
        // then
        fetcher.get(environment);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_get_unknownParentObject() {
        // given
        NodeFetcher fetcher = new NodeFetcher();
        DataFetchingEnvironment environment = DataFetchingEnvironmentImpl
                .newDataFetchingEnvironment()
                .source(new Object())
                .build();
        // when
        // then
        fetcher.get(environment);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, dataProvider = "invalidQuadFieldsAndNodes")
    public void test_get_quad_invalidFieldAndNodes(String fieldName, Quad quad) {
        // given
        NodeFetcher fetcher = new NodeFetcher();
        MergedField mergedField = MergedField.newMergedField().addField(new Field(fieldName)).build();
        DataFetchingEnvironment environment = DataFetchingEnvironmentImpl
                .newDataFetchingEnvironment()
                .source(quad)
                .mergedField(mergedField)
                .build();

        // when
        // then
        fetcher.get(environment);
    }

    @Test(dataProvider = "validQuadFieldsAndNodes")
    public void test_get_quad_validNode(String fieldName, Quad quad) {
        // given
        NodeFetcher fetcher = new NodeFetcher();
        MergedField mergedField = MergedField.newMergedField().addField(new Field(fieldName)).build();

        DataFetchingEnvironment environment = DataFetchingEnvironmentImpl
                .newDataFetchingEnvironment()
                .source(quad)
                .mergedField(mergedField)
                .build();

        // when
        WrappedNode wrappedNode = fetcher.get(environment);

        // then
        Assert.assertNotNull(wrappedNode);
        Assert.assertEquals(wrappedNode.getValue(), UtilConstants.RANDOM_ID);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, dataProvider = "invalidTripleFieldsAndNodes")
    public void test_get_triple_invalidFieldAndNodes(String fieldName, Triple triple) {
        // given
        NodeFetcher fetcher = new NodeFetcher();
        MergedField mergedField = MergedField.newMergedField().addField(new Field(fieldName)).build();
        DataFetchingEnvironment environment = DataFetchingEnvironmentImpl
                .newDataFetchingEnvironment()
                .source(triple)
                .mergedField(mergedField)
                .build();

        // when
        // then
        fetcher.get(environment);
    }

    @Test(dataProvider = "validTripleFieldsAndNodes")
    public void test_get_triple_validNode(String fieldName, Triple triple) {
        // given
        NodeFetcher fetcher = new NodeFetcher();
        MergedField mergedField = MergedField.newMergedField().addField(new Field(fieldName)).build();

        DataFetchingEnvironment environment = DataFetchingEnvironmentImpl
                .newDataFetchingEnvironment()
                .source(triple)
                .mergedField(mergedField)
                .build();

        // when
        WrappedNode wrappedNode = fetcher.get(environment);

        // then
        Assert.assertNotNull(wrappedNode);
        Assert.assertEquals(wrappedNode.getValue(), UtilConstants.RANDOM_ID);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, dataProvider = "invalidMapFieldsAndNodes")
    public void test_get_map_invalidFieldAndNodes(String fieldName, Map<String, Object> map) {
        // given
        NodeFetcher fetcher = new NodeFetcher();
        MergedField mergedField = MergedField.newMergedField().addField(new Field(fieldName)).build();
        DataFetchingEnvironment environment = DataFetchingEnvironmentImpl
                .newDataFetchingEnvironment()
                .source(map)
                .mergedField(mergedField)
                .build();

        // when
        // then
        fetcher.get(environment);
    }

    @Test(dataProvider = "validMapFieldsAndNodes")
    public void test_get_map_validNode(String fieldName, Map<String, Object> map) {
        // given
        NodeFetcher fetcher = new NodeFetcher();
        MergedField mergedField = MergedField.newMergedField().addField(new Field(fieldName)).build();

        DataFetchingEnvironment environment = DataFetchingEnvironmentImpl
                .newDataFetchingEnvironment()
                .source(map)
                .mergedField(mergedField)
                .build();

        // when
        WrappedNode wrappedNode = fetcher.get(environment);

        // then
        Assert.assertNotNull(wrappedNode);
        Assert.assertEquals(wrappedNode.getValue(), UtilConstants.RANDOM_ID);
    }

    @DataProvider(name = "invalidMapFieldsAndNodes")
    private static Object[][] invalidMapFieldsAndNodes() {
        return new Object[][] {
                { "unrecognisedField", Map.of()},
                {"subject", emptyMap()},
                {"predicate", Map.of("mismatch", "ignore")},
                {"object", Map.of("object", "string")},
                {"quads", Map.of("quads", new Object())}
        };
    }

    @DataProvider(name = "validMapFieldsAndNodes")
    private static Object[][] validMapFieldsAndNodes() {
        return new Object[][] {
                {"quads", Map.of("quads", createBlankNode(UtilConstants.RANDOM_ID))},
                {"subject", Map.of("subject", new WrappedNode(createBlankNode(UtilConstants.RANDOM_ID)))},
                {"predicate", Map.of("predicate", new WrappedNode(createBlankNode(UtilConstants.RANDOM_ID)))},
                {"object", Map.of("object", createBlankNode(UtilConstants.RANDOM_ID))},
                };
    }

    @DataProvider(name = "invalidQuadFieldsAndNodes")
    private static Object[][] invalidQuadFieldsAndNodes() {
        return new Object[][] {
                {"unrecognisedField", Quad.ANY},
                {"subject", Quad.ANY},
                {"predicate", Quad.ANY},
                {"object", Quad.ANY},
                {"graph", Quad.ANY}
        };
    }

    @DataProvider(name = "validQuadFieldsAndNodes")
    private static Object[][] validQuadFieldsAndNodes() {
            return new Object[][] {
            {"graph", new Quad(createBlankNode(UtilConstants.RANDOM_ID), Node.ANY, Node.ANY, Node.ANY)},
            {"subject", new Quad(Node.ANY, createBlankNode(UtilConstants.RANDOM_ID), Node.ANY, Node.ANY)},
            {"predicate", new Quad(Node.ANY, Node.ANY, createBlankNode(UtilConstants.RANDOM_ID), Node.ANY)},
            {"object", new Quad(Node.ANY, Node.ANY, Node.ANY, createBlankNode(UtilConstants.RANDOM_ID))},
        };
    }

    @DataProvider(name = "invalidTripleFieldsAndNodes")
    private static Object[][] invalidTripleFieldsAndNodes() {
        return new Object[][] {
                {"unrecognisedField", Triple.ANY},
                {"subject", Triple.ANY},
                {"predicate", Triple.ANY},
                {"object", Triple.ANY}
        };
    }

    @DataProvider(name = "validTripleFieldsAndNodes")
    private static Object[][] validTripleFieldsAndNodes() {
        return new Object[][] {
                {"subject", Triple.create(createBlankNode(UtilConstants.RANDOM_ID), Node.ANY, Node.ANY)},
                {"predicate", Triple.create(Node.ANY, createBlankNode(UtilConstants.RANDOM_ID), Node.ANY)},
                {"object", Triple.create(Node.ANY, Node.ANY, createBlankNode(UtilConstants.RANDOM_ID))},
        };
    }
}
