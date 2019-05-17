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
import io.pivotal.cfapp.service.SpaceUsersService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class SpaceUsersController {

	private final SpaceUsersService service;

	@Autowired
	public SpaceUsersController(
		SpaceUsersService service) {
		this.service = service;
	}

	@GetMapping("/space-users")
	public Mono<ResponseEntity<List<SpaceUsers>>> getAllSpaceUsers() {
		return service
					.findAll()
						.collectList()
							.map(users -> ResponseEntity.ok(users))
							.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/space-users/{foundation}/{organization}/{space}")
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
					.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/users/count")
	public Mono<Integer> totalAccounts() {
		return service.totalAccounts();
	}

	@GetMapping("/users")
	public Mono<Set<String>> getAllAccountNames() {
		return service.obtainAccountNames();
	}
}
