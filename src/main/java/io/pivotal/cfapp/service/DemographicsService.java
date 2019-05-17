package io.pivotal.cfapp.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.pivotal.cfapp.config.HooverSettings;
import io.pivotal.cfapp.domain.Demographics;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DemographicsService {

	private final WebClient client;
    private final HooverSettings settings;

    @Autowired
    public DemographicsService(
        WebClient client,
        HooverSettings settings) {
        this.client = client;
        this.settings = settings;
	}

	public Mono<Demographics> aggregateDemographics() {
        Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
        return
			butlers
				.flatMap(b -> obtainDemographics("https://" + b.getValue()))
				.collectList()
				.map(dl -> Demographics.aggregate(dl));
    }

    protected Mono<Demographics> obtainDemographics(String baseUrl) {
        return client
                .get()
                    .uri(baseUrl + "/snapshot/demographics")
                    .retrieve()
                    .bodyToMono(Demographics.class);
    }

}