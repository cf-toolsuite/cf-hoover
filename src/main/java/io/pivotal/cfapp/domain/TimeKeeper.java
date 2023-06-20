package io.pivotal.cfapp.domain;

import java.time.LocalDateTime;

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
public class TimeKeeper {

	private String foundation;
	private LocalDateTime collectionDateTime;

	public String toCsv() {
		return String.join(",", wrap(getFoundation()), wrap(getCollectionDateTime() != null ? getCollectionDateTime().toString() : ""));
	}

	private static String wrap(String value) {
		return value != null ? StringUtils.wrap(value, '"') : StringUtils.wrap("", '"');
	}

	public static String headers() {
		return String.join(",", "foundation", "collection date/time");
	}

}
