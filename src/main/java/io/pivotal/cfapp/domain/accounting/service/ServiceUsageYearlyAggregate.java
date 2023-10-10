package io.pivotal.cfapp.domain.accounting.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
@JsonPropertyOrder({"service_name", "service_guid", "year", "duration_in_hours", "maximum_instances", "average_instances", "plans"})
public class ServiceUsageYearlyAggregate {

    @JsonProperty("service_name")
    public String serviceName;

    @JsonProperty("service_guid")
    public String serviceGuid;

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

    @Default
    @JsonProperty("plans")
    public List<ServicePlanUsageYearly> plans = new ArrayList<>();

    @JsonCreator
    public ServiceUsageYearlyAggregate(
        @JsonProperty("service_name") String serviceName,
        @JsonProperty("service_guid") String serviceGuid,
        @JsonProperty("year") Integer year,
        @JsonProperty("duration_in_hours") Double durationInHours,
        @JsonProperty("maximum_instances") Integer maximumInstances,
        @JsonProperty("average_instances") Double averageInstances,
        @JsonProperty("plans") List<ServicePlanUsageYearly> plans) {
        this.serviceName = serviceName;
        this.serviceGuid = serviceGuid;
        this.year = year;
        this.durationInHours = durationInHours;
        this.maximumInstances = maximumInstances;
        this.averageInstances = averageInstances;
        this.plans = plans;
    }

    @JsonIgnore
    public ServiceUsageYearlyAggregate combine(ServiceUsageYearlyAggregate usage) {
        ServiceUsageYearlyAggregate result = null;
        if (usage == null) {
            result = this;
        } else if (usage.getYear().equals(year) && usage.getServiceName().equals(serviceName)) {
            Map<Integer, ServicePlanUsageYearly> yearlyPlanUsage = new HashMap<>();
            usage.getPlans().forEach(pu -> {
                for (ServicePlanUsageYearly spu: plans) {
                    if (yearlyPlanUsage.isEmpty()) {
                        yearlyPlanUsage.put(spu.getYear(), spu);
                    } else {
                        ServicePlanUsageYearly existing = yearlyPlanUsage.get(spu.getYear());
                        yearlyPlanUsage.put(spu.getYear(), spu.combine(existing));
                    }
                }
            });
            List<ServicePlanUsageYearly> sortedYearlyPlans = new ArrayList<>();
            sortedYearlyPlans.addAll(yearlyPlanUsage.values());
            sortedYearlyPlans.sort(Comparator.comparing(ServicePlanUsageYearly::getYear));
            String newServiceGuid = usage.getServiceGuid();
            if (!usage.getServiceGuid().contains(this.serviceGuid)) {
                newServiceGuid = String.join(",", this.serviceGuid, usage.getServiceGuid());
            }
            result =
                ServiceUsageYearlyAggregate
                    .builder()
                    .serviceName(usage.getServiceName())
                    .serviceGuid(newServiceGuid)
                    .averageInstances(this.averageInstances + usage.getAverageInstances())
                    .maximumInstances(this.maximumInstances + usage.getMaximumInstances())
                    .durationInHours(this.durationInHours + usage.getDurationInHours())
                    .plans(sortedYearlyPlans)
                    .build();
        } else {
            result = usage;
        }
        return result;
    }
}
