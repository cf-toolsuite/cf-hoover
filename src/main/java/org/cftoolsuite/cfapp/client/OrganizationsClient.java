package org.cftoolsuite.cfapp.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import org.cftoolsuite.cfapp.config.HooverSettings;
import org.cftoolsuite.cfapp.domain.Organization;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class OrganizationsClient {

    private final WebClient client;
    private final HooverSettings settings;

    @Autowired
    public OrganizationsClient(
        WebClient client,
        HooverSettings settings) {
        this.client = client;
        this.settings = settings;
    }

    public Mono<List<Organization>> assembleOrganizations() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        Flux<Organization> organizations =
            butlers.flatMap(b -> obtainOrganizations(b.getValue())
                                    .flatMapMany(lo -> Flux.fromIterable(lo))
                                    .map(o -> Organization.from(o).foundation(b.getKey()).build()));
        return organizations.collectList();
    }

    protected Mono<List<Organization>> obtainOrganizations(String baseUrl) {
        String uri = baseUrl + "/snapshot/organizations";
        return client
                .get()
                    .uri(uri)
                    .retrieve()
                    .toEntityList(Organization.class)
                    .map(response -> response.getBody())
                    .timeout(settings.getTimeout(), Mono.empty())
                    .onErrorResume(
                        WebClientResponseException.class,
                        e -> {
                            log.warn(String.format("Could not obtain organizations from %s", uri), e);
                            return Mono.empty();
                        }
                    );
    }

}