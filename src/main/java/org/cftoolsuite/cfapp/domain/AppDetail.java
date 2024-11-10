package org.cftoolsuite.cfapp.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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
public class AppDetail {

	private String foundation;
	private String organization;
	private String space;
	private String appId;
	private String appName;
	private String buildpack;
	private String buildpackVersion;
	private String image;
	private String stack;
	@Default
	private Integer runningInstances = 0;
	@Default
	private Integer totalInstances = 0;
	@Default
	private Long memoryUsed = 0L;
	@Default
	private Long memoryQuota = 0L;
	@Default
	private Long diskUsed = 0L;
	@Default
	private Long diskQuota = 0L;
	@Default
	private List<String> urls = new ArrayList<>();
	private LocalDateTime lastPushed;
	private String lastEvent;
	private String lastEventActor;
	private LocalDateTime lastEventTime;
	private String buildpackReleaseType;
    private LocalDateTime buildpackReleaseDate;
    private String buildpackLatestVersion;
    private String buildpackLatestUrl;
	private String requestedState;

	public String toCsv() {
        return String.join(",", wrap(getFoundation()), wrap(getOrganization()), wrap(getSpace()), wrap(getAppId()), wrap(getAppName()),
                wrap(getBuildpack()), wrap(getBuildpackVersion()), wrap(getImage()), wrap(getStack()), wrap(String.valueOf(getRunningInstances())),
                wrap(String.valueOf(getTotalInstances())), wrap(Double.toString(toGigabytes(getMemoryUsed()))), wrap(Double.toString(toGigabytes(getMemoryQuota()))),
                wrap(Double.toString(toGigabytes(getDiskUsed()))), wrap(Double.toString(toGigabytes(getDiskQuota()))),
                (wrap(String.join(",", getUrls() != null ? getUrls(): Collections.emptyList()))),
                wrap(getLastPushed() != null ? getLastPushed().toString() : ""), wrap(getLastEvent()),
                wrap(getLastEventActor()), wrap(getLastEventTime() != null ? getLastEventTime().toString() : ""),
                wrap(getRequestedState()),
                wrap(getBuildpackReleaseType()),
                wrap(getBuildpackReleaseDate() != null ? getBuildpackReleaseDate().toString() : ""),
                wrap(getBuildpackLatestVersion()),
                wrap(getBuildpackLatestUrl()));
    }

	private static String wrap(String value) {
		return value != null ? StringUtils.wrap(value, '"') : StringUtils.wrap("", '"');
	}

	private Double toGigabytes(Long input) {
		return Double.valueOf(input / Math.pow(1024, 3));
	}

	public static String headers() {
		return String.join(",", "foundation", "organization", "space", "application id", "application name", "buildpack", "buildpack version", "image",
                "stack", "running instances", "total instances", "memory used (in gb)", "memory quota (in gb)", "disk used (in gb)", "disk quota (in gb)", "urls", "last pushed", "last event",
                "last event actor", "last event time", "requested state",
                "latest buildpack release type", "latest buildpack release date", "latest buildpack version", "latest buildpack Url" );
	}

	public static AppDetailBuilder from(AppDetail detail) {
        return AppDetail
					.builder()
						.foundation(detail.getFoundation())
						.organization(detail.getOrganization())
						.space(detail.getSpace())
						.appId(detail.getAppId())
						.appName(detail.getAppName())
						.buildpack(detail.getBuildpack())
						.buildpackVersion(detail.getBuildpackVersion())
						.image(detail.getImage())
						.stack(detail.getStack())
						.runningInstances(detail.getRunningInstances())
						.totalInstances(detail.getTotalInstances())
						.memoryUsed(detail.getMemoryUsed())
						.memoryQuota(detail.getMemoryQuota())
						.diskUsed(detail.getDiskUsed())
						.diskQuota(detail.getDiskQuota())
						.urls(detail.getUrls())
						.lastPushed(detail.getLastPushed())
						.lastEvent(detail.getLastEvent())
						.lastEventActor(detail.getLastEventActor())
						.lastEventTime(detail.getLastEventTime())
						.requestedState(detail.getRequestedState())
						.buildpackReleaseType(detail.getBuildpackReleaseType())
						.buildpackReleaseDate(detail.getBuildpackReleaseDate())
						.buildpackLatestVersion(detail.getBuildpackLatestVersion())
						.buildpackLatestUrl(detail.getBuildpackLatestUrl());
	}
}
