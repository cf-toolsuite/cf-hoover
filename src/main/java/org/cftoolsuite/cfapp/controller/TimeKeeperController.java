package org.cftoolsuite.cfapp.controller;

import org.cftoolsuite.cfapp.client.TimeKeeperClient;
import org.cftoolsuite.cfapp.domain.TimeKeepers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class TimeKeeperController {

	private final TimeKeeperClient client;

	public TimeKeeperController(
		TimeKeeperClient client) {
		this.client = client;
	}

	@GetMapping("/collect")
	public Mono<ResponseEntity<TimeKeepers>> getTimeKeepers() {
		return client
					.assembleTimeKeepers()
						.map(tk -> ResponseEntity.ok(tk))
						.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

}
