package io.pivotal.cfapp.domain.accounting.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
@JsonPropertyOrder({"report_time", "monthly_service_reports", "yearly_service_reports"})
public class ServiceUsageReport {

    @JsonProperty("report_time")
    public String reportTime;

    @Default
    @JsonProperty("monthly_service_reports")
    public List<ServiceUsageMonthlyAggregate> monthlyServiceReports = new ArrayList<>();

    @Default
    @JsonProperty("yearly_service_report")
    public List<ServiceUsageYearlyAggregate> yearlyServiceReport = new ArrayList<>();

    @JsonCreator
    public ServiceUsageReport(
        @JsonProperty("report_time") String reportTime,
        @JsonProperty("monthly_service_reports") List<ServiceUsageMonthlyAggregate> monthlyServiceReports,
        @JsonProperty("yearly_service_report") List<ServiceUsageYearlyAggregate> yearlyServiceReport) {
        this.reportTime = reportTime;
        this.monthlyServiceReports = monthlyServiceReports;
        this.yearlyServiceReport = yearlyServiceReport;
    }


    public static ServiceUsageReport aggregate(List<ServiceUsageReport> source) {
        ServiceUsageReportBuilder report = ServiceUsageReport.builder();
        List<ServiceUsageMonthlyAggregate> monthlyReports = new CopyOnWriteArrayList<>();
        List<ServiceUsageYearlyAggregate> yearlyReport = new CopyOnWriteArrayList<>();
        report.reportTime(LocalDateTime.now().toString());
        source.forEach(aur -> {
            if (monthlyReports.isEmpty()) {
                monthlyReports.addAll(aur.getMonthlyServiceReports());
            } else {
                for (ServiceUsageMonthlyAggregate mr: monthlyReports) {
                    for (ServiceUsageMonthlyAggregate smr: aur.getMonthlyServiceReports()) {
                        if (!mr.combine(smr)) {
                            monthlyReports.add(smr);
                        }
                    }
                }
            }
            if (yearlyReport.isEmpty()) {
                yearlyReport.addAll(aur.getYearlyServiceReport());
            } else {
                for (ServiceUsageYearlyAggregate yr: yearlyReport){
                    for (ServiceUsageYearlyAggregate syr: aur.getYearlyServiceReport()) {
                        if (!yr.combine(syr)) {
                            yearlyReport.add(syr);
                        }
                    }
                }
            }
        });
        report.monthlyServiceReports(monthlyReports);
        report.yearlyServiceReport(yearlyReport);
        return report.build();
    }

}
