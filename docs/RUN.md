# VMware Tanzu Application Service > Hoover

## How to Run with Maven

```
./mvnw spring-boot:run -Dspring-boot.run.profiles={target_foundation_profile}
```
where `{target_foundation_profile}` is something like `pws` or `pcfone`

> You'll need to manually stop to the application with `Ctrl+C`

Alternatively, if you intend to setup [cf-hoover-ui](https://github.com/cf-toolsuite/cf-hoover-ui), for a local development environment deployment then, you must first:

* Launch a standalone instance of [Eureka server](https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-eureka-server.html)

Set an additional property before launching `cf-hoover`

```
-Dspring.cloud.discovery.enabled=true
```
