package io.pivotal.cfapp.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.pivotal.cfapp.config.HooverSettings;
import io.pivotal.cfapp.domain.JavaAppDetail;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SpringApplicationClient {

    private final WebClient client;
    private final HooverSettings settings;

    @Autowired
    public SpringApplicationClient(
        WebClient client,
        HooverSettings settings) {
        this.client = client;
        this.settings = settings;
    }


    public Mono<List<JavaAppDetail>> assembleSpringApplicationDetail() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        return
            butlers
                .flatMap(b -> obtainSpringApplicationDetail(b.getValue())
                .map(ad -> JavaAppDetail.from(ad).foundation(b.getKey()).build()))
                .collectList();
    }

    protected Flux<JavaAppDetail> obtainSpringApplicationDetail(String baseUrl) {
        String uri = baseUrl + "/snapshot/detail/ai/spring";
        return client
                .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToFlux(JavaAppDetail.class)
                    .timeout(settings.getTimeout(), Mono.just(JavaAppDetail.builder().build()))
                    .onErrorResume(
                        WebClientResponseException.class,
                        e -> {
                            log.warn(String.format("Could not obtain Spring application details from %s", uri), e);
                            return Mono.just(JavaAppDetail.builder().build());
                        }
                    );
    }

    public Mono<Map<String, Integer>> calculateSpringApplicationDependencyFrequency() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        return
            butlers
                .flatMap(b -> obtainSpringApplicationDependencyFrequency(b.getValue()))
                .flatMapIterable(Map::entrySet)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    Integer::sum
                ));
    }

    protected Mono<Map<String, Integer>> obtainSpringApplicationDependencyFrequency(String baseUrl) {
        String uri = baseUrl + "/snapshot/summary/ai/spring";
        return client
                .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Integer>>() {})
                    .timeout(settings.getTimeout(), Mono.just(Collections.emptyMap()))
                    .onErrorResume(
                        WebClientResponseException.class,
                        e -> {
                            log.warn(String.format("Could not obtain Spring dependency frequency from %s", uri), e);
                            return Mono.just(Collections.emptyMap());
                        }
                    );
    }

}