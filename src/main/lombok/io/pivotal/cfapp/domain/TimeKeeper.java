package io.pivotal.cfapp.domain;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode
@ToString
@JsonPropertyOrder({ "foundation", "collection-date-time" })
public class TimeKeeper {

	@JsonProperty("foundation")
	private String foundation;

	@JsonProperty("collection-date-time")
	private LocalDateTime collectionDateTime;

    @JsonCreator
    TimeKeeper(@JsonProperty("foundation") String foundation,
        @JsonProperty("collection-date-time") LocalDateTime collectionDateTime) {
        this.foundation = foundation;
        this.collectionDateTime = collectionDateTime;
    }

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
