package io.pivotal.cfapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.pivotal.cfapp.client.SpringApplicationDetailClient;
import io.pivotal.cfapp.domain.JavaAppDetail;
import reactor.core.publisher.Mono;

@RestController
public class SpringApplicationDetailController {

    private final SpringApplicationDetailClient client;

    @Autowired
    public SpringApplicationDetailController(SpringApplicationDetailClient client) {
        this.client = client;
    }

    @GetMapping("/snapshot/detail/ai/spring")
    public Mono<ResponseEntity<List<JavaAppDetail>>> assembleSpringApplicationDetail() {
        return client.assembleSpringApplicationDetail()
                .map(d -> ResponseEntity.ok(d))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}