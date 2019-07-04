package io.pivotal.cfapp.service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.pivotal.cfapp.config.HooverSettings;
import io.pivotal.cfapp.domain.SnapshotDetail;
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
										.bodyToMono(SpaceUsers.class)
										.timeout(settings.getTimeout(), Mono.just(SpaceUsers.builder().build()))
										.map(su -> SpaceUsers.from(su).foundation(b.getKey()).build()));
	}

	public Mono<Set<String>> obtainAccountNames() {
		Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
		return butlers.flatMap(b -> client
									.get()
										.uri("https://" + b.getValue() + "/snapshot/detail")
										.retrieve()
										.bodyToMono(SnapshotDetail.class))
										.timeout(settings.getTimeout(), Mono.just(SnapshotDetail.builder().build()))
										.flatMap(sd ->
													Flux.concat(
														Flux.fromIterable(sd.getServiceAccounts()),
														Flux.fromIterable(sd.getUserAccounts())
													)
										)
										.collect(Collectors.toSet());
	}

	public Mono<Long> totalAccounts() {
		return obtainAccountNames()
				.flatMapMany(s -> Flux.fromIterable(s))
				.count();
	}

}