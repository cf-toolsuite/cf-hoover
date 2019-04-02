package io.pivotal.cfapp.domain.accounting.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"report_time", "monthly_reports", "yearly_reports"})
public class AppUsageReport {

    @JsonProperty("report_time")
    private String reportTime;

    @JsonProperty("monthly_reports")
    private List<AppUsageMonthly> monthlyReports;

    @JsonProperty("yearly_reports")
    private List<AppUsageYearly> yearlyReports;

    public static AppUsageReport aggregate(List<AppUsageReport> source) {
        AppUsageReport report = new AppUsageReport();
        List<AppUsageMonthly> monthlyReports = new CopyOnWriteArrayList<>();
        List<AppUsageYearly> yearlyReports = new CopyOnWriteArrayList<>();
        report.setReportTime(LocalDateTime.now().toString());
        source.forEach(aur -> {
            if (monthlyReports.isEmpty()) {
                monthlyReports.addAll(aur.getMonthlyReports());
            } else {
                for (AppUsageMonthly mr: monthlyReports){
                    for (AppUsageMonthly smr: aur.getMonthlyReports()) {
                        if (!mr.combine(smr)) {
                            monthlyReports.add(smr);
                        }
                    }
                }
            }
            if (yearlyReports.isEmpty()) {
                yearlyReports.addAll(aur.getYearlyReports());
            } else {
                for (AppUsageYearly yr: yearlyReports){
                    for (AppUsageYearly syr: aur.getYearlyReports()) {
                        if (!yr.combine(syr)) {
                            yearlyReports.add(syr);
                        }
                    }
                }
            }
        });
        report.setMonthlyReports(monthlyReports);
        report.setYearlyReports(yearlyReports);
        return report;
    }
}