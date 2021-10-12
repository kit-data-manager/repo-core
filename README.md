# KIT Data Manager - Repository Service Core

[![build with gradle](https://github.com/kit-data-manager/repo-core/actions/workflows/gradle.yml/badge.svg)](https://github.com/kit-data-manager/repo-core/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/kit-data-manager/repo-core/branch/master/graph/badge.svg)](https://codecov.io/gh/kit-data-manager/repo-core)
![License](https://img.shields.io/github/license/kit-data-manager/repo-core.svg)

Library for building repos powered by KIT DM 2.0 services. It contains the core module 
for the repository microservice for the KIT DM infrastructure. The core module provides
basic services for data resource management, e.g. register DataCite-oriented metadata 
and upload/download content to data resources. It also provides commonly used dependencies 
and general purpose implementations, e.g. helpers and exceptions.

## How to build

In order to build this module you'll need:

* Java SE Development Kit 8 or higher

After obtaining the sources change to the folder where the sources are located and call:

```
user@localhost:/home/user/repo-core$ ./gradlew publishToMavenLocal
BUILD SUCCESSFUL in 1s
3 actionable tasks: 3 executed
user@localhost:/home/user/repo-core$
```

The gradle wrapper will download and install gradle, if not already available. Afterwards, the module artifact
will be built and installed into the local maven repository, from where it can be used by other projects.

## Dependency from Maven Central Repository

Instead of using a local build you may also use the most recent version from the Central Maven Repository directly. 

### Maven

~~~~
<dependency>
    <groupId>edu.kit.datamanager</groupId>
    <artifactId>repo-core</artifactId>
    <version>0.9.0</version>
</dependency>
~~~~

### Gradle (short)

~~~~
    implementation "edu.kit.datamanager:repo-core:0.9.0"
~~~~

## License

This library is licensed under the Apache License, Version 2.0.
