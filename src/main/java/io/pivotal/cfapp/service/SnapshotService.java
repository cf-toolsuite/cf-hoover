package io.pivotal.cfapp.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.pivotal.cfapp.config.HooverSettings;
import io.pivotal.cfapp.domain.AppDetail;
import io.pivotal.cfapp.domain.AppRelationship;
import io.pivotal.cfapp.domain.ApplicationCounts;
import io.pivotal.cfapp.domain.ServiceInstanceCounts;
import io.pivotal.cfapp.domain.ServiceInstanceDetail;
import io.pivotal.cfapp.domain.SnapshotDetail;
import io.pivotal.cfapp.domain.SnapshotSummary;
import io.pivotal.cfapp.report.AppDetailCsvReport;
import io.pivotal.cfapp.report.ServiceInstanceDetailCsvReport;
import io.pivotal.cfapp.task.AppDetailRetrievedEvent;
import io.pivotal.cfapp.task.ServiceInstanceDetailRetrievedEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SnapshotService {

    private final WebClient client;
    private final HooverSettings settings;

    @Autowired
    public SnapshotService(
        WebClient client,
        HooverSettings settings) {
        this.client = client;
        this.settings = settings;
    }

    public Mono<String> assembleCsvAIReport() {
        return assembleApplicationDetail()
                .map(r -> new AppDetailRetrievedEvent(this).detail(r))
                .map(event -> new AppDetailCsvReport().generateDetail(event));
    }

    public Mono<String> assembleCsvSIReport() {
        return assembleServiceInstanceDetail()
                .map(r -> new ServiceInstanceDetailRetrievedEvent(this).detail(r))
                .map(event -> new ServiceInstanceDetailCsvReport().generateDetail(event));
    }

    public Mono<List<AppDetail>> assembleApplicationDetail() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        Flux<AppDetail> detail =
            butlers.flatMap(b -> obtainSnapshotDetail("https://" + b.getValue())
                                    .flatMapMany(sd -> Flux.fromIterable(sd.getApplications()))
                                    .map(ad -> AppDetail.from(ad).foundation(b.getKey()).build()));
        return detail.collectList();
    }

    public Mono<List<ServiceInstanceDetail>> assembleServiceInstanceDetail() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        Flux<ServiceInstanceDetail> detail =
            butlers.flatMap(b -> obtainSnapshotDetail("https://" + b.getValue())
                                    .flatMapMany(sd -> Flux.fromIterable(sd.getServiceInstances()))
                                    .map(ad -> ServiceInstanceDetail.from(ad).foundation(b.getKey()).build()));
        return detail.collectList();
    }

    public Mono<List<AppRelationship>> assembleApplicationRelationships() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        Flux<AppRelationship> relations =
            butlers.flatMap(b -> obtainSnapshotDetail("https://" + b.getValue())
                                    .flatMapMany(sd -> Flux.fromIterable(sd.getApplicationRelationships()))
                                    .map(ad -> AppRelationship.from(ad).foundation(b.getKey()).build()));
        return relations.collectList();
    }

    protected Mono<SnapshotDetail> obtainSnapshotDetail(String baseUrl) {
        return client
                .get()
                    .uri(baseUrl + "/snapshot/detail")
                    .retrieve()
                    .bodyToMono(SnapshotDetail.class);
    }

    protected Mono<SnapshotSummary> obtainSnapshotSummary(String baseUrl) {
        return client
                .get()
                    .uri(baseUrl + "/snapshot/summary")
                    .retrieve()
                    .bodyToMono(SnapshotSummary.class);
    }

    public Mono<SnapshotDetail> assembleSnapshotDetail() {
        return assembleApplicationDetail()
                        .map(ad -> SnapshotDetail.builder().applications(ad))
                        .flatMap(b -> assembleServiceInstanceDetail()
                                        .map(sid -> b.serviceInstances(sid)))
                        .flatMap(b -> assembleApplicationRelationships()
                                        .map(ar -> b.applicationRelationships(ar)))
                        .flatMap(b -> assembleUserAccounts()
                                        .map(ua -> b.userAccounts(ua)))
                        .flatMap(b -> assembleServiceAccounts()
                                            .map(sa -> b.serviceAccounts(sa).build()));
    }

    public Mono<SnapshotSummary> assembleSnapshotSummary() {
        return assembleApplicationCounts()
                .map(ac -> SnapshotSummary.builder().applicationCounts(ac))
                .flatMap(b -> assembleServiceInstanceCounts().map(sic -> b.serviceInstanceCounts(sic).build()));
    }

    public Mono<Set<String>> assembleUserAccounts() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        Flux<String> accounts =
            butlers.flatMap(b -> obtainSnapshotDetail("https://" + b.getValue())
                                    .flatMapMany(sd -> Flux.fromIterable(sd.getUserAccounts())));
        return accounts.collect(Collectors.toCollection(TreeSet::new));
    }

    public Mono<Set<String>> assembleServiceAccounts() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        Flux<String> accounts =
            butlers.flatMap(b -> obtainSnapshotDetail("https://" + b.getValue())
                                    .flatMapMany(sd -> Flux.fromIterable(sd.getServiceAccounts())));
        return accounts.collect(Collectors.toCollection(TreeSet::new));
    }

    protected Mono<ApplicationCounts> assembleApplicationCounts() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        return butlers
                .flatMap(b -> obtainSnapshotSummary("https://" + b.getValue()))
                                .map(sd -> sd.getApplicationCounts())
                                .collectList()
                                .map(acl -> ApplicationCounts.aggregate(acl));
    }

    protected Mono<ServiceInstanceCounts> assembleServiceInstanceCounts() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        return butlers
                .flatMap(b -> obtainSnapshotSummary("https://" + b.getValue()))
                                .map(sd -> sd.getServiceInstanceCounts())
                                .collectList()
                                .map(acl -> ServiceInstanceCounts.aggregate(acl));
    }
}