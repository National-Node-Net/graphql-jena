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

package uk.gov.dbt.ndtp.jena.graphql.server.application.errors;


import graphql.execution.UnknownOperationException;
import uk.gov.dbt.ndtp.secure.agent.server.jaxrs.model.Problem;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.jena.web.HttpSC;

/**
 * A JAX-RS operation mapper that handles the GraphQL {@link UnknownOperationException} translating it into an <a
 * href="https://www.rfc-editor.org/rfc/rfc7807.html">RFC 7807</a> problem response.
 */
@Provider
public class UnknownOperationMapper implements ExceptionMapper<UnknownOperationException> {

    /**
     * Creates a new exception mapper that handles GraphQL {@link UnknownOperationException}
     */
    public UnknownOperationMapper() {
        // Stateless class
    }
    @Override
    public Response toResponse(UnknownOperationException exception) {
        //@formatter:off
        return new Problem("BadRequest",
                           "Unknown GraphQL Operation",
                           HttpSC.BAD_REQUEST_400,
                           exception.getMessage(),
                           null).toResponse();
        //@formatter:on
    }
}
