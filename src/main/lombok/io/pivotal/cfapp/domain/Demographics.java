package io.pivotal.cfapp.domain;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
@JsonPropertyOrder({ "demographics", "total-foundations", "total-user-accounts", "total-service-accounts" })
public class Demographics {

    @Default
    @JsonProperty("demographics")
    private Set<Demographic> demographics = new HashSet();

    @Default
    @JsonProperty("total-foundations")
    private Integer foundations = 0;

    @Default
    @JsonProperty("total-user-accounts")
    private Long userAccounts = 0L;

    @Default
    @JsonProperty("total-service-accounts")
    private Long serviceAccounts = 0L;

    @JsonCreator
    public Demographics(
        @JsonProperty("demographics") Set<Demographic> demographics,
        @JsonProperty("total-foundations") Integer foundations,
        @JsonProperty("total-user-accounts") Long userAccounts,
        @JsonProperty("total-service-accounts") Long serviceAccounts
    ) {
        this.demographics = demographics;
        this.foundations = foundations;
        this.userAccounts = userAccounts;
        this.serviceAccounts = serviceAccounts;
    }

}