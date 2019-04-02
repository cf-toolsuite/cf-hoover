package io.pivotal.cfapp.domain.accounting.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"year", "total_task_runs", "maximum_concurrent_tasks", "task_hours"})
public class TaskUsageYearly {

    @JsonProperty("year")
    private Integer year;

    @JsonProperty("total_task_runs")
    private Integer totalTaskRuns;

    @JsonProperty("maximum_concurrent_tasks")
    private Integer maximumConcurrentTasks;

    @JsonProperty("task_hours")
    private Double taskHours;

    @JsonIgnore
    public boolean combine(TaskUsageYearly usage) {
        boolean combined = false;
        if (usage.getYear().equals(year)) {
            this.totalTaskRuns += usage.getTotalTaskRuns();
            this.maximumConcurrentTasks += usage.getMaximumConcurrentTasks();
            this.taskHours += usage.getTaskHours();
            combined = true;
        }
        return combined;
    }
}