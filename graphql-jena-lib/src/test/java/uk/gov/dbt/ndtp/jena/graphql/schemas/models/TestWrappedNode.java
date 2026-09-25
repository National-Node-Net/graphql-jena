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


import static java.util.Collections.emptyMap;
import static org.apache.jena.graph.NodeFactory.createBlankNode;
import static org.apache.jena.graph.NodeFactory.createExt;
import static org.apache.jena.graph.NodeFactory.createGraphNode;
import static org.apache.jena.graph.NodeFactory.createLiteralLang;
import static org.apache.jena.graph.NodeFactory.createLiteralString;
import static org.apache.jena.graph.NodeFactory.createTripleNode;
import static org.apache.jena.graph.NodeFactory.createURI;
import static org.apache.jena.graph.NodeFactory.createVariable;

import java.util.Map;

import uk.gov.dbt.ndtp.jena.graphql.utils.UtilConstants;
import org.apache.jena.graph.Capabilities;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphEventManager;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.TransactionHandler;
import org.apache.jena.graph.Triple;
import org.apache.jena.shared.AddDeniedException;
import org.apache.jena.shared.DeleteDeniedException;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.sse.SSE;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestWrappedNode {
    @Test(dataProvider = "invalidNodeTypes", expectedExceptions = IllegalArgumentException.class)
    public void test_WrappedNode_illegalMode(Node node) {
        // given
        // when
        // then
        new WrappedNode(node);
    }

    @Test(dataProvider = "validNodeTypes")
    public void test_WrappedNode_validNodes(Node node, String expectedValue) {
        // given
        // when
        WrappedNode wrappedNode = new WrappedNode(node);
        // then
        Assert.assertNotNull(wrappedNode);
        Assert.assertEquals(wrappedNode.getValue(), expectedValue);
        Map<String, Object> map = wrappedNode.toMap();
        Assert.assertNotNull(map);
    }

    @Test(dataProvider = "validLiteralNodeTypes")
    public void test_WrappedNode_validLiteralNodes(Node node, String expectedValue) {
        // given
        // when
        WrappedNode wrappedNode = new WrappedNode(node);
        // then
        Assert.assertNotNull(wrappedNode);
        Assert.assertEquals(wrappedNode.getValue(), expectedValue);
        Map<String, Object> map = wrappedNode.toMap();
        Assert.assertNotNull(map);
    }


    @Test(expectedExceptions = IllegalArgumentException.class, dataProvider = "invalidMaps")
    public void test_WrapperNode_invalidMaps(Map<String, Object> map) {
        // given
        // when
        // then
        new WrappedNode(map);
    }

    @Test(dataProvider = "validMaps")
    public void test_WrapperNode_mapValid(Map<String, Object> map) {
        // given
        // when
        // then
        WrappedNode wrappedNode = new WrappedNode(map);
        Assert.assertNotNull(wrappedNode);
        Assert.assertNotNull(wrappedNode.getValue());
        Assert.assertNotNull(wrappedNode.toMap());
        Assert.assertNull(wrappedNode.getTriple());
    }


    @Test
    public void test_WrapperNode_mapTripleValid() {
        // given
        Map<String,Object> map = Map.of("kind", "TRIPLE", "triple", Map.of("subject", Map.of("kind", "BLANK", "value", "value"), "predicate", Map.of("kind", "BLANK", "value", "value"), "object", Map.of("kind", "BLANK", "value", "value")));
        // when
        // then
        WrappedNode wrappedNode = new WrappedNode(map);
        Assert.assertNotNull(wrappedNode);
        Assert.assertNull(wrappedNode.getValue());
        Assert.assertNotNull(wrappedNode.toMap());
        Assert.assertNotNull(wrappedNode.getTriple());
    }

    @Test
    public void test_WrapperNode_mapNullTriple() {
        // given
        Triple triple = SSE.parseTriple("(:s :p :o)");
        Node n = NodeFactory.createTripleNode(triple);
        // when
        WrappedNode wrappedNode = new WrappedNode(n);
        // then
        Assert.assertNotNull(wrappedNode.toMap());
    }

    @DataProvider(name = "validMaps")
    private static Object[] validMaps() {
        return new Object[] {
                Map.of("kind", "URI", "value", "value"),
                Map.of("kind", "BLANK", "value", "value"),
                Map.of("kind", "PLAIN_LITERAL", "value", "value"),
                Map.of("kind", "LANGUAGE_LITERAL", "value", "value"),
                Map.of("kind", "TYPED_LITERAL", "value", "value"),
                Map.of("kind", "VARIABLE", "value", "value"),
                };
    }

    @DataProvider(name = "invalidMaps")
    private static Object[] invalidMaps() {
        return new Object[] {
                emptyMap(),
                Map.of("kind", "unrecognised"),
                Map.of("kind", "TRIPLE"),
                Map.of("kind", "TRIPLE", "value", "of-no-value"),
                Map.of("kind", "TRIPLE", "triple", emptyMap()),
                Map.of("kind", "TRIPLE", "triple", Map.of("subject", emptyMap())),
                Map.of("kind", "TRIPLE", "triple", Map.of("predicate", emptyMap())),
                Map.of("kind", "TRIPLE", "triple", Map.of("object", emptyMap())),
                Map.of("kind", "TRIPLE", "triple", Map.of("subject", emptyMap())),
                Map.of("kind", "TRIPLE", "triple", Map.of("subject", emptyMap(), "predicate", emptyMap())),
                Map.of("kind", "TRIPLE", "triple", Map.of("subject", emptyMap(), "object", emptyMap())),
                Map.of("kind", "TRIPLE", "triple", Map.of("predicate", emptyMap(), "object", emptyMap())),
                Map.of("kind", "TRIPLE", "triple", Map.of("subject", emptyMap(), "predicate", emptyMap(), "object", emptyMap())),
                };
    }

    @DataProvider(name = "validLiteralNodeTypes")
    private static Object[][] validLiteralNodeTypes() {
        return new Object[][] {
                { createLiteralString(UtilConstants.RANDOM_ID), UtilConstants.RANDOM_ID },
                { createLiteralString(""), "" },
                { createLiteralLang(UtilConstants.RANDOM_ID, "lang"), UtilConstants.RANDOM_ID},
                { createLiteralString(UtilConstants.RANDOM_ID), UtilConstants.RANDOM_ID},
        };
    }

    @DataProvider(name = "validNodeTypes")
    private static Object[][] validNodeTypes() {
        return new Object[][] {
                { createBlankNode(UtilConstants.RANDOM_ID), UtilConstants.RANDOM_ID },
                { createLiteralString(UtilConstants.RANDOM_ID), UtilConstants.RANDOM_ID },
                { createURI(UtilConstants.RANDOM_ID), UtilConstants.RANDOM_ID },
                { createVariable(UtilConstants.RANDOM_ID), UtilConstants.RANDOM_ID },
                { createTripleNode(Triple.create(createBlankNode(), createBlankNode(), createBlankNode())), null },
                };
    }

    @DataProvider(name = "invalidNodeTypes")
    private static Object[] invalidNodeTypes() {
        return new Object[] {
                createExt("ext"),
                createGraphNode(new TestGraph())
        };
    }

    private static class TestGraph implements Graph {

        @Override
        public boolean dependsOn(Graph graph) {
            return false;
        }

        @Override
        public TransactionHandler getTransactionHandler() {
            return null;
        }

        @Override
        public Capabilities getCapabilities() {
            return null;
        }

        @Override
        public GraphEventManager getEventManager() {
            return null;
        }

        @Override
        public PrefixMapping getPrefixMapping() {
            return null;
        }

        @Override
        public void add(Triple triple) throws AddDeniedException {
            throw new UnsupportedOperationException("Test implementation not supported");
        }

        @Override
        public void delete(Triple triple) throws DeleteDeniedException {
            throw new UnsupportedOperationException("Test implementation not supported");
        }

        @Override
        public ExtendedIterator<Triple> find(Triple triple) {
            return null;
        }

        @Override
        public ExtendedIterator<Triple> find(Node node, Node node1, Node node2) {
            return null;
        }

        @Override
        public boolean isIsomorphicWith(Graph graph) {
            return false;
        }

        @Override
        public boolean contains(Node node, Node node1, Node node2) {
            return false;
        }

        @Override
        public boolean contains(Triple triple) {
            return false;
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Test implementation not supported");
        }

        @Override
        public void remove(Node node, Node node1, Node node2) {
            throw new UnsupportedOperationException("Test implementation not supported");
        }

        @Override
        public void close() {
            throw new UnsupportedOperationException("Test implementation not supported");
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isClosed() {
            return false;
        }
    }
}
