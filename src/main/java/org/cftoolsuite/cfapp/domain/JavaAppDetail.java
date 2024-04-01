package org.cftoolsuite.cfapp.domain;

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
public class JavaAppDetail {

    private String foundation;
    private String organization;
    private String space;
    private String appId;
    private String appName;
    private String dropletId;
    private String pomContents;
    private String springDependencies;

    public static JavaAppDetailBuilder from(JavaAppDetail detail) {
        return JavaAppDetail
                .builder()
                .foundation(detail.getFoundation())
                .organization(detail.getOrganization())
                .space(detail.getSpace())
                .appId(detail.getAppId())
                .appName(detail.getAppName())
                .dropletId(detail.getDropletId())
                .pomContents(detail.getPomContents())
                .springDependencies(detail.getSpringDependencies());
    }

    public static String headers() {
        return String.join(",", "foundation", "organization", "space", "application id", "application name", "droplet id", "pom contents", "spring dependencies" );
    }

    private static String wrap(String value) {
        return value != null ? StringUtils.wrap(value, '"') : StringUtils.wrap("", '"');
    }

    public String toCsv() {
        return String.join(",", wrap(getFoundation()), wrap(getOrganization()), wrap(getSpace()), wrap(getAppId()), wrap(getAppName()),
                wrap(getDropletId()), wrap(getPomContents()), wrap(getSpringDependencies()));
    }

}
