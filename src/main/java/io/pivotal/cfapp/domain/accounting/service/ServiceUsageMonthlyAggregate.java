
package io.pivotal.cfapp.domain.accounting.service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.Builder.Default;

@Builder
@Getter
@JsonPropertyOrder({"service_name", "service_guid", "usages", "plans"})
public class ServiceUsageMonthlyAggregate {

    @JsonProperty("service_name")
    public String serviceName;

    @JsonProperty("service_guid")
    public String serviceGuid;

    @Default
    @JsonProperty("usages")
    public List<ServiceUsageMonthly> usages = new ArrayList<>();

    @Default
    @JsonProperty("plans")
    public List<ServicePlanUsageMonthly> plans = new ArrayList<>();

    @JsonCreator
    public ServiceUsageMonthlyAggregate(
        @JsonProperty("service_name") String serviceName,
        @JsonProperty("service_guid") String serviceGuid,
        @JsonProperty("usages") List<ServiceUsageMonthly> usages,
        @JsonProperty("plans") List<ServicePlanUsageMonthly> plans) {
        this.serviceName = serviceName;
        this.serviceGuid = serviceGuid;
        this.usages = usages;
        this.plans = plans;
    }

    @JsonIgnore
    public boolean combine(ServiceUsageMonthlyAggregate usage) {
        boolean combined = false;
        if (usage.getServiceName().equals(serviceName)) {
            for (ServiceUsageMonthly su: usage.getUsages()) {
                for (ServiceUsageMonthly suu: usages) {
                    if(!suu.combine(su)) {
                        usages.add(su);
                    }
                }
            }
            for (ServicePlanUsageMonthly pu: usage.getPlans()) {
                for (ServicePlanUsageMonthly spu: plans) {
                    if(!spu.combine(pu)) {
                        plans.add(pu);
                    }
                }
            }
            if (!usage.getServiceGuid().contains(this.serviceGuid)) {
                String newGuid = String.join(",", this.serviceGuid, usage.getServiceGuid());
                this.serviceGuid = newGuid;
            }
            combined = true;
        }
        return combined;
    }

}
