package io.pivotal.cfapp.domain.accounting.application;

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
public class AppUsageReport {

    @JsonProperty("report_time")
    private String reportTime;

    @Default
    @JsonProperty("monthly_reports")
    private List<AppUsageMonthly> monthlyReports = new ArrayList<>();

    @Default
    @JsonProperty("yearly_reports")
    private List<AppUsageYearly> yearlyReports = new ArrayList<>();

    @JsonCreator
    public AppUsageReport(
        @JsonProperty("report_time") String reportTime,
        @JsonProperty("monthly_reports") List<AppUsageMonthly> monthlyReports,
        @JsonProperty("yearly_reports") List<AppUsageYearly> yearlyReports) {
        this.reportTime = reportTime;
        this.monthlyReports = monthlyReports;
        this.yearlyReports = yearlyReports;
    }


    public static AppUsageReport aggregate(List<AppUsageReport> source) {
        AppUsageReportBuilder report = AppUsageReport.builder();
        Map<String, AppUsageMonthly> monthlyReports = new HashMap<>();
        Map<Integer, AppUsageYearly> yearlyReports = new HashMap<>();
        report.reportTime(LocalDateTime.now().toString());
        source.forEach(aur -> {
                for (AppUsageMonthly smr: aur.getMonthlyReports()) {
                    if (monthlyReports.isEmpty()) {
                        monthlyReports.put(smr.getYearAndMonth(), smr);
                    } else {
                        AppUsageMonthly existing = monthlyReports.get(smr.getYearAndMonth());
                        monthlyReports.put(smr.getYearAndMonth(), smr.combine(existing));
                    }
                }
                for (AppUsageYearly syr: aur.getYearlyReports()) {
                    if (yearlyReports.isEmpty()) {
                        yearlyReports.put(syr.getYear(), syr);
                    } else {
                        AppUsageYearly existing = yearlyReports.get(syr.getYear());
                        yearlyReports.put(syr.getYear(), syr.combine(existing));
                    }
                }
        });
        List<AppUsageMonthly> sortedMonthlyReports = new ArrayList<>();
        sortedMonthlyReports.addAll(monthlyReports.values());
        sortedMonthlyReports.sort(Comparator.comparing(AppUsageMonthly::getYearAndMonth));
        report.monthlyReports(sortedMonthlyReports);
        List<AppUsageYearly> sortedYearlyReports = new ArrayList<>();
        sortedYearlyReports.addAll(yearlyReports.values());
        sortedYearlyReports.sort(Comparator.comparing(AppUsageYearly::getYear));
        report.yearlyReports(sortedYearlyReports);
        return report.build();
    }
}