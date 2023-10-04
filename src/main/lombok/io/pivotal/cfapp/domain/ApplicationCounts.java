package io.pivotal.cfapp.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
@JsonPropertyOrder({ "by-buildpack", "by-stack", "by-dockerimage", "by-status",
"total-applications", "total-running-application-instances", "total-stopped-application-instances", "total-crashed-application-instances",
"total-application-instances", "total-memory-used-in-mb", "total-disk-used-in-mb", "velocity"})
public class ApplicationCounts {

    @Default
    @JsonProperty("by-buildpack")
    private Map<String, Long> byBuildpack = new HashMap<>();

    @Default
    @JsonProperty("by-stack")
    private Map<String, Long> byStack = new HashMap<>();

    @Default
    @JsonProperty("by-dockerimage")
    private Map<String, Long> byDockerImage = new HashMap<>();

    @Default
    @JsonProperty("by-status")
    private Map<String, Long> byStatus = new HashMap<>();

    @Default
    @JsonProperty("total-applications")
    private Long totalApplications = 0L;

    @Default
    @JsonProperty("total-running-application-instances")
    private Long totalRunningApplicationInstances = 0L;

    @Default
    @JsonProperty("total-stopped-application-instances")
    private Long totalStoppedApplicationInstances = 0L;

    @Default
    @JsonProperty("total-crashed-application-instances")
    private Long totalCrashedApplicationInstances = 0L;

    @Default
    @JsonProperty("total-application-instances")
    private Long totalApplicationInstances = 0L;

    @Default
    @JsonProperty("total-memory-used-in-gb")
    private Double totalMemoryUsed = 0.0;

    @Default
    @JsonProperty("total-disk-used-in-gb")
    private Double totalDiskUsed = 0.0;

    @Default
    @JsonProperty("velocity")
    private Map<String, Long> velocity = new HashMap<>();

    public static ApplicationCounts aggregate(List<ApplicationCounts> counts) {
        Map<String, Long> byBuildpack = merge(counts.stream().map(c -> c.getByBuildpack().entrySet()));
        Map<String, Long> byStack = merge(counts.stream().map(c -> c.getByStack().entrySet()));
        Map<String, Long> byDockerImage = merge(counts.stream().map(c -> c.getByDockerImage().entrySet()));
        Map<String, Long> byStatus = merge(counts.stream().map(c -> c.getByStatus().entrySet()));
        Long totalApplications = counts.stream().mapToLong(c -> c.getTotalApplications()).sum();
        Long totalRunningApplicationInstances = counts.stream().mapToLong(c -> c.getTotalRunningApplicationInstances()).sum();
        Long totalStoppedApplicationInstances = counts.stream().mapToLong(c -> c.getTotalStoppedApplicationInstances()).sum();
        Long totalCrashedApplicationInstances = counts.stream().mapToLong(c -> c.getTotalCrashedApplicationInstances()).sum();
        Long totalApplicationInstances = counts.stream().mapToLong(c -> c.getTotalApplicationInstances()).sum();
        Double totalMemoryUsed = counts.stream().mapToDouble(c -> c.getTotalMemoryUsed()).sum();
        Double totalDiskUsed = counts.stream().mapToDouble(c -> c.getTotalDiskUsed()).sum();
        Map<String, Long> velocity = merge(counts.stream().map(c -> c.getVelocity().entrySet()));
        return ApplicationCounts
                .builder()
                    .byBuildpack(byBuildpack)
                    .byStack(byStack)
                    .byDockerImage(byDockerImage)
                    .byStatus(byStatus)
                    .totalApplications(totalApplications)
                    .totalRunningApplicationInstances(totalRunningApplicationInstances)
                    .totalStoppedApplicationInstances(totalStoppedApplicationInstances)
                    .totalCrashedApplicationInstances(totalCrashedApplicationInstances)
                    .totalApplicationInstances(totalApplicationInstances)
                    .totalMemoryUsed(totalMemoryUsed)
                    .totalDiskUsed(totalDiskUsed)
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
