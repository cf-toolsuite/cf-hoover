package org.cftoolsuite.cfapp.client;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.cftoolsuite.cfapp.config.HooverSettings;
import org.cftoolsuite.cfapp.domain.SpaceUsers;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SpaceUsersClient {

	private final WebClient client;
	private final HooverSettings settings;
	private final SnapshotClient snapshotClient;

    public SpaceUsersClient(
        WebClient client,
		HooverSettings settings,
		SnapshotClient snapshotClient) {
        this.client = client;
		this.settings = settings;
		this.snapshotClient = snapshotClient;
	}

	public Flux<SpaceUsers> findAll() {
		Flux<Map.Entry<String, String>> butlers = Flux.fromIterable(settings.getButlers().entrySet());
		return
			butlers.flatMap(b -> obtainSpaceUsers(b.getValue())
									.map(su -> SpaceUsers.from(su).foundation(b.getKey()).build()));
	}

	protected Flux<SpaceUsers> obtainSpaceUsers(String baseUrl) {
        String uri = baseUrl + "/snapshot/spaces/users";
        return client
                .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToFlux(SpaceUsers.class)
                    .timeout(settings.getTimeout(), Flux.just(SpaceUsers.builder().build()))
                    .onErrorResume(
                        WebClientResponseException.class,
                        e -> {
                            log.warn("Could not obtain SpaceUsers from %s".formatted(uri), e);
                            return Flux.just(SpaceUsers.builder().build());
                        }
                    );
    }

	public Mono<Set<String>> obtainAccountNames() {
		return snapshotClient.assembleSnapshotDetail()
				.flatMapMany(
					sd ->
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