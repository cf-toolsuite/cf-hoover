package io.pivotal.cfapp.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.pivotal.cfapp.config.HooverSettings;
import io.pivotal.cfapp.domain.SpaceUsers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SpaceUsersService {

	private final WebClient client;
    private final HooverSettings settings;

    @Autowired
    public SpaceUsersService(
        WebClient client,
        HooverSettings settings) {
        this.client = client;
        this.settings = settings;
	}

	public Flux<SpaceUsers> findAll() {
		Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
		return
			butlers.flatMap(b -> client
									.get()
										.uri("https://" + b.getValue() + "/snapshot/spaces/users")
										.retrieve()
										.bodyToFlux(SpaceUsers.class)
										.map(su -> SpaceUsers.from(su).foundation(b.getKey()).build()));
	}

	public Flux<String> obtainAccountNames() {
		Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
		return butlers.flatMap(b -> client
									.get()
										.uri("https://" + b.getValue() + "/snapshot/users")
										.retrieve()
										.bodyToFlux(String.class))
										.map(b -> b.replace("[", ""))
										.map(e -> e.replace("]", ""))
										.map(s -> s.split("\\s*,\\s*"))
										.flatMap(sa -> Flux.fromArray(sa))
										.map(un -> un.replace("\"",""))
										.distinct();
	}

	public Mono<Long> totalAccounts() {
		return obtainAccountNames().count();
	}

}