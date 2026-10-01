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

package uk.gov.dbt.ndtp.jena.graphql.fuseki;

import org.apache.jena.sparql.util.Context;
import uk.gov.dbt.ndtp.jena.graphql.execution.GraphQLOverDatasetExecutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.atlas.lib.Version;
import org.apache.jena.atlas.logging.FmtLog;
import org.apache.jena.fuseki.Fuseki;
import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.fuseki.main.sys.FusekiAutoModule;
import org.apache.jena.fuseki.server.DataAccessPoint;
import org.apache.jena.fuseki.server.Endpoint;
import org.apache.jena.fuseki.server.Operation;
import org.apache.jena.fuseki.servlets.ActionProcessor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * A Fuseki module that provides a GraphQL operation that can be used to accept GraphQL queries against the underlying
 * Dataset using an arbitrary schema as provided by the underlying {@link GraphQLOverDatasetExecutor} implementation.
 */
public class FMod_GraphQL implements FusekiAutoModule {

    private static final String VERSION = Version.versionForClass(FMod_GraphQL.class).orElse("<development>");

    /**
     * Creates a new Fuseki GraphQL Module
     */
    public FMod_GraphQL() {
        SysGraphQL.init();
    }

    @Override
    public String name() {
        return "GraphQL Queries";
    }

    @Override
    public void start() {
        SysGraphQL.init();
    }

    @Override
    public void prepare(FusekiServer.Builder serverBuilder, Set<String> datasetNames, Model configModel) {
        FmtLog.info(Fuseki.configLog, "GraphQL Fuseki Module (%s)", VERSION);
    }

    @Override
    public void configDataAccessPoint(DataAccessPoint dap, Model configModel) {
        // Wire up any GraphQL operations correctly
        dap.getDataService().forEachEndpoint(e -> setEndpointProcessor(dap, e));
    }

    /**
     * Creates the action processor, an instance of the {@link ActionProcessor}
     * interface, that will handle the processing of the GraphQL requests
     * <p>
     * The default is that this is an instance of the {@link ActionGraphQL} from this module.  However developers who
     * wish to extend the default GraphQL processing behaviour may wish to create their own custom modules that extends
     * this module and substitutes an extended action processor.
     * </p>
     *
     * @param executor The GraphQL executor defined for the endpoint
     * @return Action processor
     */
    protected ActionProcessor createActionProcessor(GraphQLOverDatasetExecutor executor) {
        return new ActionGraphQL(executor);
    }

    private String endpointName(DataAccessPoint dap, Endpoint endpoint) {
        if (endpoint.isUnnamed()) {
            return dap.getName();
        }
        return dap.getName() + "/" + endpoint.getName();
    }

    private void setEndpointProcessor(DataAccessPoint dap, Endpoint e) {
        Operation op = e.getOperation();

        if (!SysGraphQL.OP_GRAPHQL.equals(op)) {
            return;
        }

        // Issue a warning if GraphQL operation configured on an endpoint that does not end with /graphql
        String endpointPath = endpointName(dap, e);
        if (!StringUtils.endsWith(endpointPath, "/graphql")) {
            // NB - There's a limitation of how Fuseki routes requests in that it only supports one path segment
            // after the dataset i.e. you can have /ds/graphql but can't have /ds/foo/graphql
            // This means that if you want to have multiple GraphQL endpoints (to support different schemas)
            // then each would have to have a unique single path segment name, e.g. foo-graphql and bar-graphql,
            // and so they inherently will always hit this warning.
            FmtLog.warn(Fuseki.configLog,
                        "GraphQL Operation configured on endpoint %s which does not end in /graphql as recommended by the GraphQL over HTTP Specification",
                        endpointPath);
        }

        // Find the context property that defines the executor to use and create an instance of it
        Context context = e.getContext();
        String executorCls = null;

        if (context != null) {
            executorCls = context.getAsString(VocabGraphQL.EXECUTOR);
        }

        if (StringUtils.isBlank(executorCls)) {
            FmtLog.error(Fuseki.configLog,
                         "%s configured to use GraphQL but no graphql:executor property specified in the endpoint context",
                         endpointPath);
            return;
        }

        try {
            Class<?> cls = Class.forName(executorCls);
            // Enforce correct interface implemented
            if (!GraphQLOverDatasetExecutor.class.isAssignableFrom(cls)) {
                FmtLog.error(Fuseki.configLog,
                             "%s configured to use GraphQL executor %s which does not implement the required GraphQLOverDatasetExecutor interface",
                             endpointPath, cls);
                return;
            }
            GraphQLOverDatasetExecutor executor =
                    (GraphQLOverDatasetExecutor) cls.getConstructor(DatasetGraph.class)
                                                    .newInstance(DatasetGraphFactory.empty());
            FmtLog.info(Fuseki.configLog, "%s accepts GraphQL Requests using executor %s", endpointPath,
                        executorCls);
            ActionProcessor processor = createActionProcessor(executor);
            e.setProcessor(processor);
        } catch (ClassNotFoundException cnfEx) {
            FmtLog.error(Fuseki.configLog,
                         "%s configured to use GraphQL executor %s which is not found on your Classpath",
                         endpointPath, executorCls);
        } catch (NoSuchMethodException ex) {
            FmtLog.error(Fuseki.configLog,
                         "%s configured to use GraphQL executor %s which does not provide a public single argument constructor that takes a DatasetGraph",
                         endpointPath, executorCls);
        } catch (InvocationTargetException ex) {
            FmtLog.error(Fuseki.configLog,
                         "%s configured to use GraphQL executor %s whose constructor threw an error: %s",
                         endpointPath, executorCls,
                         ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
        } catch (InstantiationException ex) {
            FmtLog.error(Fuseki.configLog,
                         "%s configured to use GraphQL executor %s which is not a class that permits instantiation",
                         endpointPath, executorCls);
        } catch (IllegalAccessException ex) {
            FmtLog.error(Fuseki.configLog,
                         "%s configured to use GraphQL executor %s whose constructor is not accessible",
                         endpointPath, executorCls);
        }
    }
}
