package io.pivotal.cfapp.domain.accounting.service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
@JsonPropertyOrder({ "usages", "service_plan_name", "service_plan_guid"})
public class ServicePlanUsageMonthly {

    @Default
    @JsonProperty("usages")
    public List<ServiceUsageMonthly> usages = new ArrayList<>();

    @JsonProperty("service_plan_name")
    public String servicePlanName;

    @JsonProperty("service_plan_guid")
    public String servicePlanGuid;

    @JsonCreator
    public ServicePlanUsageMonthly(
        @JsonProperty("usages") List<ServiceUsageMonthly> usages,
        @JsonProperty("service_plan_name") String servicePlanName,
        @JsonProperty("service_plan_guid") String servicePlanGuid) {
        this.usages = usages;
        this.servicePlanName = servicePlanName;
        this.servicePlanGuid = servicePlanGuid;
    }

    @JsonIgnore
    public ServicePlanUsageMonthly combine(ServicePlanUsageMonthly usage) {
        ServicePlanUsageMonthly result = null;
        if (usage == null) {
            result = this;
        } else if (usage.getServicePlanName().equals(servicePlanName)) {
            List<ServiceUsageMonthly> u = new ArrayList<>();
            for (ServiceUsageMonthly su: usage.getUsages()) {
                for (ServiceUsageMonthly suu: usages) {
                    u.add(suu.combine(su));
                }
            }
            String newServicePlanGuid = usage.getServicePlanGuid();
            if (!usage.getServicePlanGuid().contains(this.servicePlanGuid)) {
                newServicePlanGuid = String.join(",", this.servicePlanGuid, usage.getServicePlanGuid());
            }
            result =
                ServicePlanUsageMonthly
                    .builder()
                        .servicePlanGuid(newServicePlanGuid)
                        .servicePlanName(usage.getServicePlanName())
                        .usages(u)
                        .build();
        } else {
            result = usage;
        }
        return result;
    }

}
