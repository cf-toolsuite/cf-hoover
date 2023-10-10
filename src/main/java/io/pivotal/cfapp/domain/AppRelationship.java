package io.pivotal.cfapp.domain;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor(access=AccessLevel.PACKAGE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode
@ToString
public class AppRelationship {

	private String foundation;
	private String organization;
	private String space;
	private String appId;
	private String appName;
	private String serviceInstanceId;
	private String serviceName;
	private String servicePlan;
	private String serviceType;

	public String toCsv() {
		return String.join(",", wrap(getFoundation()), wrap(getOrganization()), wrap(getSpace()), wrap(getAppId()), wrap(getAppName()),
				wrap(getServiceInstanceId()), wrap(getServiceName()), wrap(getServicePlan()), wrap(getServiceType()));
	}

	private static String wrap(String value) {
		return value != null ? StringUtils.wrap(value, '"') : StringUtils.wrap("", '"');
	}

	public static String headers() {
        return String.join(",", "foundation", "organization", "space", "application id",
                "application name", "service instance id", "service name", "service plan", "service type");
	}

	public static AppRelationshipBuilder from(AppRelationship rel) {
		return AppRelationship
				.builder()
					.foundation(rel.getFoundation())
					.organization(rel.getOrganization())
					.space(rel.getSpace())
					.appId(rel.getAppId())
					.appName(rel.getAppName())
					.serviceInstanceId(rel.getServiceInstanceId())
					.serviceName(rel.getServiceName())
					.servicePlan(rel.getServicePlan())
					.serviceType(rel.getServiceType());
	}

}
