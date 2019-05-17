package io.pivotal.cfapp.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

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
@JsonPropertyOrder({ "by-organization", "total-user-accounts", "total-service-accounts"})
public class UserCounts {

    @Default
    @JsonProperty("by-organization")
    private Map<String, Integer> byOrganization = new HashMap<>();

    @Default
    @JsonProperty("total-user-accounts")
    private Long totalUserAccounts = 0L;

    @Default
    @JsonProperty("total-service-accounts")
    private Long totalServiceAccounts = 0L;

    @JsonCreator
    public UserCounts(
        @JsonProperty("by-organization") Map<String, Integer> byOrganization,
        @JsonProperty("total-user-accounts") Long totalUserAccounts,
        @JsonProperty("total-service-accounts") Long totalServiceAccounts
    ) {
        this.byOrganization = byOrganization;
        this.totalUserAccounts = totalUserAccounts;
        this.totalServiceAccounts = totalServiceAccounts;
    }

    public static UserCounts aggregate(List<UserCounts> counts) {
        Map<String, Integer> byOrganization = merge(counts.stream().map(c -> c.getByOrganization().entrySet()));
        Long totalUserAccounts = counts.stream().mapToLong(c -> c.getTotalUserAccounts()).sum();
        Long totalServiceAccounts = counts.stream().mapToLong(c -> c.getTotalServiceAccounts()).sum();
        return UserCounts.builder()
                .byOrganization(byOrganization)
                .totalServiceAccounts(totalServiceAccounts)
                .totalUserAccounts(totalUserAccounts)
                .build();
    }

    private static Map<String, Integer> merge(Stream<Set<Entry<String, Integer>>> source) {
        Map<String, Integer> target = new HashMap<>();
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
