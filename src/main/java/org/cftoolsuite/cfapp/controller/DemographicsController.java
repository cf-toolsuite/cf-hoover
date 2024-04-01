package org.cftoolsuite.cfapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.cftoolsuite.cfapp.domain.Demographics;
import org.cftoolsuite.cfapp.client.DemographicsClient;
import reactor.core.publisher.Mono;

@RestController
public class DemographicsController {

    private final DemographicsClient client;

    @Autowired
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