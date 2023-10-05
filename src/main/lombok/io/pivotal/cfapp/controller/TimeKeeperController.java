package io.pivotal.cfapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.pivotal.cfapp.client.TimeKeeperClient;
import io.pivotal.cfapp.domain.TimeKeepers;
import reactor.core.publisher.Mono;

@RestController
public class TimeKeeperController {

	private final TimeKeeperClient client;

	@Autowired
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
