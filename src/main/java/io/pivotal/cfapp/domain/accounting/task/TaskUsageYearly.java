package io.pivotal.cfapp.domain.accounting.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
@JsonPropertyOrder({"year", "total_task_runs", "maximum_concurrent_tasks", "task_hours"})
public class TaskUsageYearly {

    @JsonProperty("year")
    private Integer year;

    @Default
    @JsonProperty("total_task_runs")
    private Integer totalTaskRuns = 0;

    @Default
    @JsonProperty("maximum_concurrent_tasks")
    private Integer maximumConcurrentTasks = 0;

    @Default
    @JsonProperty("task_hours")
    private Double taskHours = 0.0;

    @JsonCreator
    public TaskUsageYearly(
        @JsonProperty("year") Integer year,
        @JsonProperty("total_task_runs") Integer totalTaskRuns,
        @JsonProperty("maximum_concurrent_tasks") Integer maximumConcurrentTasks,
        @JsonProperty("task_hours") Double taskHours) {
        this.year = year;
        this.totalTaskRuns = totalTaskRuns;
        this.maximumConcurrentTasks = maximumConcurrentTasks;
        this.taskHours = taskHours;
    }

    @JsonIgnore
    public TaskUsageYearly combine(TaskUsageYearly usage) {
        TaskUsageYearly result = null;
        if (usage == null) {
            result = this;
        } else if (usage.getYear().equals(year)) {
            result =
                TaskUsageYearly
                    .builder()
                        .year(usage.getYear())
                        .totalTaskRuns(this.totalTaskRuns + usage.getTotalTaskRuns())
                        .maximumConcurrentTasks(this.maximumConcurrentTasks + usage.getMaximumConcurrentTasks())
                        .taskHours(this.taskHours + usage.getTaskHours())
                        .build();
        } else {
            result = usage;
        }
        return result;
    }
}