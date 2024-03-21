# VMware Tanzu Application Service > Hoover

## How to Run with Maven

```shell
./mvnw spring-boot:run -Dspring-boot.run.profiles={target_foundation_profile}
```
where `{target_foundation_profile}` is something like `dev`, `pws` or `pcfone`.

You'll need to manually stop to the application with `Ctrl+C`

### Running with Hoover-UI
If you intend to optionally setup [cf-hoover-ui](https://github.com/cf-toolsuite/cf-hoover-ui) for a local development, then you must first launch a standalone instance of [Eureka server](https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-eureka-server.html) for the hoover-ui to be able to find the hoover server.

Enable cloud discovery before launching `cf-hoover` via the command line or application config:

```
./mvnw spring-boot:run -Dspring-boot.run.profiles=pws -Dspring.cloud.discovery.enabled=true
```

or via application.yml
```yaml
spring:
  discovery:
    enabled: true
```
