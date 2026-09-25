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

package uk.gov.dbt.ndtp.jena.graphql.utils;

import static java.util.Collections.emptyMap;
import static uk.gov.dbt.ndtp.jena.graphql.utils.NodeFilter.make;
import static uk.gov.dbt.ndtp.jena.graphql.utils.NodeFilter.parse;
import static uk.gov.dbt.ndtp.jena.graphql.utils.NodeFilter.parseKinds;
import static uk.gov.dbt.ndtp.jena.graphql.utils.NodeFilter.parseList;
import static org.apache.jena.graph.NodeFactory.createBlankNode;

import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestNodeFilter {

    @Test
    public void test_parse_null() {
        // given
        // when
        Node actual = parse(null);
        // then
        Assert.assertEquals(actual, Node.ANY);
    }

    @Test
    public void test_parse_empty() {
        // given
        // when
        Node actual = parse(emptyMap());
        // then
        Assert.assertEquals(actual, Node.ANY);
    }

    @Test
    public void test_make_null() {
        // given
        // when
        Map<String, Object> actualMap = make(null);
        // then
        Assert.assertNull(actualMap);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_parseKinds_invalid() {
        // given
        // when
        // then
        parseKinds(new Object());
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_parseList_invalidObject() {
        // given
        // when
        // then
        parseList(new Object());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_parseList_invalidList() {
        // given
        // when
        // then
        parseList(List.of(new Object()));
    }

    @Test
    public void test_parseList_node(){
        // given
        // when
        List<Node> actualList = parseList(createBlankNode());
        // then
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
    }

    @Test
    public void test_parseList_map(){
        // given
        // when
        List<Node> actualList = parseList(emptyMap());
        // then
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
    }
}
