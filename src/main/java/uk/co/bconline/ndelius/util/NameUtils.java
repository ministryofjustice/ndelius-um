package uk.co.bconline.ndelius.util;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.springframework.util.StringUtils.isEmpty;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NameUtils
{
	public static String combineForenames(String forename, String forename2)
	{
		if (forename == null) forename = "";
		if (isEmpty(forename2)) return forename;
		return forename + " " + forename2;
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

}
