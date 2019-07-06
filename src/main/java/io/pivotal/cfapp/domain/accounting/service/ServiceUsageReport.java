package io.pivotal.cfapp.domain.accounting.service;

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
        Map<String, ServiceUsageMonthlyAggregate> monthlyReports = new HashMap<>();
        Map<String, ServiceUsageYearlyAggregate> yearlyReport = new HashMap<>();
        report.reportTime(LocalDateTime.now().toString());
        source.forEach(sur -> {
            for (ServiceUsageMonthlyAggregate smr: sur.getMonthlyServiceReports()) {
                if (monthlyReports.isEmpty()) {
                    monthlyReports.put(smr.getServiceName(), smr);
                } else {
                    ServiceUsageMonthlyAggregate existing = monthlyReports.get(smr.getServiceName());
                    monthlyReports.put(smr.getServiceName(), smr.combine(existing));
                }
            }
            for (ServiceUsageYearlyAggregate syr: sur.getYearlyServiceReport()) {
                if (yearlyReport.isEmpty()) {
                    yearlyReport.put(syr.getServiceName(), syr);
                } else {
                    ServiceUsageYearlyAggregate existing = yearlyReport.get(syr.getServiceName());
                    yearlyReport.put(syr.getServiceName(), syr.combine(existing));
                }
            }
        });
        List<ServiceUsageMonthlyAggregate> sortedMonthlyReports = new ArrayList<>();
        sortedMonthlyReports.addAll(monthlyReports.values());
        sortedMonthlyReports.sort(Comparator.comparing(ServiceUsageMonthlyAggregate::getServiceName));
        report.monthlyServiceReports(sortedMonthlyReports);
        List<ServiceUsageYearlyAggregate> sortedYearlyReports = new ArrayList<>();
        sortedYearlyReports.addAll(yearlyReport.values());
        sortedYearlyReports.sort(Comparator.comparing(ServiceUsageYearlyAggregate::getServiceName));
        report.yearlyServiceReport(sortedYearlyReports);
        return report.build();
    }

}
