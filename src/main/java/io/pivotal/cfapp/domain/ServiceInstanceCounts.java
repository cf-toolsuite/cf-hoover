package io.pivotal.cfapp.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Builder.Default;

@Builder
@AllArgsConstructor(access=AccessLevel.PACKAGE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@Getter
@ToString
@JsonPropertyOrder({ "by-service", "by-service-and-plan", "total-service-instances", "velocity" })
public class ServiceInstanceCounts {

    @Default
    @JsonProperty("by-service")
    private Map<String,Long> byService = new HashMap<>();

    @Default
    @JsonProperty("by-service-and-plan")
    private Map<String, Long> byServiceAndPlan = new HashMap<>();

    @Default
    @JsonProperty("total-service-instances")
    private Long totalServiceInstances = 0L;

    @Default
    @JsonProperty("velocity")
    private Map<String,Long> velocity = new HashMap<>();

    public static ServiceInstanceCounts aggregate(List<ServiceInstanceCounts> counts) {
        Map<String, Long> byService = merge(counts.stream().map(c -> c.getByService().entrySet()));
        Map<String, Long> byServiceAndPlan = merge(counts.stream().map(c -> c.getByServiceAndPlan().entrySet()));
        Long totalServiceInstances = counts.stream().mapToLong(c -> c.getTotalServiceInstances()).sum();
        Map<String, Long> velocity = merge(counts.stream().map(c -> c.getVelocity().entrySet()));
        return ServiceInstanceCounts
                .builder()
                    .byService(byService)
                    .byServiceAndPlan(byServiceAndPlan)
                    .totalServiceInstances(totalServiceInstances)
                    .velocity(velocity)
                    .build();
    }

    private static Map<String, Long> merge(Stream<Set<Entry<String, Long>>> source) {
        Map<String, Long> target = new HashMap<>();
        source.forEach(oe -> oe.forEach(ie -> {
            if (target.keySet().contains(ie.getKey())) {
                target.put(ie.getKey(), ie.getValue() + target.get(ie.getKey()));
            } else {
                target.put(ie.getKey(), ie.getValue());
            }
        }));
        return target;
    }
}
