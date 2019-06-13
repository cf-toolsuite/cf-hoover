package io.pivotal.cfapp.domain.accounting.task;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"report_time", "monthly_reports", "yearly_reports"})
public class TaskUsageReport {

    @JsonProperty("report_time")
    private String reportTime;

    @JsonProperty("monthly_reports")
    private List<TaskUsageMonthly> monthlyReports;

    @JsonProperty("yearly_reports")
    private List<TaskUsageYearly> yearlyReports;

    public static TaskUsageReport aggregate(List<TaskUsageReport> source) {
        TaskUsageReport report = new TaskUsageReport();
        List<TaskUsageMonthly> monthlyReports = new CopyOnWriteArrayList<>();
        List<TaskUsageYearly> yearlyReports = new CopyOnWriteArrayList<>();
        report.setReportTime(LocalDateTime.now().toString());
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
        report.setMonthlyReports(sortedMonthlyReports);
        List<TaskUsageYearly> sortedYearlyReports = new ArrayList<>();
        sortedYearlyReports.addAll(yearlyReports);
        sortedYearlyReports.sort(Comparator.comparing(TaskUsageYearly::getYear));
        report.setYearlyReports(sortedYearlyReports);
        return report;
    }
}

