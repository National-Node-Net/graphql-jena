# GraphQL Extensions for Apache Jena Usage
**Repository:** `graphql-jena`  
**Description:** `This documentation provides details on the APIs and Tools in this repository and how to utilise them.`  
<!-- SPDX-License-Identifier: OGL-UK-3.0 -->

Please refer to [Depending on these Modules](../README.md#depending-on-these-modules) for how to declare dependencies on 
these modules in other projects.

## IANode API

The `graphql-jena-lib` module provides the [IANode APIs](IANode-apis) and [schemas](schemas.md) for running GraphQL
queries over RDF data.  It primarily serves to demonstrate how to use the `graphql-java` to build up an executable
GraphQL schema over Jena backed RDF data.  You'll also find a bunch of helper utilities and constants for reuse in other
modules here.

### IANode GraphQL Schema

The `ianode-graph-schema` module provides a IANode specific schema and associated execution logic for use by our
IANode Graph application.

## GraphQL Server

The `graphql-server` module provides a standalone HTTP server that offers GraphQL access to RDF data.  See the
[Standalone Server](standalone-server.md) documentation for more details.

## Fuseki Module

The `graphql-fuseki-module` module provides a [Fuseki Module](fuseki-module.md) that can be added to a Fuseki server
deployment to allow adding GraphQL endpoints to a Fuseki dataset using Fuseki's configuration syntax.

---

© Crown Copyright 2026. This work has been developed by the National Digital Twin Programme and is legally attributed to the UK's Department for Business, Innovation, Science and Trade (BIST) as the
governing entity.  
Licensed under the Open Government Licence v3.0.
