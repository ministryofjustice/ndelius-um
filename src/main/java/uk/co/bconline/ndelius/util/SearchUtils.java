package uk.co.bconline.ndelius.util;

import lombok.experimental.UtilityClass;
import uk.co.bconline.ndelius.model.SearchResult;

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

	public static boolean isEmailSearch(String query) {
		// If the search query contains an '@' symbol, then assume the user is searching for an email address
		// (this is a very rough heuristic, but seems to work well in practice)
		return query != null && query.contains("@");
	}

	public static boolean resultMatchedOnEmail(String query, SearchResult result) {
		// If the result's email address contains one of the search tokens, then assume it was matched on email
		return result.getEmail() != null && SearchUtils.streamTokens(query).anyMatch(result.getEmail()::contains);
	}
}
