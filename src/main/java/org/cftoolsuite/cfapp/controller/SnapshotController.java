package org.cftoolsuite.cfapp.controller;

import org.cftoolsuite.cfapp.client.SnapshotClient;
import org.cftoolsuite.cfapp.domain.SnapshotDetail;
import org.cftoolsuite.cfapp.domain.SnapshotSummary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class SnapshotController {

	private final SnapshotClient client;

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
