package io.pivotal.cfapp.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor(access=AccessLevel.PACKAGE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@ToString
@JsonPropertyOrder({"foundation", "organization", "space", "auditors", "developers", "managers", "users", "user-count"})
public class SpaceUsers {

	private String foundation;
	private String organization;
	private String space;
	private List<String> auditors;
	private List<String> developers;
	private List<String> managers;

	@JsonProperty("users")
	public Set<String> getUsers() {
		Set<String> users = new HashSet<>();
		users.addAll(auditors);
		users.addAll(developers);
		users.addAll(managers);
		return users;
	}

	@JsonProperty("user-count")
	public Integer getUserCount() {
		return getUsers().size();
	}

	public static SpaceUsersBuilder from(SpaceUsers users) {
		return SpaceUsers
				.builder()
					.foundation(users.getFoundation())
					.auditors(users.getAuditors())
					.developers(users.getDevelopers())
					.managers(users.getManagers())
					.organization(users.getOrganization())
					.space(users.getSpace());
	}
}
