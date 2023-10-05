package io.pivotal.cfapp.domain.accounting.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
@JsonPropertyOrder({ "service_plan_name", "service_plan_guid", "year", "duration_in_hours", "maximum_instances", "average_instances"})
public class ServicePlanUsageYearly {

    @JsonProperty("service_plan_name")
    public String servicePlanName;

    @JsonProperty("service_plan_guid")
    public String servicePlanGuid;

    @JsonProperty("year")
    public Integer year;

    @Default
    @JsonProperty("duration_in_hours")
    public Double durationInHours = 0.0;

    @Default
    @JsonProperty("maximum_instances")
    public Integer maximumInstances = 0;

    @Default
    @JsonProperty("average_instances")
    public Double averageInstances = 0.0;

    @JsonCreator
    public ServicePlanUsageYearly(
        @JsonProperty("service_plan_name") String servicePlanName,
        @JsonProperty("service_plan_guid") String servicePlanGuid,
        @JsonProperty("year") Integer year,
        @JsonProperty("duration_in_hours") Double durationInHours,
        @JsonProperty("maximum_instances") Integer maximumInstances,
        @JsonProperty("average_instances") Double averageInstances) {
        this.servicePlanName = servicePlanName;
        this.servicePlanGuid = servicePlanGuid;
        this.year = year;
        this.durationInHours = durationInHours;
        this.maximumInstances = maximumInstances;
        this.averageInstances = averageInstances;
    }

    @JsonIgnore
    public ServicePlanUsageYearly combine(ServicePlanUsageYearly usage) {
        ServicePlanUsageYearly result = null;
        if (usage == null) {
            result = this;
        } else if (usage.getYear().equals(year) && usage.getServicePlanName().equals(servicePlanName)) {
            String newServicePlanGuid = usage.getServicePlanGuid();
            if (!usage.getServicePlanGuid().contains(this.servicePlanGuid)) {
                newServicePlanGuid = String.join(",", this.servicePlanGuid, usage.getServicePlanGuid());
            }
            result =
                ServicePlanUsageYearly
                    .builder()
                        .year(usage.getYear())
                        .servicePlanGuid(newServicePlanGuid)
                        .servicePlanName(usage.getServicePlanName())
                        .durationInHours(this.durationInHours + usage.getDurationInHours())
                        .averageInstances(this.averageInstances + usage.getAverageInstances())
                        .maximumInstances(this.maximumInstances + usage.getMaximumInstances())
                        .build();
        } else {
            result = usage;
        }
        return result;
    }

}
