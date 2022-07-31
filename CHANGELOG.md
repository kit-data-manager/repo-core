# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Security

### Changed

### Added

### Fixed

### Deprecated

### Removed

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

[Unreleased]: https://github.com/kit-data-manager/repo-core/compare/v1.0.3...HEAD
[1.0.3]: https://github.com/kit-data-manager/repo-core/compare/v1.0.2...v1.0.3
[1.0.2]: https://github.com/kit-data-manager/repo-core/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/kit-data-manager/repo-core/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/kit-data-manager/repo-core/compare/v0.9.2...v1.0.0
[0.9.2]: https://github.com/kit-data-manager/repo-core/compare/v0.9.1...v0.9.2
[0.9.1]: https://github.com/kit-data-manager/repo-core/compare/v0.9.0...v0.9.1
[0.9.0]: https://github.com/kit-data-manager/repo-core/releases/tag/v0.9.0
