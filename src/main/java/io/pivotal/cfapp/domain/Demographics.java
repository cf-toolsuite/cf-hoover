package io.pivotal.cfapp.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.Builder.Default;

@Builder
@Getter
@JsonPropertyOrder({ "total-organizations", "total-spaces", "total-user-accounts", "total-service-accounts" })
public class Demographics {

    @Default
    @JsonProperty("total-organizations")
    private Long organizations = 0L;

    @Default
    @JsonProperty("total-spaces")
    private Long spaces = 0L;

    @Default
    @JsonProperty("total-user-accounts")
    private Long userAccounts = 0L;

    @Default
    @JsonProperty("total-service-accounts")
    private Long serviceAccounts = 0L;

    @JsonCreator
    public Demographics(
        @JsonProperty("total-organizations") Long organizations,
        @JsonProperty("total-spaces") Long spaces,
        @JsonProperty("total-user-accounts") Long userAccounts,
        @JsonProperty("total-service-accounts") Long serviceAccounts
    ) {
        this.organizations = organizations;
        this.spaces = spaces;
        this.userAccounts = userAccounts;
        this.serviceAccounts = serviceAccounts;
    }

    public static Demographics aggregate(List<Demographics> counts) {
        Long organizations = counts.stream().mapToLong(c -> c.getOrganizations()).sum();
        Long spaces = counts.stream().mapToLong(c -> c.getSpaces()).sum();
        Long userAccounts = counts.stream().mapToLong(c -> c.getUserAccounts()).sum();
        Long serviceAccounts = counts.stream().mapToLong(c -> c.getServiceAccounts()).sum();
        return Demographics
                .builder()
                    .organizations(organizations)
                    .spaces(spaces)
                    .userAccounts(userAccounts)
                    .serviceAccounts(serviceAccounts)
                    .build();
    }
}