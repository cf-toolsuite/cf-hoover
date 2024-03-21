# VMware Tanzu Application Service > Hoover

## How to configure

Make a copy of then edit the contents of the `application.yml` file located in `src/main/resources`.  A best practice is to append a suffix representing the target deployment environment (e.g., `application-pws.yml`, `application-pcfone.yml`). You will need to provide administrator credentials to Apps Manager for the foundation if you want the butler to keep your entire foundation tidy.

> You really should not bundle configuration with the application. To take some of the sting away, you might consider externalizing and/or [encrypting](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/#_encryption_and_decryption) this configuration.

### Managing external configuration

Create a [Git](https://git-scm.com/docs/gittutorial) repository or work with a [Vault](https://www.baeldung.com/vault) instance as the home your configuration.  Cf-hoover has a dependency on the [Spring Cloud Config](https://cloud.spring.io/spring-cloud-static/spring-cloud-config/2.1.0.RELEASE/single/spring-cloud-config.html#_locating_remote_configuration_resources) client, but is disabled by default.

The `cloud` [profile](https://spring.io/understanding/profiles) enables the client, so when you [cf push](https://docs.run.pivotal.io/devguide/deploy-apps/deploy-app.html#push) cf-hoover, [bind](https://cli.cloudfoundry.org/en-US/cf/bind-service.html) it to a [properly configured](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/configuring-with-git.html#general-configuration) Config Server [service instance](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/managing-service-instances.html), and start the app instance, it will consult the [Git](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/configuration-properties.html#git-global-configuration) repo or [Vault](https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/configuration-properties.html#vault-global-configuration) instance for configuration to target and aggregate results from one or more previously deployed [cf-butler](https://github.com/cf-toolsuite/cf-butler) instances.

A sample repository exists for your perusal [here](https://github.com/cf-toolsuite/cf-hoover-config).

### Minimum required keys

At a minimum you should supply values for the `cf.butlers` map of butler routes via one of the following methods

#### Properties
```
cf.butlers.pws=cf-butler-grateful-mouse.cfapps.io
cf.butlers.pcfone=cf-butler-active-tasmaniandevil.apps.pcfone.io
```

#### application.yml
```yaml
cf:
  butlers:
    pws: cf-butler-grateful-mouse.cfapps.io
    pcfone: cf-butler-active-tasmaniandevil.apps.pcfone.io
```
Each key is an alias for a foundation and each value is the route to an application instance of cf-butler deployed on that foundation. If you don't include a protocol, it'll default to `https`.

### General configuration notes

If you copied and appended a suffix to the original `application.yml` then you would set `spring-boot.run.profiles` to be that suffix. For example if you had a configuration file named `application-pws.yml`:

```
./mvnw spring-boot:run -Dspring-boot.run.profiles=pws
```

Consult the [samples](../samples) directory for additional examples.
