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

package uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.models;

import static org.apache.jena.graph.NodeFactory.createBlankNode;
import static org.apache.jena.graph.NodeFactory.createURI;
import static uk.gov.dbt.ndtp.jena.graphql.utils.UtilConstants.RANDOM_ID;

import org.apache.jena.graph.Node;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.PrefixMapFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.IANodeGraphSchema;

public class TestIANodeGraphNode {

    @Test
    public void test_getURI_blankNode() {
        // given
       IANodeGraphNode node = new IANodeGraphNode(createBlankNode(RANDOM_ID), null);
        // when
        String actualUri = node.getUri();
        // then
        Assert.assertEquals(actualUri, IANodeGraphSchema.BLANK_NODE_PREFIX + RANDOM_ID);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_getURI_nonURINode() {
        // given
       IANodeGraphNode node = new IANodeGraphNode(Node.ANY, null);
        // when
        // then
        node.getUri();
    }

    @Test
    public void test_getShortURI_nullPrefix() {
        // given
        IANodeGraphNode node = new IANodeGraphNode(createBlankNode(RANDOM_ID), null);
        // when
        String actualUri = node.getShortUri();
        // then
        Assert.assertEquals(actualUri, IANodeGraphSchema.BLANK_NODE_PREFIX + RANDOM_ID);
    }

    @Test
    public void test_getShortURI_notURINode() {
        // given
       IANodeGraphNode node = new IANodeGraphNode(createBlankNode(RANDOM_ID), PrefixMapFactory.create());
        // when
        String actualUri = node.getShortUri();
        // then
        Assert.assertEquals(actualUri, IANodeGraphSchema.BLANK_NODE_PREFIX + RANDOM_ID);
    }

    @Test
    public void test_getShortURI_URINode_noPrefix() {
        // given
        IANodeGraphNode node = new IANodeGraphNode(createURI(RANDOM_ID), PrefixMapFactory.create());
        // when
        String actualUri = node.getShortUri();
        // then
        Assert.assertEquals(actualUri, RANDOM_ID);
    }

    @Test
    public void test_getShortURI_URINode_withPrefix() {
        // given
        PrefixMap prefixMap = PrefixMapFactory.create();
        prefixMap.add(RANDOM_ID, "test/");
        IANodeGraphNode node = new IANodeGraphNode(createURI("test/" + RANDOM_ID), prefixMap);
        // when
        String actualUri = node.getShortUri();
        // then
        Assert.assertEquals(actualUri, RANDOM_ID + ":" + RANDOM_ID);
    }

    @Test
    public void test_getUriHash() {
        // given
        IANodeGraphNode node = new IANodeGraphNode(createURI(RANDOM_ID), null);
        // when
        String actual = node.getUriHash();
        // then
        Assert.assertNotNull(actual);
        Assert.assertNotEquals(actual, RANDOM_ID);
    }
}
