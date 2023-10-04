package io.pivotal.cfapp.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@JsonPropertyOrder({ "application-counts", "service-instance-counts" })
public class SnapshotSummary {

    @Default
    @JsonProperty("application-counts")
    private ApplicationCounts applicationCounts = ApplicationCounts.builder().build();

    @Default
    @JsonProperty("service-instance-counts")
    private ServiceInstanceCounts serviceInstanceCounts = ServiceInstanceCounts.builder().build();

    @JsonCreator
    public SnapshotSummary(
        @JsonProperty("application-counts") ApplicationCounts applicationCounts,
        @JsonProperty("service-instance-counts") ServiceInstanceCounts serviceInstanceCounts) {
        this.applicationCounts = applicationCounts;
        this.serviceInstanceCounts = serviceInstanceCounts;
    }

}