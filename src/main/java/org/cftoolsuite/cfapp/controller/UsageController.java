package org.cftoolsuite.cfapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.cftoolsuite.cfapp.domain.accounting.application.AppUsageReport;
import org.cftoolsuite.cfapp.domain.accounting.service.ServiceUsageReport;
import org.cftoolsuite.cfapp.domain.accounting.task.TaskUsageReport;
import org.cftoolsuite.cfapp.client.UsageClient;
import reactor.core.publisher.Mono;

@RestController
public class UsageController {

    private final UsageClient client;

    @Autowired
    public UsageController(UsageClient client) {
        this.client = client;
    }

    @GetMapping(value = "/accounting/tasks")
    public Mono<ResponseEntity<TaskUsageReport>> getTaskReport() {
        return client
                .getTaskReport()
                .map(r -> ResponseEntity.ok(r))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/accounting/applications")
    public Mono<ResponseEntity<AppUsageReport>> getApplicationReport() {
        return client
                .getApplicationReport()
                .map(r -> ResponseEntity.ok(r))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/accounting/services")
    public Mono<ResponseEntity<ServiceUsageReport>> getServiceReport() {
        return client
                .getServiceReport()
                .map(r -> ResponseEntity.ok(r))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}