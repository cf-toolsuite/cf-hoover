package org.cftoolsuite.cfapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.cftoolsuite.cfapp.client.OrganizationsClient;
import org.cftoolsuite.cfapp.domain.Organization;
import reactor.core.publisher.Mono;

@RestController
public class OrganizationsController {

    private final OrganizationsClient client;

    @Autowired
    public OrganizationsController(OrganizationsClient client) {
        this.client = client;
    }

    @GetMapping("/snapshot/organizations")
    public Mono<ResponseEntity<List<Organization>>> assembleOrganizations() {
        return client.assembleOrganizations()
                .map(d -> ResponseEntity.ok(d))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}