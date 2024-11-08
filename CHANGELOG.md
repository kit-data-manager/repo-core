# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
* Enhanced searching capabilities for related identifiers with new methods in the RelatedIdentifierSpec class.
* Comprehensive unit tests added for the RelatedIdentifierSpec class to ensure robust functionality.
* Improved error messaging for missing publisher during updates in the DataResourceService.

### Fixed

### Security

### Deprecated

### Removed

## [1.2.3] - 2024-11-08

### Added
* Enhanced searching capabilities for related identifiers with new methods in the RelatedIdentifierSpec class.
* Comprehensive unit tests added for the RelatedIdentifierSpec class to ensure robust functionality.

### Fixed
* Fixed potential issue with unprivileged find
* Improved error messaging for missing publisher during updates in the DataResourceService.

### Security
* Update actions/setup-java action to v4.5.0
* Update dependency com.fasterxml.jackson.datatype:jackson-datatype-joda to v2.18.1.
* Update dependency com.fasterxml.jackson.datatype:jackson-datatype-jsr310 to v2.18.1
* Update dependency com.fasterxml.jackson.module:jackson-module-afterburner to v2.18.1
* Update dependency com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider to v2.18.1
* Update dependency com.google.code.gson:gson to v2.11.0
* Bump commons-io:commons-io from 2.16.1 to 2.17.0.
* Update dependency de.codecentric:spring-boot-admin-starter-client to v3.3.5
* Update dependency edu.kit.datamanager:service-base to v1.3.2
* Update plugin io.freefair.lombok to v8.10.2
* Update plugin io.freefair.maven-publish-java to v8.10.2
* Update dependency gradle to v8.10.2
* Update dependency jacoco to v0.8.12
* Bump org.apache.tika:tika-core from 2.9.2 to 3.0.0
* Update dependency org.javers:javers-spring-boot-starter-sql to v7.6.3
* Bump org.javers:javers-spring-boot-starter-sql from 7.6.2 to 7.6.3
* Update plugin org.owasp.dependencycheck to v11
* Bump org.postgresql:postgresql from 42.7.3 to 42.7.4.
* Update dependency org.springframework.boot:spring-boot-dependencies to v3.3.5
* Update dependency org.springframework.data:spring-data-elasticsearch to v5.3.5
* Update dependency org.springframework.restdocs:spring-restdocs-mockmvc to v3.0.2
* Update dependency org.springframework:spring-messaging to v6.1.14

### Deprecated

### Removed
* Fixed issue with privileged find for state 'REVOKED'

### Security
* Removed outdated configuration for GitHub Actions in the project setup.

## [1.2.2] - 2024-04-02

### Fixed
* Alignment of metadata and file version in ContentInformationService by @github-actions in https://github.com/kit-data-manager/repo-core/pull/271

### Security
* Update gradle to 8.5
* Bump actions/setup-java to 4.2.1
* Bump actions/checkout to 4
* Bump codecov/codecov-action to 4
* Bump github/codeql-action to 3
* Bump maven-publish-java to 8.6
* Bump jacoco to 0.8.11
* Bump javers-spring-boot-starter-sql to 7.4.2
* Bump springDocVersion to 2.5.0
* Bump convert to 4.0.2
* Bump tika-core to 2.9.1
* Bump dependencycheck to 9.1.0
* Bump commons-text to 1.11.0
* Bump commons-io to 2.16.0
* Bump postgresql to 42.7.2
* Bump h2 to 2.2.224
* Bump jackson-jaxrs-json-provider to 2.17.0
* Bump jackson-module-afterburner to 2.17.0
* Bump jackson-datatype-jsr310 to 2.17.0
* Bump jackson-datatype-joda to 2.17.0
* Bump dependency-management to 1.1.4
* Bump lombok to 8.6
* Bump dozer-core to 7.0.0
* Bump service-base to 1.2.1

## [1.2.1] - 2023-06-27

### Changed
- Revert to Gradle 7.6.1 due to deployment issues

## [1.2.0] - 2023-06-27

### Changed
- At least JDK 17 is now required.
- Bump some github actions from 2 to 3.
- Bump org.springframework.boot:spring-boot-dependencies from 2.7.7 to 3.1.0.
- Bump gradle from 7.6.1 to 8.1.1. 
- Bump service-base from 1.1.1 to 1.2.0.
- Bump JaVers from 6.14.0 to 7.0.0.

### Added

### Fixed
- Add check for empty ACL SID.

### Deprecated

### Removed

## [1.1.2] - 2023-03-17

### Changed
- ContentInformation metadata now returns own ETags different from the ETag of the parent resource.
- Creating resources from DataCite JSON metadata is now triggered by providing Content-Type 'application/vnd.datacite.org+json' at POST /api/v1/dataresources/.
- The allowed size of description content has been changed from 255 to 10240 characters (see 'Migration Remarks'). 
- Bump gson from 2.10 to 2.10.1 by @dependabot in https://github.com/kit-data-manager/repo-core/pull/128
- Bump httpclient from 4.5.13 to 4.5.14 by @dependabot in https://github.com/kit-data-manager/repo-core/pull/127
- Bump jackson-jaxrs-json-provider from 2.14.1 to 2.14.2 by @dependabot in https://github.com/kit-data-manager/repo-core/pull/139
- Bump io.freefair.lombok from 6.5.1 to 6.6.1 by @dependabot in https://github.com/kit-data-manager/repo-core/pull/140
- Bump edu.kit.datamanager.service-base from 1.1.0 to 1.1.1 

### Added
- Creating resources from Zenodo JSON metadata has been added and is triggered by providing Content-Type 'application/vnd.zenodo.org+json' at POST /api/v1/dataresources/.

### Fixed
- Health information no longer collected from RabbitMQMessagingService if messaging is disabled by @ThomasJejkal in https://github.com/kit-data-manager/repo-core/pull/134
- Removed remaining autowired loggers which caused NPE at runtime by @ThomasJejkal in https://github.com/kit-data-manager/repo-core/pull/132
- Creating resource from DataCite JSON metadata has been fixed.
- Fixed automatic mediaType detection for content upload, i.e., solve issue that text-based files are always detected as text/plain.

### Migration Remarks

For existing databases, a manual update is required to adjust the column size. 
The query may depend on the used database system, for PostgreSQL this would be: 

```
alter table description alter column description type character varying(10240);
``` 

## [1.1.1] - 2023-01-26

### Security

### Changed
- Bump gson from 2.10 to 2.10.1 by @dependabot in https://github.com/kit-data-manager/repo-core/pull/128
- Bump httpclient from 4.5.13 to 4.5.14 by @dependabot in https://github.com/kit-data-manager/repo-core/pull/127

### Added

### Fixed
- Autowired loggers removed to avoid NPE at runtime by @ThomasJejkal in https://github.com/kit-data-manager/repo-core/pull/132
- Health information no longer queried from RabbitMQMessagingService if messaging is disabled by @ThomasJejkal in https://github.com/kit-data-manager/repo-core/pull/134


### Deprecated

### Removed

## [1.1.0] - 2023-01-11

### Security

### Changed
- Listing resources now keeps ACL information in resources, on which the caller has ADMINISTRATE permissions
- Bump commons-text from 1.9 to 1.10.0 
- Bump service-base from 1.0.5 to 1.1.0
- Bump jaxb-core from 4.0.0 to 4.0.1 
- Bump io.spring.dependency-management from 1.0.13.RELEASE to 1.0.14.RELEASE.
- Bump spring-boot-admin-starter-client from 2.7.5 to 2.7.10
- Bump io.spring.dependency-management from 1.0.14.RELEASE to 1.1.0
- Bump org.owasp.dependencycheck from 7.2.1 to 7.4.1 
- Bump springDocVersion from 1.6.11 to 1.6.14
- Bump javers-spring-boot-starter-sql from 6.7.1 to 6.8.2
- Bump jackson-jaxrs-json-provider from 2.13.4 to 2.14.0 
- Bump jackson-datatype-joda from 2.13.4 to 2.14.0
- Bump tika-core from 2.5.0 to 2.6.0 
- Bump spring-boot-dependencies from 2.7.4 to 2.7.7
- Bump gson from 2.9.1 to 2.10 
- Bump postgresql from 42.5.0 to 42.5.1
- Bump jackson-datatype-joda from 2.14.0 to 2.14.1 
- Bump jackson-jaxrs-json-provider from 2.14.0 to 2.14.1
- Update gradle from 7.2 to 7.6

### Added

### Fixed

### Deprecated

### Removed

## [1.0.4] - 2022-10-13

### Changed

- Update to com.jfrog.bintray 1.8.5 
- Update to io.freefair.lombok 6.5.1
- Update to org.owasp.dependencycheck 7.2.1
- Update to io.freefair.maven-publish-java 6.5.1
- Update to io.spring.dependency-management 1.0.13.RELEASE
- Update to net.researchgate.release 3.0.2
- Update to logback-classic 1.2.11
- Update to gson 2.9.1
- Update to slf4j-api 1.7.36 
- Update to spring-boot 2.7.4
- Update to spring-doc 1.6.11
- Update to spring-messaging 5.3.23
- Update to javers 6.7.1
- Update to tika-core 2.5.0 
- Update to jackson-datatype-joda 2.13.3
- Update to postgresql 42.4.1
- Update to jackson-jaxrs-json-provider 2.13.3
- Update to jackson-module-afterburner 2.13.3
- Update to jackson-datatype-joda 2.13.3
- Update to spring-boot-admin-starter-client 2.7.4
- Update to jaxb-core 4.0.0
- Update to javassist 3.29.1-GA
- Update to service-base 1.0.5

### Fixed
- Fixed typo of property repo.plugin.storage.id.maxDepth
- Fixed authorization issue where users were able to access previous versions of resources even if for the current version all permissions were revoked
 
## [1.0.3] - 2022-07-31

### Changed
- Update to service-base 1.0.4
- Update to io.freefair.lombok 6.5.0.3
- Update to org.owasp.dependencycheck 7.1.1
- Update to net.researchgate.release 3.0.0
- Update to spring-boot 2.7.2
- Update to spring-doc 1.6.9
- Update to spring-messaging 5.3.22
- Update to spring-restdocs-mockmvc 2.0.6.RELEASE
- Update to postgresql 42.3.3

## [1.0.2] - 2022-03-25

### Changed
 - Update to service-base 1.0.1
 - Make AMQPMessageDao and RabbitMQMessagingService optional

## [1.0.1] - 2022-03-22

### Fixed
- Fix issue with path. (Windows) (issue #15)

## [1.0.0] - 2022-03-11

### Fixed
- Fix problem regarding jaVers. Make javers scope for deep search configurable.

### Changed
 - Update to service-base 1.0.0
 - Make mediatype detection in versioning services more reliable.
 - Make tests compatible to keycloak
 - Make tests compatible to JDK 17 

## [0.9.2] - 2021-11-13

### Security
 - Fixed shipped log4j configuration to be invulnerable to CVE-2021-44228

### Changed
 - Update to Spring-Boot 2.4.13
 - Update to dozer-core 6.5.2
 - Update to json-patch 1.13
 - Update to jackson-jaxrs-json-provider 2.13.0
 - Update to jackson-module-afterburner 2.13.0
 - Update to jackson-datatype-jsr310 2.13.0
 - Update to jackson-datatype-joda 2.13.0
 - Update to service-base 0.3.2

### Added
- none

### Fixed
- Improved error handling and added persistence for AMQP messages if message queue is offline

### Deprecated
- none

### Removed
- none

## [0.9.1] - 2021-11-12

### Changed
- Add index for last update for faster access.

## [0.9.0] - 2021-10-13
Extracted from the 'base-repo' project.

### Security
- none

### Changed
- Upgrade to Spring Boot 2.4.10
- Upgrade to Gradle 7.2
- Switch to 'service-base' version 0.3.0.

### Added
- Add service to get all versions of a digital object.
- Add storage service for hierarchical storage.

### Fixed
- none

### Deprecated
- none

### Removed
- none

[Unreleased]: https://github.com/kit-data-manager/repo-core/compare/v1.2.3...HEAD
[1.2.3]: https://github.com/kit-data-manager/repo-core/compare/v1.2.2...v1.2.3
[1.2.2]: https://github.com/kit-data-manager/repo-core/compare/v1.2.1...v1.2.2
[1.2.1]: https://github.com/kit-data-manager/repo-core/compare/v1.2.0...v1.2.1
[1.2.0]: https://github.com/kit-data-manager/repo-core/compare/v1.1.2...v1.2.0
[1.1.2]: https://github.com/kit-data-manager/repo-core/compare/v1.1.1...v1.1.2
[1.1.1]: https://github.com/kit-data-manager/repo-core/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/kit-data-manager/repo-core/compare/v1.0.4...v1.1.0
[1.0.4]: https://github.com/kit-data-manager/repo-core/compare/v1.0.3...v1.0.4
[1.0.3]: https://github.com/kit-data-manager/repo-core/compare/v1.0.2...v1.0.3
[1.0.2]: https://github.com/kit-data-manager/repo-core/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/kit-data-manager/repo-core/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/kit-data-manager/repo-core/compare/v0.9.2...v1.0.0
[0.9.2]: https://github.com/kit-data-manager/repo-core/compare/v0.9.1...v0.9.2
[0.9.1]: https://github.com/kit-data-manager/repo-core/compare/v0.9.0...v0.9.1
[0.9.0]: https://github.com/kit-data-manager/repo-core/releases/tag/v0.9.0
