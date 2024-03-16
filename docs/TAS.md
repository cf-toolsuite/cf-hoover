# VMware Tanzu Application Service > Hoover

## How to deploy to VMware Tanzu Application Service

Please review the [manifest.yml](../manifest.yml) before deploying.

### Using scripts

Deploy the app (bound to an instance of Spring Cloud Config Server)

Create a file named `config-server.json` located in a `config` sub-directory off the root of this project. Look at the sample [here](../samples/config-server.json) to get an idea of the contents.  Consult the Spring Cloud Services Config Server [documentation](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/index.html) for more advanced configuration options like [SSH repository access](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/configuring-with-git.html#ssh-repository-access).

Then execute

```
./scripts/deploy.sh
```

Shutdown and destroy the app and service instances with

```
./scripts/destroy.sh
```

> Note: If you are seeing [OutOfMemory exceptions](https://dzone.com/articles/troubleshooting-problems-with-native-off-heap-memo) shortly after startup you may need to [cf scale](https://docs.pivotal.io/application-service/2-10/devguide/deploy-apps/cf-scale.html#verticall) the available memory when working with multiple foundations.

Alternative scripts exist for deploying `cf-hoover` when working with `cf-hoover-ui`.  See [deploy.with-registry.sh](../scripts/deploy.with-registry.sh) and [destroy.with-registry.sh](../scripts/destroy.with-registry.sh).