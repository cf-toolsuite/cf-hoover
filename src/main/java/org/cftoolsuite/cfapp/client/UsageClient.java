package org.cftoolsuite.cfapp.client;

import java.util.Map;

import org.cftoolsuite.cfapp.config.HooverSettings;
import org.cftoolsuite.cfapp.domain.accounting.application.AppUsageReport;
import org.cftoolsuite.cfapp.domain.accounting.service.ServiceUsageReport;
import org.cftoolsuite.cfapp.domain.accounting.task.TaskUsageReport;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// @see https://docs.pivotal.io/pivotalcf/2-4/opsguide/accounting-report.html

@Slf4j
@Service
public class UsageClient {

    private final WebClient client;
    private final HooverSettings settings;

    public UsageClient(
        WebClient client,
        HooverSettings settings) {
        this.client = client;
        this.settings = settings;
    }

    protected Mono<TaskUsageReport> getTaskReport(String butlerRoute) {
        String uri = butlerRoute + "/accounting/tasks";
        return client
                .get()
                    .uri(uri)
                        .retrieve()
                            .bodyToMono(TaskUsageReport.class)
                            .timeout(settings.getTimeout(), Mono.just(TaskUsageReport.builder().build()))
                            .onErrorResume(
                                WebClientResponseException.class,
                                e -> {
                                    log.warn("Could not obtain TaskUsageReport from %s".formatted(uri), e);
                                    return Mono.just(TaskUsageReport.builder().build());
                                }
                            );
    }

    protected Mono<AppUsageReport> getApplicationReport(String butlerRoute) {
        String uri = butlerRoute + "/accounting/applications";
        return client
                .get()
                    .uri(uri)
                        .retrieve()
                            .bodyToMono(AppUsageReport.class)
                            .timeout(settings.getTimeout(), Mono.just(AppUsageReport.builder().build()))
                            .onErrorResume(
                                WebClientResponseException.class,
                                e -> {
                                    log.warn("Could not obtain AppUsageReport from %s".formatted(uri), e);
                                    return Mono.just(AppUsageReport.builder().build());
                                }
                            );
    }

    protected Mono<ServiceUsageReport> getServiceReport(String butlerRoute) {
        String uri = butlerRoute + "/accounting/services";
        return client
                .get()
                    .uri(uri)
                        .retrieve()
                            .bodyToMono(ServiceUsageReport.class)
                            .timeout(settings.getTimeout(), Mono.just(ServiceUsageReport.builder().build()))
                            .onErrorResume(
                                WebClientResponseException.class,
                                e -> {
                                    log.warn("Could not obtain ServiceUsageReport from %s".formatted(uri), e);
                                    return Mono.just(ServiceUsageReport.builder().build());
                                }
                            );
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