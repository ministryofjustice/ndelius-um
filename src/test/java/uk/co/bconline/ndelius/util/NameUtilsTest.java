package uk.co.bconline.ndelius.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NameUtilsTest
{

	@Test
	public void combineForenames()
	{
		String result = NameUtils.combineNames("a b", "c d");
		assertEquals("a b c d", result);
	}

	@Test
	public void combineForenamesWithNoSecondForename()
	{
		String result = NameUtils.combineNames("a b", null);
		assertEquals("a b", result);
	}

	@Test
	public void combineForenamesWithNoFirstForename()
	{
		String result = NameUtils.combineNames(null, "a");
		assertEquals(" a", result);
		assertEquals("", NameUtils.firstForename(result));
		assertEquals("a", NameUtils.subsequentForenames(result));
	}

	@Test
	public void combineLotsOfNames()
	{
		String result = NameUtils.combineNames("a b", "c d", "e f", "g h", "i j");
		assertEquals("a b c d e f g h i j", result);
	}

	@Test
	public void combineLotsOfNamesWithNulls()
	{
		String result = NameUtils.combineNames(null, "c d", "e f", null, "i j");
		assertEquals(" c d e f i j", result);
	}

	@Test
	public void firstForename()
	{
		String result = NameUtils.firstForename("a b c d");
		assertEquals("a", result);
	}

	@Test
	public void firstForenameWithNoSubsequentForename()
	{
		String result = NameUtils.firstForename("a");
		assertEquals("a", result);
	}

	@Test
	public void firstForenameWhenNull()
	{
		String result = NameUtils.firstForename(null);
		assertEquals("", result);
	}

	@Test
	public void subsequentForenames()
	{
		String result = NameUtils.subsequentForenames("a b c d");
		assertEquals("b c d", result);
	}

	@Test
	public void noSubsequentForeneames()
	{
		String result = NameUtils.subsequentForenames("a");
		assertEquals("", result);
	}

	@Test
	public void subsequentForenamesWhenNull()
	{
		String result = NameUtils.subsequentForenames(null);
		assertEquals("", result);
	}

}