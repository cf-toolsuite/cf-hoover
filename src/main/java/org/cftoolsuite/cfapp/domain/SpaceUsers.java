package org.cftoolsuite.cfapp.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
@JsonPropertyOrder({"foundation", "organization", "space", "auditors", "developers", "managers", "users", "user-count"})
public class SpaceUsers {

	@JsonProperty("foundation")
	private String foundation;

	@JsonProperty("organization")
	private String organization;

	@JsonProperty("space")
	private String space;

	@Default
	@JsonProperty("auditors")
	private List<String> auditors = new ArrayList<>();

	@Default
	@JsonProperty("developers")
	private List<String> developers = new ArrayList<>();

	@Default
	@JsonProperty("managers")
	private List<String> managers = new ArrayList<>();

	@Default
	@JsonProperty("users")
	private Set<String> users = new HashSet<>();

	@Default
	@JsonProperty("user-count")
	private Integer userCount = 0;

	@JsonCreator
	public SpaceUsers(
		@JsonProperty("foundation") String foundation,
		@JsonProperty("organization") String organization,
		@JsonProperty("space") String space,
		@JsonProperty("auditors") List<String> auditors,
		@JsonProperty("developers") List<String> developers,
		@JsonProperty("managers") List<String> managers,
		@JsonProperty("users") Set<String> users,
		@JsonProperty("user-count") Integer userCount
	) {
		this.foundation = foundation;
		this.organization = organization;
		this.space = space;
		this.auditors = auditors;
		this.developers = developers;
		this.managers = managers;
		this.users = users;
		this.userCount = userCount;
	}

	public static SpaceUsersBuilder from(SpaceUsers users) {
		return SpaceUsers
				.builder()
					.organization(users.getOrganization())
					.space(users.getSpace())
					.foundation(users.getFoundation())
					.auditors(users.getAuditors())
					.developers(users.getDevelopers())
					.managers(users.getManagers())
					.users(users.getUsers())
					.userCount(users.getUserCount());
	}
}
