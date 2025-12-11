package org.cftoolsuite.cfapp.controller;

import java.util.List;
import java.util.Map;

import org.cftoolsuite.cfapp.client.SpringApplicationClient;
import org.cftoolsuite.cfapp.domain.JavaAppDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class SpringApplicationController {

    private final SpringApplicationClient client;

    public SpringApplicationController(SpringApplicationClient client) {
        this.client = client;
    }

    @GetMapping("/snapshot/detail/ai/spring")
    public Mono<ResponseEntity<List<JavaAppDetail>>> assembleSpringApplicationDetail() {
        return client.assembleSpringApplicationDetail()
                .map(d -> ResponseEntity.ok(d))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/snapshot/summary/ai/spring")
    public Mono<ResponseEntity<Map<String, Integer>>> calculateSpringApplicationDependencyFrequency() {
        return client.calculateSpringApplicationDependencyFrequency()
                .map(d -> ResponseEntity.ok(d))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}