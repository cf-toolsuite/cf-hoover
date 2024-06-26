package org.cftoolsuite.cfapp.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import org.cftoolsuite.cfapp.config.HooverSettings;
import org.cftoolsuite.cfapp.domain.Space;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SpacesClient {

    private final WebClient client;
    private final HooverSettings settings;

    @Autowired
    public SpacesClient(
        WebClient client,
        HooverSettings settings) {
        this.client = client;
        this.settings = settings;
    }

    public Mono<List<Space>> assembleSpaces() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        Flux<Space> spaces =
            butlers.flatMap(b -> obtainSpaces(b.getValue())
                                    .flatMapMany(ls -> Flux.fromIterable(ls))
                                    .map(s -> Space.from(s).foundation(b.getKey()).build()));
        return spaces.collectList();
    }

    protected Mono<List<Space>> obtainSpaces(String baseUrl) {
        String uri = baseUrl + "/snapshot/spaces";
        return client
                .get()
                    .uri(uri)
                    .retrieve()
                    .toEntityList(Space.class)
                    .map(response -> response.getBody())
                    .timeout(settings.getTimeout(), Mono.empty())
                    .onErrorResume(
                        WebClientResponseException.class,
                        e -> {
                            log.warn(String.format("Could not obtain spaces from %s", uri), e);
                            return Mono.empty();
                        }
                    );
    }

}