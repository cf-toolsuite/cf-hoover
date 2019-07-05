package io.pivotal.cfapp.domain.accounting.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
        List<TaskUsageMonthly> monthlyReports = new CopyOnWriteArrayList<>();
        List<TaskUsageYearly> yearlyReports = new CopyOnWriteArrayList<>();
        report.reportTime(LocalDateTime.now().toString());
        source.forEach(aur -> {
            if (monthlyReports.isEmpty()) {
                monthlyReports.addAll(aur.getMonthlyReports());
            } else {
                for (TaskUsageMonthly mr: monthlyReports){
                    for (TaskUsageMonthly smr: aur.getMonthlyReports()) {
                        if (!mr.combine(smr)) {
                            monthlyReports.add(smr);
                        }
                    }
                }
            }
            if (yearlyReports.isEmpty()) {
                yearlyReports.addAll(aur.getYearlyReports());
            } else {
                for (TaskUsageYearly yr: yearlyReports){
                    for (TaskUsageYearly syr: aur.getYearlyReports()) {
                        if (!yr.combine(syr)) {
                            yearlyReports.add(syr);
                        }
                    }
                }
            }
        });
        List<TaskUsageMonthly> sortedMonthlyReports = new ArrayList<>();
        sortedMonthlyReports.addAll(monthlyReports);
        sortedMonthlyReports.sort(Comparator.comparing(TaskUsageMonthly::getYearAndMonth));
        report.monthlyReports(sortedMonthlyReports);
        List<TaskUsageYearly> sortedYearlyReports = new ArrayList<>();
        sortedYearlyReports.addAll(yearlyReports);
        sortedYearlyReports.sort(Comparator.comparing(TaskUsageYearly::getYear));
        report.yearlyReports(sortedYearlyReports);
        return report.build();
    }
}

