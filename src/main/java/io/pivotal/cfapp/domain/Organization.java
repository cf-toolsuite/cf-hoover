package io.pivotal.cfapp.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@JsonPropertyOrder({ "foundation", "id", "name"})
@ToString
public class Organization {

    @JsonProperty("foundation")
    private String foundation;

    @JsonProperty("id")
    private final String id;

    @JsonProperty("name")
    private final String name;

    @JsonCreator
    public Organization(
            @JsonProperty("foundation") String foundation,
            @JsonProperty("id") String id,
            @JsonProperty("name") String name) {
        this.foundation = foundation;
        this.id = id;
        this.name = name;
    }

    public static OrganizationBuilder from(Organization organization) {
        return Organization
					.builder()
						.foundation(organization.getFoundation())
                        .id(organization.getId())
						.name(organization.getName());
	}

}
