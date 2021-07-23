package uk.co.bconline.ndelius.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

@UtilityClass
public class SearchUtils {

	public static String[] tokenize(String query) {
		if (query == null) return new String[]{};
		return query.toLowerCase(Locale.ROOT).trim().split("\\s+");
	}

	public static Stream<String> streamTokens(String query) {
		return Arrays.stream(tokenize(query));
	}
}
