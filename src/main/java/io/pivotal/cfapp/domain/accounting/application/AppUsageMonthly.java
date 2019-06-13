package io.pivotal.cfapp.domain.accounting.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"month", "year", "average_app_instances","maximum_app_instances", "app_instance_hours"})
public class AppUsageMonthly {

    @JsonProperty("month")
    private Integer month;

    @JsonProperty("year")
    private Integer year;

    @JsonProperty("average_app_instances")
    private Double averageAppInstances;

    @JsonProperty("maximum_app_instances")
    private Integer maximumAppInstances;

    @JsonProperty("app_instance_hours")
    private Double appInstanceHours;

    @JsonIgnore
    public boolean combine(AppUsageMonthly usage) {
        boolean combined = false;
        if (usage.getMonth().equals(month) && usage.getYear().equals(year)) {
            this.averageAppInstances += usage.getAverageAppInstances();
            this.maximumAppInstances += usage.getMaximumAppInstances();
            this.appInstanceHours += usage.getAppInstanceHours();
            combined = true;
        }
        return combined;
    }

    @JsonIgnore
    public String getYearAndMonth() {
        return String.join("-", String.valueOf(year), String.format("%02d", month));
    }

}