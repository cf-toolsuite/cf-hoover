# VMware Tanzu Application Service > Hoover

## Prerequisites

Required

* Access to a [foundation with Cloud Foundry](https://docs.cloudfoundry.org/deploying/cf-deployment/index.html) that was deployed with [cf-deployment](https://github.com/cloudfoundry/cf-deployment/releases) v30.10.0 or better
  * You will need to deploy the open source versions of Spring Cloud [Config Server](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/) and [Discovery Service](https://docs.spring.io/spring-cloud/docs/current/reference/htmlsingle/#spring-cloud-running-eureka-server).  Review the examples [here](https://github.com/cf-toolsuite/home/tree/main/footprints/local/support/config-server) and [here](https://github.com/cf-toolsuite/home/tree/main/footprints/local/support/discovery-service).

or

* Access to a [foundation with VMware Tanzu Application Service](https://pivotal.io/platform/pivotal-application-service) 4.0.19+LTS-T or better installed
* [Spring Cloud Services, Config Server](https://docs.vmware.com/en/Spring-Cloud-Services-for-VMware-Tanzu/3.1/spring-cloud-services/GUID-config-server-configuring-with-git.html) 3.1.x or better installed

Optional

* [Spring Cloud Services, Service Registry](https://docs.vmware.com/en/Spring-Cloud-Services-for-VMware-Tanzu/3.1/spring-cloud-services/GUID-service-registry-index.html) 3.1.x or better installed
