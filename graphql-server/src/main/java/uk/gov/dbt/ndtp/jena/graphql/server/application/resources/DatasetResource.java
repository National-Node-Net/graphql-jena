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

package uk.gov.dbt.ndtp.jena.graphql.server.application.resources;


import jakarta.servlet.ServletContext;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import uk.gov.dbt.ndtp.jena.graphql.execution.DatasetExecutor;
import uk.gov.dbt.ndtp.jena.graphql.execution.TraversalExecutor;
import uk.gov.dbt.ndtp.jena.graphql.schemas.DatasetSchema;
import uk.gov.dbt.ndtp.jena.graphql.schemas.TraversalSchema;
import uk.gov.dbt.ndtp.jena.graphql.execution.ianode.graph.IANodeGraphExecutor;
import uk.gov.dbt.ndtp.jena.graphql.server.model.GraphQLOverHttp;
import uk.gov.dbt.ndtp.jena.graphql.server.model.GraphQLRequest;

/**
 * A JAX-RS resource that provides access to GraphQL queries using the
 * {@link DatasetSchema} and {@link TraversalSchema}
 */
@Path("/dataset")
public class DatasetResource extends AbstractGraphQLResource {

    /**
     * Creates a resource that can answer GraphQL queries using the basic GraphQL schemas we've defined
     */
    public DatasetResource() {
        // Stateless class instance
    }

    /**
     * GET requests using the {@link DatasetSchema}
     *
     * @param query          GraphQL Query
     * @param operationName  GraphQL Operation name
     * @param variables      GraphQL variables
     * @param extensions     GraphQL extensions
     * @param servletContext Servlet context
     * @return Response
     */
    @Path("/graphql")
    @GET
    @Produces({ GraphQLOverHttp.CONTENT_TYPE_GRAPHQL_RESPONSE_JSON, "application/problem+json" })
    public Response quads(@QueryParam(GraphQLOverHttp.PARAMETER_QUERY) @NotNull String query,
                          @QueryParam(GraphQLOverHttp.PARAMETER_OPERATION_NAME) String operationName,
                          @QueryParam(GraphQLOverHttp.PARAMETER_VARIABLES) String variables,
                          @QueryParam(GraphQLOverHttp.PARAMETER_EXTENSIONS) String extensions,
                          @Context ServletContext servletContext) {
        return executeOrValidateGraphQL(query, operationName, variables, extensions, servletContext, DatasetExecutor.class, false);
    }

    /**
     * POST requests using the {@link DatasetSchema}
     *
     * @param request        GraphQL Request
     * @param servletContext Servlet context
     * @return Response
     */
    @Path("/graphql")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ GraphQLOverHttp.CONTENT_TYPE_GRAPHQL_RESPONSE_JSON, "application/problem+json" })
    public Response postQuads(GraphQLRequest request, @Context ServletContext servletContext) {
        return executeOrValidateGraphQL(request.getQuery(), request.getOperationName(), request.getVariables(),
                                        request.getExtensions(), servletContext, DatasetExecutor.class, false);
    }

    /**
     * GET requests using the {@link TraversalSchema}
     *
     * @param query          GraphQL Query
     * @param operationName  GraphQL Operation name
     * @param variables      GraphQL variables
     * @param extensions     GraphQL extensions
     * @param servletContext Servlet context
     * @return Response
     */
    @Path("/traversal/graphql")
    @GET
    @Produces({ GraphQLOverHttp.CONTENT_TYPE_GRAPHQL_RESPONSE_JSON, "application/problem+json" })
    public Response traverse(@QueryParam(GraphQLOverHttp.PARAMETER_QUERY) @NotNull String query,
                             @QueryParam(GraphQLOverHttp.PARAMETER_OPERATION_NAME) String operationName,
                             @QueryParam(GraphQLOverHttp.PARAMETER_VARIABLES) String variables,
                             @QueryParam(GraphQLOverHttp.PARAMETER_EXTENSIONS) String extensions,
                             @Context ServletContext servletContext) {
        return executeOrValidateGraphQL(query, operationName, variables, extensions, servletContext, TraversalExecutor.class, false);
    }

    /**
     * POST requests using the {@link TraversalSchema}
     *
     * @param request        GraphQL Request
     * @param servletContext Servlet context
     * @return Response
     */
    @Path("/traversal/graphql")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ GraphQLOverHttp.CONTENT_TYPE_GRAPHQL_RESPONSE_JSON, "application/problem+json" })
    public Response postTraversal(GraphQLRequest request, @Context ServletContext servletContext) {
        return executeOrValidateGraphQL(request.getQuery(), request.getOperationName(), request.getVariables(),
                                        request.getExtensions(), servletContext, TraversalExecutor.class, false);
    }


    /**
     * GET requests using the {@link uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.IANodeGraphSchema}
     *
     * @param query          GraphQL Query
     * @param operationName  GraphQL Operation name
     * @param variables      GraphQL variables
     * @param extensions     GraphQL extensions
     * @param servletContext Servlet context
     * @return Response
     */
    @Path("/ianode/graphql")
    @GET
    @Produces({ GraphQLOverHttp.CONTENT_TYPE_GRAPHQL_RESPONSE_JSON, "application/problem+json" })
    public Response ianode(@QueryParam(GraphQLOverHttp.PARAMETER_QUERY) @NotNull String query,
                             @QueryParam(GraphQLOverHttp.PARAMETER_OPERATION_NAME) String operationName,
                             @QueryParam(GraphQLOverHttp.PARAMETER_VARIABLES) String variables,
                             @QueryParam(GraphQLOverHttp.PARAMETER_EXTENSIONS) String extensions,
                             @Context ServletContext servletContext) {
        return executeOrValidateGraphQL(query, operationName, variables, extensions, servletContext, IANodeGraphExecutor.class, false);
    }

    /**
     * POST requests using the {@link uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.IANodeGraphSchema}
     *
     * @param request        GraphQL Request
     * @param servletContext Servlet context
     * @return Response
     */
    @Path("/ianode/graphql")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ GraphQLOverHttp.CONTENT_TYPE_GRAPHQL_RESPONSE_JSON, "application/problem+json" })
    public Response postIANode(GraphQLRequest request, @Context ServletContext servletContext) {
        return executeOrValidateGraphQL(request.getQuery(), request.getOperationName(), request.getVariables(),
                                        request.getExtensions(), servletContext, IANodeGraphExecutor.class, false);
    }

    /**
     * GET requests using the {@link DatasetSchema}
     *
     * @param query          GraphQL Query
     * @param operationName  GraphQL Operation name
     * @param variables      GraphQL variables
     * @param extensions     GraphQL extensions
     * @param servletContext Servlet context
     * @return Response
     */

    @Path("/validate")
    @GET
    @Produces({ GraphQLOverHttp.CONTENT_TYPE_GRAPHQL_RESPONSE_JSON, "application/problem+json" })
    public Response getValidate(@QueryParam(GraphQLOverHttp.PARAMETER_QUERY) @NotNull String query,
                                @QueryParam(GraphQLOverHttp.PARAMETER_OPERATION_NAME) String operationName,
                                @QueryParam(GraphQLOverHttp.PARAMETER_VARIABLES) String variables,
                                @QueryParam(GraphQLOverHttp.PARAMETER_EXTENSIONS) String extensions,
                                @Context ServletContext servletContext) {
        return executeOrValidateGraphQL(query, operationName, variables, extensions, servletContext, DatasetExecutor.class, true);
    }


    /**
     * POST requests using the {@link DatasetSchema}
     *
     * @param request        GraphQL Request
     * @param servletContext Servlet context
     * @return Response
     */
    @Path("/validate")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ GraphQLOverHttp.CONTENT_TYPE_GRAPHQL_RESPONSE_JSON, "application/problem+json" })
    public Response postValidate(GraphQLRequest request, @Context ServletContext servletContext) {
        return executeOrValidateGraphQL(request.getQuery(), request.getOperationName(), request.getVariables(),
                                        request.getExtensions(), servletContext, DatasetExecutor.class, true);
    }


    /**
     * GET requests using the {@link TraversalSchema}
     *
     * @param query          GraphQL Query
     * @param operationName  GraphQL Operation name
     * @param variables      GraphQL variables
     * @param extensions     GraphQL extensions
     * @param servletContext Servlet context
     * @return Response
     */
    @Path("/traversal/validate")
    @GET
    @Produces({ GraphQLOverHttp.CONTENT_TYPE_GRAPHQL_RESPONSE_JSON, "application/problem+json" })
    public Response validateGetTraversal(@QueryParam(GraphQLOverHttp.PARAMETER_QUERY) @NotNull String query,
                                         @QueryParam(GraphQLOverHttp.PARAMETER_OPERATION_NAME) String operationName,
                                         @QueryParam(GraphQLOverHttp.PARAMETER_VARIABLES) String variables,
                                         @QueryParam(GraphQLOverHttp.PARAMETER_EXTENSIONS) String extensions,
                                         @Context ServletContext servletContext) {
        return executeOrValidateGraphQL(query, operationName, variables, extensions, servletContext, TraversalExecutor.class, true);
    }

    /**
     * POST requests using the {@link TraversalSchema}
     *
     * @param request        GraphQL Request
     * @param servletContext Servlet context
     * @return Response
     */
    @Path("/traversal/validate")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ GraphQLOverHttp.CONTENT_TYPE_GRAPHQL_RESPONSE_JSON, "application/problem+json" })
    public Response validatePostTraversal(GraphQLRequest request, @Context ServletContext servletContext) {
        return executeOrValidateGraphQL(request.getQuery(), request.getOperationName(), request.getVariables(),
                                        request.getExtensions(), servletContext, TraversalExecutor.class, true);
    }

    /**
     * HEAD requests using the {@link uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.IANodeGraphSchema}
     *
     * @param query          GraphQL Query
     * @param operationName  GraphQL Operation name
     * @param variables      GraphQL variables
     * @param extensions     GraphQL extensions
     * @param servletContext Servlet context
     * @return Response
     */
    @Path("/ianode/validate")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ GraphQLOverHttp.CONTENT_TYPE_GRAPHQL_RESPONSE_JSON, "application/problem+json" })
    public Response getValidateIANode(@QueryParam(GraphQLOverHttp.PARAMETER_QUERY) String query,
                                        @QueryParam(GraphQLOverHttp.PARAMETER_OPERATION_NAME) String operationName,
                                        @QueryParam(GraphQLOverHttp.PARAMETER_VARIABLES) String variables,
                                        @QueryParam(GraphQLOverHttp.PARAMETER_EXTENSIONS) String extensions,
                                        @Context ServletContext servletContext) {
        return executeOrValidateGraphQL(query, operationName, variables, extensions, servletContext, IANodeGraphExecutor.class, true);
    }

    /**
     * POST requests using the {@link uk.gov.dbt.ndtp.jena.graphql.schemas.ianode.graph.IANodeGraphSchema}
     *
     * @param request        GraphQL Request
     * @param servletContext Servlet context
     * @return Response
     */
    @Path("/ianode/validate")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ GraphQLOverHttp.CONTENT_TYPE_GRAPHQL_RESPONSE_JSON, "application/problem+json" })
    public Response postValidateIANode(GraphQLRequest request, @Context ServletContext servletContext) {
        return executeOrValidateGraphQL(request.getQuery(), request.getOperationName(), request.getVariables(),
                                        request.getExtensions(), servletContext, IANodeGraphExecutor.class,true);
    }

}
