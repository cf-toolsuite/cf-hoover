package io.pivotal.cfapp.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.pivotal.cfapp.domain.SpaceUsers;
import io.pivotal.cfapp.client.SpaceUsersClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class SpaceUsersController {

	private final SpaceUsersClient client;

	@Autowired
	public SpaceUsersController(
		SpaceUsersClient client) {
		this.client = client;
	}

	@GetMapping("/snapshot/spaces/users")
	public Mono<ResponseEntity<List<SpaceUsers>>> getAllSpaceUsers() {
		return client
					.findAll()
						.collectList()
							.map(users -> ResponseEntity.ok(users))
							.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/snapshot/{foundation}/{organization}/{space}/users")
	public Mono<ResponseEntity<SpaceUsers>> getUsersInOrganizationAndSpace(
		@PathVariable("foundation") String foundation,
		@PathVariable("organization") String organization,
		@PathVariable("space") String space) {
		return getAllSpaceUsers()
					.flatMapMany(r -> Flux.fromIterable(r.getBody()))
					.filter(su ->
						su.getFoundation().equals(foundation) &&
						su.getOrganization().equals(organization) &&
						su.getSpace().equals(space))
					.singleOrEmpty()
					.map(users -> ResponseEntity.ok(users))
					.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@GetMapping("/snapshot/users/count")
	public Mono<ResponseEntity<Long>> totalAccounts() {
		return client
				.totalAccounts()
				.map(t -> ResponseEntity.ok(t))
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}

	@GetMapping("/snapshot/users")
	public Mono<ResponseEntity<Set<String>>> getAllAccountNames() {
		return client
				.obtainAccountNames()
				.map(r -> ResponseEntity.ok(r))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
}
