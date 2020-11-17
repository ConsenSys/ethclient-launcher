[![ethclient-launcher Actions Status](https://github.com/ConsenSys/ethclient-launcher/workflows/ethclient-launcher-ci/badge.svg)](https://github.com/ConsenSys/ethclient-launcher/actions)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/ConsenSys/ethclient-launcher/blob/master/LICENSE)
[![sonar-quality-gate][sonar-quality-gate]][sonar-url] [![sonar-coverage][sonar-coverage]][sonar-url] [![sonar-bugs][sonar-bugs]][sonar-url] [![sonar-vulnerabilities][sonar-vulnerabilities]][sonar-url]
# Ethclient launcher

## Description

Ethereum client launcher

## Installation

### Install Prerequisites

* Java 11

### Build Instructions

```shell script
./gradlew assemble
```

## Code Style

We use Google's Java coding conventions for the project. To reformat code, run: 

```shell script 
./gradlew spotlessApply
```

Code style is checked automatically during a build.

[sonar-url]: https://sonarcloud.io/dashboard?id=ConsenSys_ethclient-launcher
[sonar-quality-gate]: https://sonarcloud.io/api/project_badges/measure?project=ConsenSys_ethclient-launcher&metric=alert_status
[sonar-coverage]: https://sonarcloud.io/api/project_badges/measure?project=ConsenSys_ethclient-launcher&metric=coverage
[sonar-bugs]: https://sonarcloud.io/api/project_badges/measure?project=ConsenSys_ethclient-launcher&metric=bugs
[sonar-vulnerabilities]: https://sonarcloud.io/api/project_badges/measure?project=ConsenSys_ethclient-launcher&metric=vulnerabilities