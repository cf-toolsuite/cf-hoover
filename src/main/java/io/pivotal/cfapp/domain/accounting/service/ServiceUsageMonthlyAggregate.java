
package io.pivotal.cfapp.domain.accounting.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
    public ServiceUsageMonthlyAggregate combine(ServiceUsageMonthlyAggregate usage) {
        ServiceUsageMonthlyAggregate result = null;
        if (usage == null) {
            result = this;
        } else if (usage.getServiceName().equals(serviceName)) {
            Map<String, ServiceUsageMonthly> monthlyUsage = new HashMap<>();
            Map<String, ServicePlanUsageMonthly> monthlyPlans = new HashMap<>();
            usage.getUsages().forEach(su -> {
                for (ServiceUsageMonthly suu: usages) {
                    if (monthlyUsage.isEmpty()) {
                        monthlyUsage.put(suu.getYearAndMonth(), suu);
                    } else {
                        ServiceUsageMonthly existing = monthlyUsage.get(suu.getYearAndMonth());
                        monthlyUsage.put(suu.getYearAndMonth(), suu.combine(existing));
                    }
                }
            });
            usage.getPlans().forEach(pu -> {
                for (ServicePlanUsageMonthly spu: plans) {
                    if (monthlyPlans.isEmpty()) {
                        monthlyPlans.put(spu.getServicePlanName(), spu);
                    } else {
                        ServicePlanUsageMonthly existing = monthlyPlans.get(spu.getServicePlanName());
                        monthlyPlans.put(spu.getServicePlanName(), spu.combine(existing));
                    }
                }
            });
            List<ServiceUsageMonthly> sortedMonthlyUsage = new ArrayList<>();
            sortedMonthlyUsage.addAll(monthlyUsage.values());
            sortedMonthlyUsage.sort(Comparator.comparing(ServiceUsageMonthly::getYearAndMonth));
            List<ServicePlanUsageMonthly> sortedMonthlyPlans = new ArrayList<>();
            sortedMonthlyPlans.addAll(monthlyPlans.values());
            sortedMonthlyPlans.sort(Comparator.comparing(ServicePlanUsageMonthly::getServicePlanName));
            String newServiceGuid = usage.getServiceGuid();
            if (!usage.getServiceGuid().contains(this.serviceGuid)) {
                newServiceGuid = String.join(",", this.serviceGuid, usage.getServiceGuid());
            }
            result =
                ServiceUsageMonthlyAggregate
                    .builder()
                        .serviceGuid(newServiceGuid)
                        .serviceName(usage.getServiceName())
                        .usages(sortedMonthlyUsage)
                        .plans(sortedMonthlyPlans)
                        .build();
        } else {
            result = usage;
        }
        return result;
    }

}
