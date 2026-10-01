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

package uk.gov.dbt.ndtp.jena.graphql.server.application;


import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.io.IOException;

import uk.gov.dbt.ndtp.jena.graphql.execution.DatasetExecutor;
import uk.gov.dbt.ndtp.jena.graphql.execution.TraversalExecutor;
import uk.gov.dbt.ndtp.jena.graphql.execution.ianode.graph.IANodeGraphExecutor;
import uk.gov.dbt.ndtp.jena.graphql.utils.ExcludeFromJacocoGeneratedReport;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.tdb2.TDB2Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A servlet context listener that configures the GraphQL executors for the server
 */
public class DatasetInitializer implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetInitializer.class);

    private DatasetGraph dsg;

    /**
     * Creates a new servlet context listener that will initialise the RDF Dataset that GraphQL queries will operate over
     */
    public DatasetInitializer() {
        dsg = null;
    }

    @ExcludeFromJacocoGeneratedReport
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Determine from our System properties what dataset (if any) we are using
        String location = System.getProperty("tdb.location");
        String data = System.getProperty("data.location");
        if (StringUtils.isNotBlank(location)) {
            LOGGER.info("Using persistent TDB Dataset at {}", location);
            this.dsg = TDB2Factory.connectDataset(location).asDatasetGraph();
        } else if (StringUtils.isNotBlank(data)) {
            LOGGER.info("Using non-persistent In-Memory Dataset read from file {}", data);
            this.dsg = DatasetGraphFactory.createTxnMem();
            RDFParserBuilder.create().source(data).build().parse(this.dsg);
        } else {
            LOGGER.info("Using non-persistent In-Memory Dataset");
            this.dsg = DatasetGraphFactory.createTxnMem();
        }

        // Then create the necessary executors so that the various endpoints can retrieve these later when they actually
        // handle queries
        try {
            sce.getServletContext()
               .setAttribute(DatasetExecutor.class.getCanonicalName(), new DatasetExecutor(this.dsg));
            sce.getServletContext()
               .setAttribute(TraversalExecutor.class.getCanonicalName(), new TraversalExecutor(this.dsg));
            sce.getServletContext()
               .setAttribute(IANodeGraphExecutor.class.getCanonicalName(), new IANodeGraphExecutor(this.dsg));
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialise Dataset GraphQL Executors", e);
        }
    }

    @ExcludeFromJacocoGeneratedReport
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (this.dsg != null) {
            this.dsg.close();
        }
    }
}
