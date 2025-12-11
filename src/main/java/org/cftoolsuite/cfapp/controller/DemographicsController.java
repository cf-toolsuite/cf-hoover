package org.cftoolsuite.cfapp.controller;

import org.cftoolsuite.cfapp.client.DemographicsClient;
import org.cftoolsuite.cfapp.domain.Demographics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class DemographicsController {

    private final DemographicsClient client;

    public DemographicsController(DemographicsClient client) {
        this.client = client;
    }

    @GetMapping("/snapshot/demographics")
    public Mono<ResponseEntity<Demographics>> aggregateDemographics() {
        return client.aggregateDemographics()
                .map(d -> ResponseEntity.ok(d))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}