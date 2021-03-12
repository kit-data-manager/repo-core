# KIT Data Manager - Base Repository Service

![Build Status](https://img.shields.io/travis/kit-data-manager/base-repo.svg)
![Code Coverage](https://img.shields.io/coveralls/github/kit-data-manager/base-repo.svg)
![License](https://img.shields.io/github/license/kit-data-manager/repo-core.svg)
![Docker Cloud Build Status](https://img.shields.io/docker/cloud/build/kitdm/repo-core)
![Docker Image Version (latest semver)](https://img.shields.io/docker/v/kitdm/repo-core)

This project contains the core module for the repository microservice for the KIT DM infrastructure. The core module provides
basic services for data resource management, e.g. register DataCite-oriented metadata and upload/download content to data resources.

## How to build

In order to build this microservice you'll need:

* Java SE Development Kit 8 or higher

After obtaining the sources change to the folder where the sources are located perform the following steps:

```
user@localhost:/home/user/repo-core$ ./gradlew clean build
> Configure project :
Using release profile for building base-repo
<-------------> 0% EXECUTING [0s]
[...]
user@localhost:/home/user/repo-core$
```

The Gradle wrapper will now take care of downloading the configured version of 
Gradle, checking out all required libraries to build this library.

## How to start

### Prerequisites

* PostgreSQL 9.1 or higher
* RabbitMQ 3.7.3 or higher (in case you want to use the messaging feature, which is recommended)


## More Information

* [Information about the DataCite metadata schema](https://schema.datacite.org/)

## License

The KIT Data Manager is licensed under the Apache License, Version 2.0.
