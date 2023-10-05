package io.pivotal.cfapp.domain.accounting.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
@JsonPropertyOrder({"report_time", "monthly_reports", "yearly_reports"})
public class TaskUsageReport {

    @JsonProperty("report_time")
    private String reportTime;

    @Default
    @JsonProperty("monthly_reports")
    private List<TaskUsageMonthly> monthlyReports = new ArrayList<>();

    @Default
    @JsonProperty("yearly_reports")
    private List<TaskUsageYearly> yearlyReports = new ArrayList<>();

    @JsonCreator
    public TaskUsageReport(
        @JsonProperty("report_time") String reportTime,
        @JsonProperty("monthly_reports") List<TaskUsageMonthly> monthlyReports,
        @JsonProperty("yearly_reports") List<TaskUsageYearly> yearlyReports) {
        this.reportTime = reportTime;
        this.monthlyReports = monthlyReports;
        this.yearlyReports = yearlyReports;
    }


    public static TaskUsageReport aggregate(List<TaskUsageReport> source) {
        TaskUsageReportBuilder report = TaskUsageReport.builder();
        Map<String, TaskUsageMonthly> monthlyReports = new HashMap<>();
        Map<Integer, TaskUsageYearly> yearlyReports = new HashMap<>();
        report.reportTime(LocalDateTime.now().toString());
        source.forEach(aur -> {
                for (TaskUsageMonthly smr: aur.getMonthlyReports()) {
                    if (monthlyReports.isEmpty()) {
                        monthlyReports.put(smr.getYearAndMonth(), smr);
                    } else {
                        TaskUsageMonthly existing = monthlyReports.get(smr.getYearAndMonth());
                        monthlyReports.put(smr.getYearAndMonth(), smr.combine(existing));
                    }
                }
                for (TaskUsageYearly syr: aur.getYearlyReports()) {
                    if (yearlyReports.isEmpty()) {
                        yearlyReports.put(syr.getYear(), syr);
                    } else {
                        TaskUsageYearly existing = yearlyReports.get(syr.getYear());
                        yearlyReports.put(syr.getYear(), syr.combine(existing));
                    }
                }
        });
        List<TaskUsageMonthly> sortedMonthlyReports = new ArrayList<>();
        sortedMonthlyReports.addAll(monthlyReports.values());
        sortedMonthlyReports.sort(Comparator.comparing(TaskUsageMonthly::getYearAndMonth));
        report.monthlyReports(sortedMonthlyReports);
        List<TaskUsageYearly> sortedYearlyReports = new ArrayList<>();
        sortedYearlyReports.addAll(yearlyReports.values());
        sortedYearlyReports.sort(Comparator.comparing(TaskUsageYearly::getYear));
        report.yearlyReports(sortedYearlyReports);
        return report.build();
    }
}

