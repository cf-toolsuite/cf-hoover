package io.pivotal.cfapp.service;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.pivotal.cfapp.config.HooverSettings;
import io.pivotal.cfapp.domain.Demographic;
import io.pivotal.cfapp.domain.Demographics;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DemographicsService {

    private final SnapshotService snapshotService;
	private final WebClient client;
    private final HooverSettings settings;

    @Autowired
    public DemographicsService(
        SnapshotService snapshotService,
        WebClient client,
        HooverSettings settings) {
        this.snapshotService = snapshotService;
        this.client = client;
        this.settings = settings;
	}

    // We're going to get raw counts from each cf-butler instance registered w/ cf-hoover
    // User and service accounts represent a special case, we collect and merge sets before counting
	public Mono<Demographics> aggregateDemographics() {
        Mono<Long> serviceAccounts = snapshotService.assembleServiceAccounts().flatMapMany(sa -> Flux.fromIterable(sa)).count();
        Mono<Long> userAccounts = snapshotService.assembleUserAccounts().flatMapMany(sa -> Flux.fromIterable(sa)).count();
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        return
			butlers
				.flatMap(b -> obtainDemographics(b.getKey(), "https://" + b.getValue()))
				.collect(Collectors.toSet())
                .map(dl -> Demographics
                            .builder()
                                .demographics(dl)
                                .foundations(settings.getButlers().keySet().size()))
                .flatMap(b -> serviceAccounts.map(c -> b.serviceAccounts(c)))
                .flatMap(b -> userAccounts.map(c -> b.userAccounts(c).build()));
    }

    protected Mono<Demographic> obtainDemographics(String foundation, String baseUrl) {
        return client
                .get()
                    .uri(baseUrl + "/snapshot/demographics")
                    .retrieve()
                    .bodyToMono(Demographic.class)
                    .map(d -> Demographic
                                    .builder()
                                        .foundation(foundation)
                                        .organizations(d.getOrganizations())
                                        .spaces(d.getSpaces())
                                        .userAccounts(d.getUserAccounts())
                                        .serviceAccounts(d.getServiceAccounts())
                                        .build());
    }

}