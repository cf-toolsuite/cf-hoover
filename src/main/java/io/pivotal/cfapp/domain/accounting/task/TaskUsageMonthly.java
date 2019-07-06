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
@JsonPropertyOrder({"month", "year", "total_task_runs", "maximum_concurrent_tasks", "task_hours"})
public class TaskUsageMonthly {

    @JsonProperty("month")
    private Integer month;

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
    public TaskUsageMonthly(
        @JsonProperty("month") Integer month,
        @JsonProperty("year") Integer year,
        @JsonProperty("total_task_runs") Integer totalTaskRuns,
        @JsonProperty("maximum_concurrent_tasks") Integer maximumConcurrentTasks,
        @JsonProperty("task_hours") Double taskHours) {
        this.month = month;
        this.year = year;
        this.totalTaskRuns = totalTaskRuns;
        this.maximumConcurrentTasks = maximumConcurrentTasks;
        this.taskHours = taskHours;
    }

    @JsonIgnore
    public TaskUsageMonthly combine(TaskUsageMonthly usage) {
        TaskUsageMonthly result = null;
        if (usage == null) {
            result = this;
        } else if (usage.getMonth().equals(month) && usage.getYear().equals(year)) {
            result =
                TaskUsageMonthly
                    .builder()
                        .year(usage.getYear())
                        .month(usage.getMonth())
                        .totalTaskRuns(this.totalTaskRuns + usage.getTotalTaskRuns())
                        .maximumConcurrentTasks(this.maximumConcurrentTasks + usage.getMaximumConcurrentTasks())
                        .taskHours(this.taskHours + usage.getTaskHours())
                        .build();
        } else {
            result = usage;
        }
        return result;
    }

    @JsonIgnore
    public String getYearAndMonth() {
        return String.join("-", String.valueOf(year), String.format("%02d", month));
    }
}