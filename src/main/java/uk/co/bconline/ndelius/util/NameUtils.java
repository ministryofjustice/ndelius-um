package uk.co.bconline.ndelius.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.springframework.util.StringUtils.isEmpty;

@UtilityClass
public class NameUtils
{
	public static String combineNames(String name1, String name2)
	{
		if (name1 == null) name1 = "";
		if (isEmpty(name2)) return name1;
		return name1 + " " + name2;
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
