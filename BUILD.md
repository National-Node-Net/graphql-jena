# Building GraphQL Extensions for Apache Jena
**Repository:** `graphql-jena`  
**Description:** `Build instructions for graphql-jena`  
<!-- SPDX-License-Identifier: OGL-UK-3.0 -->

This project is a Java based project built with Apache Maven.

## Requirements

- JDK 21+
- Apache Maven 3.8.3+

## Build

A straightforward Maven build is provided:

```bash
$ mvn clean install
```

## Test

Decent test coverage is provided and enforced during builds via the Jacoco Plugin **BUT** users should be aware that
this project is experimental and there may be bugs, or scenarios we haven't tested.

Logging is disabled in tests but can be enabled with the following:
```bash
$ mvn test -Dlogback.configurationFile=logback-debug.xml
```

© Crown Copyright 2026. This work has been developed by the National Digital Twin Programme and is legally attributed to the UK's Department for Business, Innovation, Science and Trade (BIST) as the
governing entity.  
Licensed under the Open Government Licence v3.0.
