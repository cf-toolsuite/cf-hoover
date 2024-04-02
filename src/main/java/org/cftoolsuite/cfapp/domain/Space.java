package org.cftoolsuite.cfapp.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@JsonPropertyOrder({ "foundation", "organization-id", "organization-name", "space-id", "space-name" })
@EqualsAndHashCode
@ToString
public class Space {

    @JsonProperty("foundation")
    private String foundation;

    @JsonProperty("organization-id")
    private final String organizationId;

    @JsonProperty("organization-name")
    private final String organizationName;

    @JsonProperty("space-id")
    private final String spaceId;

    @JsonProperty("space-name")
    private final String spaceName;

    @JsonCreator
    Space(
            @JsonProperty("foundation") String foundation,
            @JsonProperty("organization-id") String organizationId,
            @JsonProperty("organization-name") String organizationName,
            @JsonProperty("space-id") String spaceId,
            @JsonProperty("space-name") String spaceName) {
        this.foundation = foundation;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.spaceId = spaceId;
        this.spaceName = spaceName;
    }

    public static SpaceBuilder from(Space space) {
        return Space
                    .builder()
                        .foundation(space.getFoundation())
                        .organizationId(space.getOrganizationId())
                        .organizationName(space.getOrganizationName())
                        .spaceId(space.getSpaceId())
                        .spaceName(space.getSpaceName());
    }
}
