[![quorum-mainnet-launcher Actions Status](https://github.com/ConsenSys/quorum-mainnet-launcher/workflows/quorum-mainnet-launcher-ci/badge.svg)](https://github.com/ConsenSys/quorum-mainnet-launcher/actions)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/ConsenSys/quorum-mainnet-launcher/blob/master/LICENSE)
[![sonar-quality-gate][sonar-quality-gate]][sonar-url] [![sonar-coverage][sonar-coverage]][sonar-url] [![sonar-bugs][sonar-bugs]][sonar-url] [![sonar-vulnerabilities][sonar-vulnerabilities]][sonar-url]
# Quorum mainnet client configuration generator

## Description

This library makes it easy to create a config file for an ethereum client at startup. Through different questions the configuration file will be created.

## Installation

### Install Prerequisites

* Java 11

### Build Instructions

```shell script
./gradlew assemble
```

### Import dependency

```groovy
repositories {
    maven { url "https://consensys.bintray.com/pegasys-repo" }
}
```
```groovy
  implementation 'net.consensys.services:quorum-mainnet-launcher:X.X.X'
```

### How to use 

#### 1) Create script

There is a sample here : https://github.com/ConsenSys/quorum-mainnet-launcher/blob/main/src/test/resources/net/consensys/quorum/mainnet/launcher/launcher.json

There are severals kinds of `prompt` :

- List 

```
Which Ethereum network would you like to use ?
  ❯ mainnet
    rinkeby
    ropsten
    goerli
```

**NB : You can populate your list directly from a class**

```json
"available-options": "net.consensys.quorum.mainnet.launcher.network.NetworkName"
```


- Confirm

```
Do you want to enable pruning? (y/N) yes
```

- Input

```
What is the data directory ? (/besu)
```

**NB : it will automatically propose, for all prompts, the default value set thanks to Picocli**

```java
@CommandLine.Option(
      names = {"--data-path"},
      description = "The path to data directory (default: ${DEFAULT-VALUE})")
private final String dataPath = "/besu";
```


- Checkbox

```
Select the list of APIs to enable on JSON-RPC HTTP service
❯ ◉ eth
  ◯ debug
  ◯ miner
  ◉ net
  ◯ perm
  ◉ web3
  ◯ admin
```

### 2) Configure your launcher

```java
 final ImmutableLauncherConfig launcherConfig = ImmutableLauncherConfig.builder()
 .launcherScript(BesuCommand.class.getResourceAsStream("launcher.json"))
 .addCommandClasses(firstPicocliClass, anotherPicocliClass)
 .build();
```

### 3) Run your launcher

```java
final File configFileCreated = new LauncherManager(launcherConfig).run();
```

Then the config file is created

```toml
pruning-enabled=true
data-path="/besu"
rpc-http-enabled=true
rpc-http-apis=["ETH", "NET", "WEB3"]
network="MAINNET"
Xadd-enabled=true
```

Once the config file is created you can continue starting the client without restarting

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