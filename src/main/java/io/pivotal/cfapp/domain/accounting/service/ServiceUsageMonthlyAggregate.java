
package io.pivotal.cfapp.domain.accounting.service;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"service_name", "service_guid", "usages", "plans"})
public class ServiceUsageMonthlyAggregate {

    @JsonProperty("service_name")
    public String serviceName;

    @JsonProperty("service_guid")
    public String serviceGuid;

    @JsonProperty("usages")
    public List<ServiceUsageMonthly> usages;

    @JsonProperty("plans")
    public List<ServicePlanUsageMonthly> plans;

    @JsonIgnore
    public boolean combine(ServiceUsageMonthlyAggregate usage) {
        boolean combined = false;
        if (usage.getServiceName().equals(serviceName)) {
            // TODO iterate and combine monthly service usage and service/plan usage
            String newGuid = String.join(",", this.serviceGuid, usage.getServiceGuid());
            this.serviceGuid = newGuid;
            combined = true;
        }
        return combined;
    }

}
