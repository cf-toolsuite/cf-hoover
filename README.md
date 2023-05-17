# VMware Tanzu Application Service > Hoover

[![Beta](https://img.shields.io/badge/Release-GA-darkgreen)](https://img.shields.io/badge/Release-GA-darkgreen) ![Github Action CI Workflow Status](https://github.com/pacphi/cf-hoover/actions/workflows/ci.yml/badge.svg) [![Known Vulnerabilities](https://snyk.io/test/github/pacphi/cf-hoover/badge.svg?style=plastic)](https://snyk.io/test/github/pacphi/cf-hoover) [![Release](https://jitpack.io/v/pacphi/cf-hoover.svg)](https://jitpack.io/#pacphi/cf-hoover/master-SNAPSHOT) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

> Status: Beta

You're already aware of and are using [cf-butler](https://github.com/pacphi/cf-butler) to help report on and manage application and service instances.  Wouldn't it be nice to easily aggregate reporting across multiple foundations? This is `cf-hoover`'s raison d'être.

# Table of Contents

  * [Prerequisites](#prerequisites)
  * [Tools](#tools)
  * [Clone](#clone)
  * [How to configure](#how-to-configure)
      * [Managing external configuration](#managing-external-configuration)
      * [Minimum required keys](#minimum-required-keys)
      * [General configuration notes](#general-configuration-notes)
  * [How to Build](#how-to-build)
  * [How to Run with Gradle](#how-to-run-with-gradle)
  * [How to deploy to VMware Tanzu Application Service](#how-to-deploy-to-vmware-tanzu-application-service)
      * [Using scripts](#using-scripts)
  * [Endpoints](#endpoints)
      * [Snapshot](#snapshot)
      * [Accounting](#accounting)

## Prerequisites

Required

* [VMware Tanzu Application Service](https://pivotal.io/platform/pivotal-application-service) 2.11 or better
* [Spring Cloud Services, Config Server](https://docs.pivotal.io/spring-cloud-services/3-1/common/config-server/configuring-with-git.html) 3.1.x or better

Optional

* [Spring Cloud Services, Service Registry](https://docs.pivotal.io/spring-cloud-services/3-1/common/service-registry/index.html) 3.1.x or better


## Tools

* [git](https://git-scm.com/downloads) 2.40.0 or better
* [JDK](http://openjdk.java.net/install/) 17 or better
* [cf](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html) CLI 8.6.1 or better


## Clone

```
git clone https://github.com/pacphi/cf-hoover.git
```


## How to configure

Make a copy of then edit the contents of the `application.yml` file located in `src/main/resources`.  A best practice is to append a suffix representing the target deployment environment (e.g., `application-pws.yml`, `application-pcfone.yml`). You will need to provide administrator credentials to Apps Manager for the foundation if you want the butler to keep your entire foundation tidy.

> You really should not bundle configuration with the application. To take some of the sting away, you might consider externalizing and/or [encrypting](https://blog.novatec-gmbh.de/encrypted-properties-spring/) this configuration.

### Managing external configuration

Create a [Git](https://git-scm.com/docs/gittutorial) repository or work with a [Vault](https://www.baeldung.com/vault) instance as the home your configuration.  Cf-hoover has a dependency on the [Spring Cloud Config](https://cloud.spring.io/spring-cloud-static/spring-cloud-config/2.1.0.RELEASE/single/spring-cloud-config.html#_locating_remote_configuration_resources) client, but is disabled by default.

The `cloud` [profile](https://spring.io/understanding/profiles) enables the client, so when you [cf push](https://docs.run.pivotal.io/devguide/deploy-apps/deploy-app.html#push) cf-hoover, [bind](https://cli.cloudfoundry.org/en-US/cf/bind-service.html) it to a [properly configured](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/configuring-with-git.html#general-configuration) Config Server [service instance](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/managing-service-instances.html), and start the app instance, it will consult the [Git](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/configuration-properties.html#git-global-configuration) repo or [Vault](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/configuration-properties.html#vault-global-configuration) instance for configuration to target and aggregate results from one or more previously deployed [cf-butler](https://github.com/pacphi/cf-butler) instances.

A sample repository exists for your perusal [here](https://github.com/pacphi/cf-hoover-config).

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
./gradlew bootRun -Dspring.profiles.active=pws
```

> Consult the [samples](samples) directory for examples.


## How to Build

```
./gradlew build
```


## How to Run with Gradle

```
./gradlew bootRun -Dspring.profiles.active={target_foundation_profile}
```
where `{target_foundation_profile}` is something like `pws` or `pcfone`

> You'll need to manually stop to the application with `Ctrl+C`

Alternatively, if you intend to setup [cf-hoover-ui](https://github.com/pacphi/cf-hoover-ui), for a local development environment deployment then, you must first:

    * Launch a standalone instance of [Eureka server](https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-eureka-server.html)

Set an additional property before launching `cf-hoover`

```
-Dspring.cloud.discovery.enabled=true
```

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
foundation,organization,space,application id,application name,buildpack,image,stack,running instances,total instances,urls,last pushed,last event,last event actor,last event time,requested state
"mvp","credhub-service-broker-org","credhub-service-broker-space","1a908282-d83b-47c4-8674-f60c398d403e","credhub-broker-1.2.0","binary",,"cflinuxfs2","1","1","credhub-broker.cfapps.cf.cirrus.labs.mvptime.org","2019-03-04T00:00","audit.app.droplet.create","system_services","2019-03-04T00:00","started"
"mvp","mvptime","default","5f7349ba-6431-4f4e-a1fa-5b8bbaa3fdef","bootiful-greeting","java",,"cflinuxfs2","2","2","bootiful-greeting-responsible-sable.cfapps.cf.zoo.labs.foo.org","2018-06-27T00:00",,,,"started"
"mvp","mvptime","default","0af297a2-e886-42c4-a0bc-5a4cdffb327c","bootiful-hello","java",,"cflinuxfs2","3","3","bootiful-hello-brash-camel.cfapps.cf.zoo.labs.foo.org","2018-07-12T00:00","app.crash","bootiful-hello","2019-03-04T00:00","started"
"mvp","mvptime","default","44694d3f-a278-4745-ac5d-aa693cb61b7b","bootiful-volume","java",,"cflinuxfs2","1","1","bootiful-volume.cfapps.cf.zoo.labs.foo.org","2018-05-28T00:00","app.crash","bootiful-volume","2019-03-04T00:00","started"
"mvp","mvptime","default","3458d94d-f629-4f86-84a3-2b6e16409269","reactive-cassy","java",,"cflinuxfs2","1","1","reactive-cassy-anxious-klipspringer.cfapps.cf.zoo.labs.foo.org","2018-11-20T00:00",,,,"started"
"mvp","planespotter","default","979da8bb-7a1b-434b-9aa3-fae5362ef15f","bootiful-hello","java",,"cflinuxfs2","1","1","bootiful-hello-chipper-buffalo.cfapps.cf.zoo.labs.foo.org","2018-10-17T00:00","audit.app.ssh-authorized","vmanoharan@pivotal.io","2019-03-20T00:00","started"
"mvp","planespotter","default","a961e75f-ad6f-4eeb-9f80-90eefe2041fd","planespotter-alpha","java",,"cflinuxfs2","1","1","planespotter-alpha.cfapps.cf.zoo.labs.foo.org,planespotter.cfapps.cf.zoo.labs.foo.org","2018-10-11T00:00","audit.app.update","vmanoharan@pivotal.io","2019-03-21T00:00","started"
```

```
GET /snapshot/detail/si
```
> Provides lists of all service instances in comma-separated value format

Sample output

```
foundation,organization,space,service id,name,service,description,plan,type,bound applications,last operation,last updated,dashboard url,requested state
"mvp","mvptime","default",,"reactive-cassy-secrets","credhub","Stores configuration parameters securely in CredHub","default","managed_service_instance","reactive-cassy","create","2018-11-20T00:00",,"succeeded"
"mvp","planespotter","default",,"planespotter-vault","credhub","Stores configuration parameters securely in CredHub","default","managed_service_instance","planespotter-alpha","update","2019-03-21T00:00",,"succeeded"
```

```
GET /snapshot/demographics
```
> Yields organization, space, user account, and service account totals across all registered foundations

Sample output

```
{
  demographics: [
    {
      foundation: "mvp",
      total-organizations: 4,
      total-spaces: 11,
      total-user-accounts: 3,
      total-service-accounts: 3
    }
  ],
  total-foundations: 1,
  total-user-accounts: 3,
  total-service-accounts: 3
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
