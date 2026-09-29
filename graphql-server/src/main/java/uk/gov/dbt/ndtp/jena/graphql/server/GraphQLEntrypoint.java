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

package uk.gov.dbt.ndtp.jena.graphql.server;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.annotations.AirlineModule;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Directory;
import com.github.rvesse.airline.annotations.restrictions.MutuallyExclusiveWith;
import com.github.rvesse.airline.annotations.restrictions.Port;
import com.github.rvesse.airline.annotations.restrictions.PortType;
import com.github.rvesse.airline.parser.errors.ParseException;
import uk.gov.dbt.ndtp.jena.graphql.server.application.DatasetInitializer;
import uk.gov.dbt.ndtp.secure.agent.server.jaxrs.applications.AbstractAppEntrypoint;
import uk.gov.dbt.ndtp.secure.agent.server.jaxrs.applications.CorsConfigurationBuilder;
import uk.gov.dbt.ndtp.secure.agent.server.jaxrs.applications.ServerBuilder;

import java.io.File;

import uk.gov.dbt.ndtp.jena.graphql.server.application.GraphQLApplication;
import uk.gov.dbt.ndtp.jena.graphql.utils.ExcludeFromJacocoGeneratedReport;

/**
 * The CLI entrypoint for the standalone GraphQL server
 */
@Command(name = "graphql-jena-server", description = "Runs a standalone HTTP Server offering a GraphQL API over a Jena Dataset")
public class GraphQLEntrypoint extends AbstractAppEntrypoint {

    @Option(name = {
            "-l",
            "--loc",
            "--location"
    }, title = "TdbDirectory", description = "Specifies a path to a pre-existing TDB database to provide GraphQL access over")
    @Directory(mustExist = true)
    @MutuallyExclusiveWith(tag = "data-source")
    private File location;

    @Option(name = {
            "-p",
            "--port"
    }, title = "Port", description = "Specifies the port to run the server on.  Defaults to 11666.")
    @Port(acceptablePorts = { PortType.ANY })
    private int port = 11666;

    @Option(name = {
            "-d",
            "--data"
    }, title = "RdfFile", description = "Specifies a path to a pre-existing RDF file to use as the basis of an in-memory dataset to provide GraphQL access over")
    @com.github.rvesse.airline.annotations.restrictions.File(mustExist = true)
    @MutuallyExclusiveWith(tag = "data-source")
    private File data;

    @AirlineModule
    private HelpOption<GraphQLEntrypoint> help = new HelpOption<>();

    @Override
    protected ServerBuilder buildServer() {
        return ServerBuilder.create()
                            .application(GraphQLApplication.class)
                            .displayName("Standalone GraphQL Jena Server")
                            .localhost()
                            .port(this.port)
                            .withCors(CorsConfigurationBuilder::withDefaults)
                            .withListener(DatasetInitializer.class);
    }

    /**
     * Creates a new entrypoint for running the GraphQL Server application
     */
    public GraphQLEntrypoint() {
        // Stateless class
    }

    /**
     * The main method for the GraphQL server
     *
     * @param args Arguments
     */
    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        try {
            GraphQLEntrypoint entrypoint = SingleCommand.singleCommand(GraphQLEntrypoint.class).parse(args);

            if (entrypoint.help.showHelpIfRequested()) {
                System.exit(0);
            }

            if (entrypoint.location != null) {
                System.setProperty("tdb.location", entrypoint.location.getAbsolutePath());
            } else if (entrypoint.data != null) {
                System.setProperty("data.location", entrypoint.data.getAbsolutePath());
            }

            entrypoint.run(true);
            System.exit(0);
        } catch (ParseException e) {
            System.err.println("Failed to parse command line arguments: " + e.getMessage());
            System.exit(1);
        }
    }
}
