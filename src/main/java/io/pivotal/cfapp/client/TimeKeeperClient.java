package io.pivotal.cfapp.client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.pivotal.cfapp.config.HooverSettings;
import io.pivotal.cfapp.domain.TimeKeeper;
import io.pivotal.cfapp.domain.TimeKeepers;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TimeKeeperClient {

    private final WebClient client;
    private final HooverSettings settings;

    @Autowired
    public TimeKeeperClient(
        WebClient client,
        HooverSettings settings) {
        this.client = client;
        this.settings = settings;
    }

    public Mono<Set<TimeKeeper>> assembleDateTimeCollection() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        Flux<TimeKeeper> result =
            butlers.flatMap(b -> obtainDateTimeCollected("https://" + b.getValue())
                                    .map(dtc -> TimeKeeper.builder().foundation(b.getKey()).collectionDateTime(dtc).build()));
        return result.collectList().map(l -> Set.copyOf(l));
    }

    protected Mono<LocalDateTime> obtainDateTimeCollected(String baseUrl) {
        String uri = baseUrl + "/collect";
        return client
                .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(s -> LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .timeout(settings.getTimeout(), Mono.empty())
                    .onErrorResume(
                        WebClientResponseException.class,
                        e -> {
                            log.warn("Could not obtain X-DateTime-Collected from {}", uri);
                            return Mono.empty();
                        }
                    );
    }

    public Mono<TimeKeepers> assembleTimeKeepers() {
        return assembleDateTimeCollection()
                .map(tk -> TimeKeepers.builder().timeKeepers(tk).build());
    }

}