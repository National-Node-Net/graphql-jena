# Quick Start Usage
**Repository:** `graphql-jena`  
**Description:** `The document demonstrates how to execute a basic GraphQL query on an RDF dataset using dataset graph APIs`  
<!-- SPDX-License-Identifier: OGL-UK-3.0 -->

The following is a simple example of using these APIs with our [Dataset](schemas.md#dataset) schema to make a very basic
GraphQL query against a dataset:

```java
DatasetGraph dsg = DatasetGraphFactory.createTxnMem();
RDFParserBuilder.create()
                .source("example-data.trig")
                .build()
                .parse(dsg);

DatasetExecution executor = new DatasetExecution(dsg);

// Define our query
String query = """
query {
    quads {
        subject {
            kind
            value
        }
    }
}""";
ExecutionResult result = executor.execute(query);

// Do something with the results, it's a bit clunky as GraphQL returns everything as a Map<String, Object>
Map<String, Object> data = result.getData();
if (data.containsKey("quads")) {
    List<Map<String, Object>> quads = data.get("quads");
    for (Map<String, Object> quad : quads) {
        Map<String, Object> subject = quad.get("subject");
        System.out.println(subject.get("value"));
    }
}
```

A more typical use case is to expose a GraphQL endpoint as part of a Fuseki server which can be done by modifying your
Fuseki configuration file e.g.

```ttl
fuseki:endpoint [ fuseki:operation graphql:graphql ;
                  ja:context [ ja:cxtName "graphql:executor" ;
                               ja:cxtValue "uk.gov.dbt.ndtp.jena.graphql.execution.DatasetExecutor" ];
                  fuseki:name "dataset-graphql" ];
```

See the [Fuseki Module](fuseki-module.md) for more detailed configuration instructions.

---

© Crown Copyright 2026. This work has been developed by the National Digital Twin Programme and is legally attributed to the UK's Department for Business, Innovation, Science and Trade (BIST) as the
governing entity.  
Licensed under the Open Government Licence v3.0.
