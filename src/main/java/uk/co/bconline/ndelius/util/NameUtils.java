package uk.co.bconline.ndelius.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.util.StringUtils;

import java.util.stream.Stream;

import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.springframework.util.StringUtils.isEmpty;
import static org.springframework.util.StringUtils.trimTrailingWhitespace;

@UtilityClass
public class NameUtils
{
	public static String combineNames(String... names)
	{
		if (names == null || names.length == 0) return "";
		if (names[0] == null) names[0] = "";
		val firstName = names[0];
		val otherNames = join(" ", copyOfRange(names, 1, names.length));
		return trimTrailingWhitespace(firstName + " " + otherNames);
	}

	public static String firstForename(String forenames)
	{
		if (isEmpty(forenames)) return "";
		return forenames.split(" ")[0];
	}

	public static String subsequentForenames(String forenames)
	{
		if (isEmpty(forenames)) return "";
		return stream(forenames.split(" ")).skip(1).collect(joining(" "));
	}

	public static String join(String delimiter, String... strings)
	{
		return Stream.of(strings)
				.filter(str -> !StringUtils.isEmpty(str))
				.collect(joining(delimiter));
	}

	public static String camelCaseToTitleCase(String s) {
		return StringUtils.capitalize(s.replaceAll(String.format("%s|%s|%s",
				"(?<=[A-Z])(?=[A-Z][a-z])",
				"(?<=[^A-Z])(?=[A-Z])",
				"(?<=[A-Za-z])(?=[^A-Za-z])"), " "));
	}
}
