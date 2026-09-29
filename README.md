# README  

**Repository:** `graphql-jena`  
**Description:** `This repository provides GraphQL query capabilities as extensions to Apache Jena. This is done by providing common functionality around GraphQL queries over RDF data backed by Apache Jena APIs.` 

<!-- SPDX-License-Identifier: Apache-2.0 AND OGL-UK-3.0 -->

## Overview  
This repository contributes to the development of **secure, scalable, and interoperable data-sharing infrastructure**. It supports NDTP’s mission to enable **trusted, federated, and decentralised** data-sharing across organisations.  

This repository is one of several open-source components that underpin NDTP’s **Integration Architecture (IA)**—a framework designed to allow organisations to manage and exchange data securely while maintaining control over their own information. The IA is actively deployed and tested across multiple sectors, ensuring its adaptability and alignment with real-world needs. 

For a complete overview of the Integration Architecture (IA) project, please see the [Integration Architecture 
Documentation](https://github.com/National-Node-Net/integration-architecture-documentation).

## Prerequisites  
Before using this repository, ensure you have the following dependencies installed:  
- **Required Tooling:** 
    - Java 17
    - Github PAT token set to allow retrieval of maven packages from Github Packages
- **Pipeline Requirements:** N/A
- **Supported Kubernetes Versions:** N/A
- **System Requirements:** 
    - Java 17

## Quick Start  
Follow these steps to get started quickly with this repository. For detailed installation, configuration, and deployment, refer to the relevant MD files.  

### 1. Download and Build  
```sh  
git clone https://github.com/National-Node-Net/graphql-jena.git
cd graphql-jena  
```
### 2. Run Build  
```sh  
mvn clean install
```

See [Build](BUILD.md) for more detailed requirements and instructions.

### 3. Usage
To use these modules in your own projects just declare an appropriate dependency.  For example using Maven:

```xml
<dependency>
  <groupId>uk.gov.dbt.ndtp.jena.graphql</groupId>
  <artifactId>ARTIFACT_ID</artifactId>
  <version>VERSION</version>
</dependency>
```

Where `ARTIFACT_ID` and `VERSION` are replaced with appropriate values.  For release history and changes see the [Change Log](CHANGELOG.md).

Further instructions and examples can be found in the [quick start usage document](docs/QuickstartUsage.md).

#### Usage Documentation
Please refer to the full [Documentation](docs/index.md) for more detailed usage examples and explanations of how you can
use the APIs provided in these modules to implement your own GraphQL Schemas over Jena `DatasetGraph`.

## Features  
- **Key functionality**  
    - Enables GraphQL queries over RDF data using Apache Jena APIs.  
    - Provides a seamless interface for querying and managing linked data.  
    - Supports advanced query capabilities, including filtering, sorting, and pagination.  

- **Key integrations**  
    - Integrates with Apache Jena for RDF data storage and processing.  
    - Compatible with GitHub Packages for dependency management.  
    - Designed to work within the National Digital Twin Programme’s Integration Architecture.  
    - Supports integration with external GraphQL clients and tools.  

- **Scalability & performance** : N/A

- **Modularity**
    - Compatible with Java-based projects through Maven dependency management.
    - Designed to work within the National Digital Twin Programme’s Integration Architecture.

## Testing Guide

### Running Unit Tests
Navigate to the root of the project and run `mvn test` to run the tests for the repository.

## API Documentation  
Documentation detailing the relevant configuration and endpoints is provided [here](docs/configuration-secure-agent-graph.md ). 


## Public Funding Acknowledgment  
This repository has been developed with public funding as part of the National Digital Twin Programme (NDTP), a UK Government initiative. NDTP, alongside its partners, has invested in this work to advance open, secure, and reusable digital twin technologies for any organisation, whether from the public or private sector, irrespective of size.  

## License  
This repository contains both source code and documentation, which are covered by different licenses:  
- **Code:** Originally developed by Telicent UK Ltd, now maintained by National Digital Twin Programme. Licensed under the [Apache License 2.0](LICENSE.md).  
- **Documentation:** Licensed under the [Open Government Licence (OGL) v3.0](OGL_LICENSE.md).  

By contributing to this repository, you agree that your contributions will be licensed under these terms.

See [LICENSE.md](LICENSE.md), [OGL_LICENSE.md](OGL_LICENSE.md), and [NOTICE.md](NOTICE.md) for details.  

## Security and Responsible Disclosure  
We take security seriously. If you believe you have found a security vulnerability in this repository, please follow our responsible disclosure process outlined in [SECURITY.md](SECURITY.md).  

## Contributing  
We welcome contributions that align with the Programme’s objectives. Please read our [Contributing](CONTRIBUTING.md) guidelines before submitting pull requests.  

## Acknowledgements  
This repository has benefited from collaboration with various organisations. For a list of acknowledgments, see [ACKNOWLEDGEMENTS.md](ACKNOWLEDGEMENTS.md).  

## Support and Contact  
For questions or support, check our Issues or contact the NDTP team on ndtp@businessandtrade.gov.uk.

**Maintained by the National Digital Twin Programme (NDTP).**  

© Crown Copyright 2026. This work has been developed by the National Digital Twin Programme and is legally attributed to the UK's Department for Business, Innovation, Science and Trade (BIST) as the governing entity.
