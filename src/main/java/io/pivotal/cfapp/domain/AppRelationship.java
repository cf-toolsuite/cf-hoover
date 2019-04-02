package io.pivotal.cfapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor(access=AccessLevel.PACKAGE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@Getter
public class AppRelationship {

	private String foundation;
	private String organization;
	private String space;
	private String appId;
	private String appName;
	private String serviceId;
	private String serviceName;
	private String servicePlan;
	private String serviceType;

	public String toCsv() {
		return String.join(",", wrap(getFoundation()), wrap(getOrganization()), wrap(getSpace()), wrap(getAppId()), wrap(getAppName()),
				wrap(getServiceId()), wrap(getServiceName()), wrap(getServicePlan()), wrap(getServiceType()));
	}

	private String wrap(String value) {
		return value != null ? StringUtils.wrap(value, '"') : StringUtils.wrap("", '"');
	}

	public static String headers() {
        return String.join(",", "foundation", "organization", "space", "application id",
                "application name", "service id", "service name", "service plan", "service type");
    }

	public static AppRelationshipBuilder from(AppRelationship rel) {
		return AppRelationship
				.builder()
					.appId(rel.getAppId())
					.appName(rel.getAppName())
					.foundation(rel.getFoundation())
					.organization(rel.getOrganization())
					.space(rel.getSpace())
					.serviceId(rel.getServiceId())
					.serviceName(rel.getServiceName())
					.servicePlan(rel.getServicePlan())
					.serviceType(rel.getServiceType());
	}
}
