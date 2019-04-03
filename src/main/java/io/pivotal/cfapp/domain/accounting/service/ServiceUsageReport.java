package io.pivotal.cfapp.domain.accounting.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"report_time", "monthly_service_reports", "yearly_service_report"})
public class ServiceUsageReport {

    @JsonProperty("report_time")
    public String reportTime;

    @JsonProperty("monthly_service_reports")
    public List<ServiceUsageMonthlyAggregate> monthlyServiceReports;

    @JsonProperty("yearly_service_report")
    public List<ServiceUsageYearlyAggregate> yearlyServiceReports;

    public static ServiceUsageReport aggregate(List<ServiceUsageReport> source) {
        ServiceUsageReport report = new ServiceUsageReport();
        List<ServiceUsageMonthlyAggregate> monthlyReports = new CopyOnWriteArrayList<>();
        List<ServiceUsageYearlyAggregate> yearlyReports = new CopyOnWriteArrayList<>();
        report.setReportTime(LocalDateTime.now().toString());
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
            if (yearlyReports.isEmpty()) {
                yearlyReports.addAll(aur.getYearlyServiceReports());
            } else {
                for (ServiceUsageYearlyAggregate yr: yearlyReports){
                    for (ServiceUsageYearlyAggregate syr: aur.getYearlyServiceReports()) {
                        if (!yr.combine(syr)) {
                            yearlyReports.add(syr);
                        }
                    }
                }
            }
        });
        report.setMonthlyServiceReports(monthlyReports);
        report.setYearlyServiceReports(yearlyReports);
        return report;
    }

}
