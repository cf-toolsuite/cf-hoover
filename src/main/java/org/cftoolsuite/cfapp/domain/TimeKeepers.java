package org.cftoolsuite.cfapp.domain;

import java.util.HashSet;
import java.util.Set;

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
@JsonPropertyOrder({ "time-keepers" })
public class TimeKeepers {

    @Default
    @JsonProperty("time-keepers")
    private Set<TimeKeeper> timeKeepers = new HashSet<>();

    @JsonCreator
    public TimeKeepers(
        @JsonProperty("time-keepers") Set<TimeKeeper> timeKeepers) {
        this.timeKeepers = timeKeepers;
    }

}