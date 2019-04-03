package io.pivotal.cfapp.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.pivotal.cfapp.config.HooverSettings;
import io.pivotal.cfapp.domain.accounting.application.AppUsageReport;
import io.pivotal.cfapp.domain.accounting.service.ServiceUsageReport;
import io.pivotal.cfapp.domain.accounting.task.TaskUsageReport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// @see https://docs.pivotal.io/pivotalcf/2-4/opsguide/accounting-report.html

@Service
public class UsageService {

    private final WebClient client;
    private final HooverSettings settings;

    @Autowired
    public UsageService(
        WebClient client,
        HooverSettings settings) {
        this.client = client;
        this.settings = settings;
    }

    protected Mono<TaskUsageReport> getTaskReport(String butlerRoute) {
        String uri = "https://" + butlerRoute + "/accounting/tasks";
        return client
                .get()
                    .uri(uri)
                        .retrieve()
                            .bodyToMono(TaskUsageReport.class);
    }

    protected Mono<AppUsageReport> getApplicationReport(String butlerRoute) {
        String uri = "https://" + butlerRoute + "/accounting/applications";
        return client
                .get()
                    .uri(uri)
                        .retrieve()
                            .bodyToMono(AppUsageReport.class);
    }

    protected Mono<ServiceUsageReport> getServiceReport(String butlerRoute) {
        String uri = "https://" + butlerRoute + "/accounting/services";
        return client
                .get()
                    .uri(uri)
                        .retrieve()
                            .bodyToMono(ServiceUsageReport.class);
    }

    public Mono<TaskUsageReport> getTaskReport() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        return butlers.flatMap(b -> getTaskReport(b.getValue()))
                                        .collectList()
                                        .map(l -> TaskUsageReport.aggregate(l));
    }

    public Mono<AppUsageReport> getApplicationReport() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        return butlers.flatMap(b -> getApplicationReport(b.getValue()))
                                        .collectList()
                                        .map(l -> AppUsageReport.aggregate(l));
    }

    public Mono<ServiceUsageReport> getServiceReport() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        return butlers.flatMap(b -> getServiceReport(b.getValue()))
                                        .collectList()
                                        .map(l -> ServiceUsageReport.aggregate(l));
    }
}