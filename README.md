# VMware Tanzu Application Service > Hoover

[![GA](https://img.shields.io/badge/Release-GA-darkgreen)](https://img.shields.io/badge/Release-GA-darkgreen) ![Github Action CI Workflow Status](https://github.com/cf-toolsuite/cf-hoover/actions/workflows/ci.yml/badge.svg) [![Known Vulnerabilities](https://snyk.io/test/github/cf-toolsuite/cf-hoover/badge.svg?style=plastic)](https://snyk.io/test/github/cf-toolsuite/cf-hoover) [![Release](https://jitpack.io/v/cf-toolsuite/cf-hoover.svg)](https://jitpack.io/#cf-toolsuite/cf-hoover/master-SNAPSHOT) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

You're already aware of and are using [cf-butler](https://github.com/cf-toolsuite/cf-butler) to help report on and manage application and service instances.  Wouldn't it be nice to easily aggregate reporting across multiple foundations? This is `cf-hoover`'s raison d'être.

# Table of Contents

  * [Prerequisites](#prerequisites)
  * [Tools](#tools)
  * [Clone](#clone)
  * [How to configure](#how-to-configure)
      * [Managing external configuration](#managing-external-configuration)
      * [Minimum required keys](#minimum-required-keys)
      * [General configuration notes](#general-configuration-notes)
  * [How to Build](#how-to-build)
  * [How to Run with Maven](#how-to-run-with-maven)
  * [How to check code quality with Sonarqube](#how-to-check-code-quality-with-sonarqube)
  * [How to deploy to VMware Tanzu Application Service](#how-to-deploy-to-vmware-tanzu-application-service)
      * [Using scripts](#using-scripts)
  * [Endpoints](#endpoints)
      * [Snapshot](#snapshot)
      * [Accounting](#accounting)

## Prerequisites

Required

* [VMware Tanzu Application Service](https://pivotal.io/platform/pivotal-application-service) 4.0.19+LTS-T or better
* [Spring Cloud Services, Config Server](https://docs.pivotal.io/spring-cloud-services/3-1/common/config-server/configuring-with-git.html) 3.1.x or better

Optional

* [Spring Cloud Services, Service Registry](https://docs.pivotal.io/spring-cloud-services/3-1/common/service-registry/index.html) 3.1.x or better


## Tools

* [git](https://git-scm.com/downloads) 2.40.0 or better
* [JDK](http://openjdk.java.net/install/) 21 or better
* [cf](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html) CLI 8.6.1 or better


## Clone

```
git clone https://github.com/cf-toolsuite/cf-hoover.git
```


## How to configure

Make a copy of then edit the contents of the `application.yml` file located in `src/main/resources`.  A best practice is to append a suffix representing the target deployment environment (e.g., `application-pws.yml`, `application-pcfone.yml`). You will need to provide administrator credentials to Apps Manager for the foundation if you want the butler to keep your entire foundation tidy.

> You really should not bundle configuration with the application. To take some of the sting away, you might consider externalizing and/or [encrypting](https://blog.novatec-gmbh.de/encrypted-properties-spring/) this configuration.

### Managing external configuration

Create a [Git](https://git-scm.com/docs/gittutorial) repository or work with a [Vault](https://www.baeldung.com/vault) instance as the home your configuration.  Cf-hoover has a dependency on the [Spring Cloud Config](https://cloud.spring.io/spring-cloud-static/spring-cloud-config/2.1.0.RELEASE/single/spring-cloud-config.html#_locating_remote_configuration_resources) client, but is disabled by default.

The `cloud` [profile](https://spring.io/understanding/profiles) enables the client, so when you [cf push](https://docs.run.pivotal.io/devguide/deploy-apps/deploy-app.html#push) cf-hoover, [bind](https://cli.cloudfoundry.org/en-US/cf/bind-service.html) it to a [properly configured](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/configuring-with-git.html#general-configuration) Config Server [service instance](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/managing-service-instances.html), and start the app instance, it will consult the [Git](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/configuration-properties.html#git-global-configuration) repo or [Vault](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/configuration-properties.html#vault-global-configuration) instance for configuration to target and aggregate results from one or more previously deployed [cf-butler](https://github.com/cf-toolsuite/cf-butler) instances.

A sample repository exists for your perusal [here](https://github.com/cf-toolsuite/cf-hoover-config).

### Minimum required keys

At a minimum you should supply values for the following keys

* `cf.butlers` -  a map of cf-butler routes

    For example

    properties
    ```
    cf.butlers.pws=cf-butler-grateful-mouse.cfapps.io
    cf.butlers.pcfone=cf-butler-active-tasmaniandevil.apps.pcfone.io
    ```

    yaml
    ```
    cf:
      butlers:
        pws: cf-butler-grateful-mouse.cfapps.io
        pcfone: cf-butler-active-tasmaniandevil.apps.pcfone.io
    ```
    > Each key is an alias for a foundation and each value is the route to an application instance of cf-butler deployed on that foundation

### General configuration notes

If you copied and appended a suffix to the original `application.yml` then you would set `spring.profiles.active` to be that suffix

E.g., if you had a configuration file named `application-pws.yml`

```
./mvnw spring-boot:run -Dspring.profiles.active=pws
```

> Consult the [samples](samples) directory for examples.


## How to Build

```
./mvnw clean package
```
### Alternatives

The below represent a collection of Maven profiles available in the Maven POM.

* Log4J2 logging (log4j2)
  * swaps out [Logback](http://logback.qos.ch/documentation.html) logging provider for [Log4J2](https://logging.apache.org/log4j/2.x/manual/async.html) and [Disruptor](https://lmax-exchange.github.io/disruptor/user-guide/index.html#_introduction)
* Native image (native)
  * uses [Spring AOT](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#spring-aot-maven) to compile a native executable with [GraalVM](https://www.graalvm.org/docs/introduction/)


```
./mvnw clean package -Plog4j2
```
> Swap out default "lossy" logging provider


```
# Using Cloud Native Buildpacks image
./mvnw spring-boot:build-image -Pnative

# Using pre-installed Graal CE
./mvnw native:compile -Pnative -DskipTests
```


## How to Run with Maven

```
./mvnw spring-boot:run -Dspring.profiles.active={target_foundation_profile}
```
where `{target_foundation_profile}` is something like `pws` or `pcfone`

> You'll need to manually stop to the application with `Ctrl+C`

Alternatively, if you intend to setup [cf-hoover-ui](https://github.com/cf-toolsuite/cf-hoover-ui), for a local development environment deployment then, you must first:

* Launch a standalone instance of [Eureka server](https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-eureka-server.html)

Set an additional property before launching `cf-hoover`

```
-Dspring.cloud.discovery.enabled=true
```


## How to check code quality with Sonarqube

Launch an instance of Sonarqube on your workstation with Docker

```
docker run -d --name sonarqube -p 9000:9000 -p 9092:9092 sonarqube
```

Then make sure to add goal and required arguments when building with Maven. For example:

```
mvn sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

Then visit `http://localhost:9000` in your favorite browser to inspect results of scan.


## How to deploy to VMware Tanzu Application Service

Please review the [manifest.yml](manifest.yml) before deploying.

### Using scripts

Deploy the app (bound to an instance of Spring Cloud Config Server)

Create a file named `config-server.json` located in a `config` sub-directory off the root of this project. Look at the sample [here](samples/config-server.json) to get an idea of the contents.  Consult the Spring Cloud Services Config Server [documentation](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/index.html) for more advanced configuration options like [SSH repository access](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/configuring-with-git.html#ssh-repository-access).

Then execute

```
./deploy.sh
```

Shutdown and destroy the app and service instances with

```
./destroy.sh
```

> Note: If you are seeing [OutOfMemory exceptions](https://dzone.com/articles/troubleshooting-problems-with-native-off-heap-memo) shortly after startup you may need to [cf scale](https://docs.pivotal.io/application-service/2-10/devguide/deploy-apps/cf-scale.html#verticall) the available memory when working with multiple foundations.

Alternative scripts exist for deploying `cf-hoover` when working with `cf-hoover-ui`.  See [deploy.with-registry.sh](deploy.with-registry.sh) and [destroy.with-registry.sh](destroy.with-registry.sh).

## Endpoints

These REST endpoints have been exposed for administrative purposes.

### Snapshot

```
GET /snapshot/organizations
```
> Assembles list of organizations per foundation registered

```
GET /snapshot/spaces
```
> Assembles list of spaces per foundation registered


```
GET /snapshot/spaces/users
```
> Provides details and light metrics for users by role within all organizations and spaces across all registered foundations

Sample output
```
[
  {
    foundation: "pws"
    organization: "Northwest",
    space: "akarode",
    auditors: [ ],
    developers: [
      "wlund@pivotal.io",
      "akarode@pivotal.io"
    ],
    managers: [
      "wlund@pivotal.io",
      "akarode@pivotal.io"
    ],
    users: [
      "wlund@pivotal.io",
      "akarode@pivotal.io"
    ],
    user-count: 2,
  },
  {
    foundation: "pws"
    organization: "Northwest",
    space: "arao",
    auditors: [ ],
    developers: [
      "arao@pivotal.io"
    ],
    managers: [
      "arao@pivotal.io"
    ],
    users: [
      "arao@pivotal.io"
    ],
    user-count: 1
  },
...
```
> `users` is the unique subset of all users from each role in the organization/space

```
GET /snapshot/{foundation}/{organization}/{space}/users
```
> Provides details and light metrics for users by role within a targeted foundation (alias), organization and space

```
GET /snapshot/users
```
> Lists all unique user accounts across all registered foundations

```
GET /snapshot/users/count
```
> Counts the number of user accounts across all registered foundations

```
GET /snapshot/summary
```
> Provides summary metrics for applications and service instances across all registered foundations

> **Note**: this summary report does not take the place of an official foundation Accounting Report. The Accounting Report is focussed on calculating aggregates (on a monthly basis) such as: (a) the total hours of application instance usage, (b) the largest # of application instances running (a.k.a. maximum concurrent application instances), c) the total hours of service instance usage and (d) the largest # of service instances running (a.k.a. maximum concurrent service instances).

Sample output
```
{
  "application-counts": {
    "by-buildpack": {
      "java": 28,
      "nodejs": 2,
      "unknown": 5
    },
    "by-stack": {
      "cflinuxfs2": 20,
      "cflinuxfs3": 15
    },
    "by-dockerimage": {
      "--": 0
    },
    "by-status": {
      "stopped": 15,
      "started": 20
    },
    "total-applications": 35,
    "total-running-application-instances": 21,
    "total-stopped-application-instances": 18,
    "total-crashed-application-instances": 3,
    "total-application-instances": 42,
    "velocity": {
      "between-two-days-and-one-week": 6,
      "between-one-week-and-two-weeks": 0,
      "between-one-day-and-two-days": 3,
      "between-one-month-and-three-months": 5,
      "between-three-months-and-six-months": 4,
      "between-two-weeks-and-one-month": 1,
      "in-last-day": 0,
      "between-six-months-and-one-year": 10,
      "beyond-one-year": 6
    }
  },
  "service-instance-counts": {
    "by-organization": {
    "Northwest": 37
    },
    "by-service": {
      "rediscloud": 2,
      "elephantsql": 4,
      "mlab": 2,
      "p-service-registry": 2,
      "cleardb": 10,
      "p-config-server": 2,
      "user-provided": 9,
      "app-autoscaler": 2,
      "cloudamqp": 4
    },
    "by-service-and-plan": {
      "cleardb/spark": 10,
      "mlab/sandbox": 2,
      "rediscloud/30mb": 2,
      "p-service-registry/trial": 2,
      "elephantsql/turtle": 4,
      "p-config-server/trial": 2,
      "cloudamqp/lemur": 4,
      "app-autoscaler/standard": 2
    },
    "total-service-instances": 37,
    "velocity": {
      "between-two-days-and-one-week": 4,
      "between-one-week-and-two-weeks": 1,
      "between-one-day-and-two-days": 2,
      "between-one-month-and-three-months": 3,
      "between-three-months-and-six-months": 0,
      "between-two-weeks-and-one-month": 1,
      "in-last-day": 0,
      "between-six-months-and-one-year": 5,
      "beyond-one-year": 8
    }
  }
}
```

```
GET /snapshot/detail
```
> Provides lists of all applications and service instances (by foundation, organization and space) and accounts (split into sets of user and service names)

> **Note**: this detail report does not take the place of an official foundation Accounting Report. However, it does provide a much more detailed snapshot of all the applications that were currently running at the time of collection.

```
GET /snapshot/detail/ai
```
> Provides lists of all applications in comma-separated value format

Sample output

```
foundation,organization,space,application id,application name,buildpack,buildpack version,image,stack,running instances,total instances,memory used (in gb),memory quota (in gb),disk used (in gb),disk quota (in gb),urls,last pushed,last event,last event actor,last event time,requested state
"npike-foundation","arul","dev","ff1f2147-079c-4f58-bbe7-ad0fb905a2e8","pcfdemo",,,,"cflinuxfs3","0","1","0.0","0.0","pcfdemo.apps.sangabriel.cf-app.com",,"audit.app.update","bcbc230c-2ecc-4dc9-91de-6b49776ad403","2023-05-12T16:10:12","stopped"
"npike-foundation","arul","dev","655aab3a-8b77-42ce-a87b-aa5848cc9d7d","rabbitmq-example-app",,,,"cflinuxfs3","0","2","0.0","0.0","rabbitmq-example-app.apps.sangabriel.cf-app.com","2023-05-12T16:10:14","audit.app.build.create","bcbc230c-2ecc-4dc9-91de-6b49776ad403","2023-05-12T16:10:27","stopped"
"npike-foundation","arul","dev","b814712d-03e9-44b2-ac28-1946cbdbc82c","spring-music","java","v4.54",,"cflinuxfs3","1","1","0.21608664747327566","0.16757965087890625","spring-music-noisy-kookaburra-eg.apps.sangabriel.cf-app.com","2023-05-12T16:10:14","audit.app.restart","bcbc230c-2ecc-4dc9-91de-6b49776ad403","2023-05-12T16:11:28","started"
"npike-foundation","arul","prod","3a414a89-388c-4625-9fbf-0cd2b889345a","rabbitmq",,,,"cflinuxfs3","0","2","0.0","0.0","rabbitmq.apps.sangabriel.cf-app.com","2023-05-12T16:10:14","audit.app.build.create","01ecf2c7-f4dd-4ca5-8dfd-30df64f09918","2023-05-12T16:10:25","stopped"
"npike-foundation","credhub-service-broker-org","credhub-service-broker-space","b95adbde-2597-4150-b048-8be45796cab6","credhub-broker-1.5.1","binary","1.1.3",,"cflinuxfs3","1","1","0.016004773788154125","0.009128570556640625","credhub-broker.apps.sangabriel.cf-app.com","2023-05-12T15:31:52","audit.app.droplet.create","82dcc4bb-ef83-4db8-b05a-a0e2b88e67e3","2023-05-12T15:32:21","started"
"npike-foundation","dev","observability","be785d20-fd7e-4674-bb7f-8b742b40e1d4","cf-butler","java","v4.54",,"cflinuxfs3","1","1","0.1790512539446354","0.211700439453125","cf-butler.apps.sangabriel.cf-app.com","2023-06-07T00:54:18","audit.app.restart","bcbc230c-2ecc-4dc9-91de-6b49776ad403","2023-06-07T00:55:07","started"
"npike-foundation","dev","observability","2858eaba-5c18-4916-849b-7676c6bb33c5","cf-hoover","java","v4.54",,"cflinuxfs3","1","1","0.40730543807148933","0.2159271240234375","cf-hoover-forgiving-wombat-sr.apps.sangabriel.cf-app.com","2023-04-14T12:54:07",,,,"started"
"npike-foundation","dev","observability","f1e7eb6c-7d54-4968-b6e4-3f9f8fade059","cf-hoover-ui","java","v4.54",,"cflinuxfs3","1","1","0.5119553785771132","0.2413177490234375","cf-hoover-ui-chatty-chimpanzee-jf.apps.sangabriel.cf-app.com","2023-04-14T14:30:03",,,,"started"
"npike-foundation","dev","sample-apps","89b4ae55-705a-4572-9890-46d5171a613c","nicky-butler","java","v4.54",,"cflinuxfs3","0","1","0.0","0.0","nicky-butler.apps.sangabriel.cf-app.com","2023-04-14T18:44:03","audit.app.stop","bcbc230c-2ecc-4dc9-91de-6b49776ad403","2023-05-16T02:45:57","stopped"
"npike-foundation","dev","tap","1fdc8b02-99a7-4741-8c9d-7df4363a7f4d","tas-java-web-app",,,"dev.registry.pivotal.io/warroyo/supply-chain/tas-java-web-app-dev-tap@sha256:0943939b3ca1bcb6527ad39bf766d84b9be4455a14a0b67ab7c4b6f4840750e1","cflinuxfs3","2","2","0.352079289034009","0.5242393091320992","tas-java-web-app.apps.sangabriel.cf-app.com","2023-05-10T18:36:44","audit.app.update","9602fa7e-d66f-4a0e-b78c-b191106b79c4","2023-05-10T18:40:46","started"
"npike-foundation","p-spring-cloud-services","249f77f1-63a9-41c2-a26a-ad848df3fcba","3e4c55ca-ed5f-4e97-86c2-25d38626b90d","config-server","java","v4.54",,"cflinuxfs3","1","1","0.24609375","0.15219497680664062","config-server-249f77f1-63a9-41c2-a26a-ad848df3fcba.apps.sangabriel.cf-app.com","2023-04-14T12:54:45",,,,"started"
"npike-foundation","p-spring-cloud-services","63df5b0a-b4a8-4be1-8aad-c54dab4cb7ed","893f0f47-6eed-4bd0-bb83-5472b7ea4c07","service-registry","java","v4.54",,"cflinuxfs3","1","1","0.2760823564603925","0.17532730102539062","service-registry-63df5b0a-b4a8-4be1-8aad-c54dab4cb7ed.apps.sangabriel.cf-app.com","2023-04-14T12:54:47",,,,"started"
"npike-foundation","system","autoscaling","d7396dab-0a12-4e68-a78f-c6008d5051a7","autoscale","binary","1.1.3",,"cflinuxfs3","3","3","0.0546162910759449","0.052013397216796875","autoscale.sys.sangabriel.cf-app.com","2023-04-13T15:06:22",,,,"started"
"npike-foundation","system","autoscaling","c5aa1354-6dd4-41f5-aa7b-939a19029532","autoscale-api","java","v4.54",,"cflinuxfs3","1","1","0.2501183710992336","0.188018798828125","autoscale.sys.sangabriel.cf-app.com/api/v2","2023-04-13T15:06:51",,,,"started"
"npike-foundation","system","notifications-with-ui","806444bb-3da7-468e-9da6-d5bde5b56fd7","notifications-ui","binary","1.1.3",,"cflinuxfs3","2","2","0.021087645553052425","0.02587890625","notifications-ui.sys.sangabriel.cf-app.com","2023-04-13T15:05:45",,,,"started"
"npike-foundation","system","offline-docs","3b30b7d8-d170-41c7-b4e3-20a0e4eb369a","offline-docs","ruby","1.9.2",,"cflinuxfs3","1","1","0.09670342318713665","0.21299362182617188","offline-docs.apps.sangabriel.cf-app.com","2023-04-13T15:01:02",,,,"started"
"npike-foundation","system","p-dataflow","ed513df1-a946-45b8-8975-2db788f0bbec","p-dataflow-1.13.0","java","v4.54",,"cflinuxfs3","1","1","0.6273137014359236","0.44135284423828125","p-dataflow.apps.sangabriel.cf-app.com","2023-04-27T18:13:46",,,,"started"
"npike-foundation","system","system","ecd6f8da-38ef-4714-9d84-cee2cb5f13d4","app-usage-scheduler","ruby","1.9.2",,"cflinuxfs3","1","1","0.0999792842194438","0.1661376953125",,"2023-04-13T14:56:21",,,,"started"
"npike-foundation","system","system","9bd4378d-c97f-46cd-bcf9-e99fcd56297d","app-usage-server","ruby","1.9.2",,"cflinuxfs3","2","2","0.5762754492461681","0.33228302001953125","app-usage.sys.sangabriel.cf-app.com","2023-04-13T14:56:21",,,,"started"
"npike-foundation","system","system","fefc5be6-90ee-4b87-8815-1c13af429dd9","app-usage-worker","ruby","1.9.2",,"cflinuxfs3","1","1","0.11408127937465906","0.1661376953125",,"2023-04-13T14:56:20",,,,"started"
"npike-foundation","system","system","31c7e146-0949-40a4-b57f-d11d403916e1","apps-manager-js-green","staticfile","1.6.0",,"cflinuxfs3","6","6","0.10966186318546534","0.78497314453125","apps.sys.sangabriel.cf-app.com","2023-04-13T15:02:38",,,,"started"
"npike-foundation","system","system","b610eb6f-9c9f-4906-872d-d12021232755","p-invitations-green","nodejs","1.8.6",,"cflinuxfs3","2","2","0.12076483760029078","0.36865997314453125","p-invitations.sys.sangabriel.cf-app.com","2023-04-13T15:02:32",,,,"started"
"npike-foundation","system","system","586a6e4e-b6df-4fe5-88a3-eb9b7c667144","search-server-green","nodejs","1.8.6",,"cflinuxfs3","2","2","0.1306796595454216","0.3582916259765625","search-server.sys.sangabriel.cf-app.com","2023-04-13T15:02:29",,,,"started"
"npike-foundation","zoo-labs","demo","35eb5fdf-46a3-4d77-bfe6-dd29878a1553","primes","java","v4.54",,"cflinuxfs4","2","2","0.3480446543544531","0.35300445556640625","primes-bogus-hedgehog-kx.apps.sangabriel.cf-app.com","2023-05-25T17:59:41","audit.app.environment.show","bcbc230c-2ecc-4dc9-91de-6b49776ad403","2023-06-06T20:15:19","started"
```

```
GET /snapshot/detail/si
```
> Provides lists of all service instances in comma-separated value format

Sample output

```
foundation,organization,space,service instance id,name,service,description,plan,type,bound applications,last operation,last updated,dashboard url,requested state
"npike-foundation","dev","observability","efe26b17-0722-4b9d-8160-2b804adf4bbc","cf-butler-secrets",,,,"user_provided_service_instance","cf-butler",,,,
"npike-foundation","dev","observability","249f77f1-63a9-41c2-a26a-ad848df3fcba","cf-hoover-config","p.config-server","Service to provide configuration to applications at runtime.","standard","managed_service_instance","cf-hoover","create","2023-04-14T12:56:27","https://config-server-249f77f1-63a9-41c2-a26a-ad848df3fcba.apps.sangabriel.cf-app.com/dashboard","succeeded"
"npike-foundation","dev","observability","731a52c3-f189-47cb-b936-0442ad63da26","cf-butler-backend","p.mysql","Dedicated instances of MySQL","db-small-80","managed_service_instance","cf-butler","create","2023-04-13T20:53:47",,"succeeded"
"npike-foundation","dev","observability","63df5b0a-b4a8-4be1-8aad-c54dab4cb7ed","hooverRegistry","p.service-registry","Deploys Eureka server as a service registry for application clients.","standard","managed_service_instance","cf-hoover,cf-hoover-ui","create","2023-04-14T12:56:33","https://service-registry-63df5b0a-b4a8-4be1-8aad-c54dab4cb7ed.apps.sangabriel.cf-app.com/dashboard","succeeded"
"npike-foundation","dev","sample-apps","69ee91b3-7493-45bd-b0bb-d9dc3e04a0bd","cf-butler-backend","p.mysql","Dedicated instances of MySQL","db-small-80","managed_service_instance","nicky-butler","create","2023-04-14T15:20:18",,"succeeded"
"npike-foundation","p-spring-cloud-services","249f77f1-63a9-41c2-a26a-ad848df3fcba","5f18839e-8425-4e58-925b-4f4cab17a378","mirror-svc","p.mirror-service","Spring Cloud Config Server git mirror service. This is an internal system service and should not be created directly by end users.","standard","managed_service_instance","config-server","create","2023-04-14T12:54:31",,"succeeded"
"npike-foundation","system","system","c004a59c-c604-430a-b19a-345f1f3653c2","structured-format-json",,,,"user_provided_service_instance","app-usage-worker",,,,
```

```
GET /snapshot/demographics
```
> Yields organization, space, user account, and service account totals across all registered foundations

Sample output

```
{
    "demographics": [
        {
            "foundation": "npike-foundation",
            "total-organizations": 7,
            "total-service-accounts": 5,
            "total-spaces": 18,
            "total-user-accounts": 0
        }
    ],
    "total-foundations": 1,
    "total-service-accounts": 5,
    "total-user-accounts": 0
}
```

```
GET /collect
```
> Returns time keeping records (i.e., the date/time of collection of snapshot data from each cf-butler instance registered with cf-hoover)

Sample output

```
{
    "time-keepers": [
        {
            "collectionDateTime": "2023-06-20T03:13:29",
            "foundation": "npike-foundation"
        }
    ]
}
```


### Accounting

```
GET /accounting/applications
```
> Produces an aggregate system-wide account report of [application usage](https://docs.pivotal.io/pivotalcf/2-4/opsguide/accounting-report.html#app-usage)

> **Note**: Report excludes application instances in the `system` org

```
GET /accounting/services
```
> Produces an aggregate system-wide account report of [service usage](https://docs.pivotal.io/pivotalcf/2-4/opsguide/accounting-report.html#service-usage)

> **Note**: Report excludes user-provided service instances

```
GET /accounting/tasks
```
> Produces an aggregate system-wide account report of [task usage](https://docs.pivotal.io/pivotalcf/2-4/opsguide/accounting-report.html#task-usage)
