package io.pivotal.cfapp.client;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.pivotal.cfapp.config.HooverSettings;
import io.pivotal.cfapp.domain.Demographic;
import io.pivotal.cfapp.domain.Demographics;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DemographicsClient {

    private final SnapshotClient snapshotClient;
	private final WebClient client;
    private final HooverSettings settings;

    @Autowired
    public DemographicsClient(
        SnapshotClient snapshotClient,
        WebClient client,
        HooverSettings settings) {
        this.snapshotClient = snapshotClient;
        this.client = client;
        this.settings = settings;
	}

    // We're going to get raw counts from each cf-butler instance registered w/ cf-hoover
    // User and service accounts represent a special case, we collect and merge sets before counting
	public Mono<Demographics> aggregateDemographics() {
        Mono<Long> serviceAccounts = snapshotClient.assembleServiceAccounts().flatMapMany(sa -> Flux.fromIterable(sa)).count();
        Mono<Long> userAccounts = snapshotClient.assembleUserAccounts().flatMapMany(sa -> Flux.fromIterable(sa)).count();
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
        String uri = baseUrl + "/snapshot/demographics";
        return client
                .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Demographic.class)
                    .timeout(settings.getTimeout(), Mono.just(Demographic.builder().build()))
                    .onErrorResume(
                        WebClientResponseException.class,
                        e -> {
                            log.warn("Could not obtain Demographic from {}", uri);
                            return Mono.just(Demographic.builder().build());
                        }
                    )
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