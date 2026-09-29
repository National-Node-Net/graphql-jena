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

import graphql.execution.MergedField;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingEnvironmentImpl;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Quad;
import org.testng.annotations.Test;
import uk.gov.dbt.ndtp.jena.graphql.execution.ianode.graph.IANodeExecutionContext;
import uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.models.State;

import java.util.Map;

import static org.apache.jena.graph.NodeFactory.*;

public class TestStartingNodesFetcher {
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_get_illegalRawStart_null() {
        // given
        StartingNodesFetcher fetcher = new StartingNodesFetcher(false);
        DatasetGraph dsg  = DatasetGraphFactory.create();
        dsg.add(new Quad(createLiteralString("graph"), createBlankNode("subject"), IesFetchers.IS_START_OF, createLiteralString("object")));
        dsg.add(new Quad(createLiteralString("graph"), createBlankNode("subject"), IesFetchers.IS_START_OF, createBlankNode("object")));
        dsg.add(new Quad(createLiteralString("graph"), createBlankNode("subject"), IesFetchers.IS_START_OF, createURI("object")));
        MergedField mergedField = MergedField.newMergedField().addField(new Field("start")).build();
        IANodeExecutionContext context = new IANodeExecutionContext(dsg, "");
        DataFetchingEnvironment environment = DataFetchingEnvironmentImpl
                .newDataFetchingEnvironment()
                .localContext(context)
                .mergedField(mergedField)
                .source(new State(createBlankNode("object"), Node.ANY, Node.ANY))
                .build();
        // when
        // then
        fetcher.get(environment);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_get_illegalRawStart_nullMulti() {
        // given
        StartingNodesFetcher fetcher = new StartingNodesFetcher(true);
        DatasetGraph dsg  = DatasetGraphFactory.create();
        dsg.add(new Quad(createLiteralString("graph"), createBlankNode("subject"), IesFetchers.IS_START_OF, createLiteralString("object")));
        dsg.add(new Quad(createLiteralString("graph"), createBlankNode("subject"), IesFetchers.IS_START_OF, createBlankNode("object")));
        dsg.add(new Quad(createLiteralString("graph"), createBlankNode("subject"), IesFetchers.IS_START_OF, createURI("object")));
        MergedField mergedField = MergedField.newMergedField().addField(new Field("start")).build();
        IANodeExecutionContext context = new IANodeExecutionContext(dsg, "");
        DataFetchingEnvironment environment = DataFetchingEnvironmentImpl
                .newDataFetchingEnvironment()
                .localContext(context)
                .mergedField(mergedField)
                .source(new State(createBlankNode("object"), Node.ANY, Node.ANY))
                .build();
        // when
        // then
        fetcher.get(environment);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_get_illegalRawStart_badURIArgument() {
        // given
        StartingNodesFetcher fetcher = new StartingNodesFetcher(false);
        DatasetGraph dsg  = DatasetGraphFactory.create();
        dsg.add(new Quad(createLiteralString("graph"), createBlankNode("subject"), IesFetchers.IS_START_OF, createLiteralString("object")));
        dsg.add(new Quad(createLiteralString("graph"), createBlankNode("subject"), IesFetchers.IS_START_OF, createBlankNode("object")));
        dsg.add(new Quad(createLiteralString("graph"), createBlankNode("subject"), IesFetchers.IS_START_OF, createURI("object")));
        MergedField mergedField = MergedField.newMergedField().addField(new Field("start")).build();
        IANodeExecutionContext context = new IANodeExecutionContext(dsg, "");
        DataFetchingEnvironment environment = DataFetchingEnvironmentImpl
                .newDataFetchingEnvironment()
                .localContext(context)
                .mergedField(mergedField)
                .arguments(Map.of("graph", "_:uri", "uri", -1))
                .source(new State(createBlankNode("object"), Node.ANY, Node.ANY))
                .build();
        // when
        // then
        fetcher.get(environment);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_get_illegalRawStart_badURIArgumentList() {
        // given
        StartingNodesFetcher fetcher = new StartingNodesFetcher(true);
        DatasetGraph dsg  = DatasetGraphFactory.create();
        dsg.add(new Quad(createLiteralString("graph"), createBlankNode("subject"), IesFetchers.IS_START_OF, createLiteralString("object")));
        dsg.add(new Quad(createLiteralString("graph"), createBlankNode("subject"), IesFetchers.IS_START_OF, createBlankNode("object")));
        dsg.add(new Quad(createLiteralString("graph"), createBlankNode("subject"), IesFetchers.IS_START_OF, createURI("object")));
        MergedField mergedField = MergedField.newMergedField().addField(new Field("start")).build();
        IANodeExecutionContext context = new IANodeExecutionContext(dsg, "");
        DataFetchingEnvironment environment = DataFetchingEnvironmentImpl
                .newDataFetchingEnvironment()
                .localContext(context)
                .mergedField(mergedField)
                .arguments(Map.of("graph", "_:uri", "uris", "unsuitable"))
                .source(new State(createBlankNode("object"), Node.ANY, Node.ANY))
                .build();
        // when
        // then
        fetcher.get(environment);
    }
}
