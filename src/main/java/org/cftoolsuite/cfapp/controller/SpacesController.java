package org.cftoolsuite.cfapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.cftoolsuite.cfapp.client.SpacesClient;
import org.cftoolsuite.cfapp.domain.Space;
import reactor.core.publisher.Mono;

@RestController
public class SpacesController {

    private final SpacesClient client;

    @Autowired
    public SpacesController(SpacesClient client) {
        this.client = client;
    }

    @GetMapping("/snapshot/spaces")
    public Mono<ResponseEntity<List<Space>>> assembleSpaces() {
        return client.assembleSpaces()
                .map(d -> ResponseEntity.ok(d))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}