package io.pivotal.cfapp.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
public class SpringApplicationDetailClient {

    private final WebClient client;
    private final HooverSettings settings;

    @Autowired
    public SpringApplicationDetailClient(
        WebClient client,
        HooverSettings settings) {
        this.client = client;
        this.settings = settings;
    }


    public Mono<List<JavaAppDetail>> assembleSpringApplicationDetail() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        return
            butlers
                .flatMap(b -> obtainSpringApplicationDetail("https://" + b.getValue())
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
                            log.warn(String.format("Could not obtain JavaAppDetail from %s", uri), e);
                            return Mono.just(JavaAppDetail.builder().build());
                        }
                    );
    }

}