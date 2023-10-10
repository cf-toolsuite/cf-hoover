package io.pivotal.cfapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.pivotal.cfapp.domain.SnapshotDetail;
import io.pivotal.cfapp.domain.SnapshotSummary;
import io.pivotal.cfapp.client.SnapshotClient;
import reactor.core.publisher.Mono;

@RestController
public class SnapshotController {

	private final SnapshotClient client;

	@Autowired
	public SnapshotController(
		SnapshotClient client) {
		this.client = client;
	}

	@GetMapping("/snapshot/detail")
	public Mono<ResponseEntity<SnapshotDetail>> getDetail() {
		return client
					.assembleSnapshotDetail()
							.map(detail -> ResponseEntity.ok(detail))
							.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/snapshot/summary")
	public Mono<ResponseEntity<SnapshotSummary>> getSummary() {
		return client
					.assembleSnapshotSummary()
					.map(summary -> ResponseEntity.ok(summary))
					.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping(value = { "/snapshot/detail/si" }, produces = MediaType.TEXT_PLAIN_VALUE )
	public Mono<ResponseEntity<String>> getServiceInstanceCsvReport() {
		return client
					.assembleCsvSIReport()
					.map(r -> ResponseEntity.ok(r));
	}

	@GetMapping(value = { "/snapshot/detail/ai" }, produces = MediaType.TEXT_PLAIN_VALUE )
	public Mono<ResponseEntity<String>> getApplicationInstanceCsvReport() {
		return client
					.assembleCsvAIReport()
					.map(r -> ResponseEntity.ok(r));
	}

}
