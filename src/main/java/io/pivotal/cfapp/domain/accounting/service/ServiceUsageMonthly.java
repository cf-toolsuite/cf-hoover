package io.pivotal.cfapp.domain.accounting.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"month", "year", "duration_in_hours", "average_instances", "maximum_instances"})
public class ServiceUsageMonthly {

    @JsonProperty("month")
    public Integer month;

    @JsonProperty("year")
    public Integer year;

    @JsonProperty("duration_in_hours")
    public Double durationInHours;

    @JsonProperty("average_instances")
    public Integer averageInstances;

    @JsonProperty("maximum_instances")
    public Integer maximumInstances;

    @JsonIgnore
    public boolean combine(ServiceUsageMonthly usage) {
        boolean combined = false;
        if (usage.getYear().equals(year) && usage.getMonth().equals(month)) {
            this.durationInHours += usage.getDurationInHours();
            this.averageInstances += usage.getAverageInstances();
            this.maximumInstances += usage.getMaximumInstances();
            combined = true;
        }
        return combined;
    }

    @JsonIgnore
    public String getYearAndMonth() {
        return String.join("-", String.valueOf(year), String.format("%02d", month));
    }

}
