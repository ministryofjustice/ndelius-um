package uk.co.bconline.ndelius.service.impl;

import com.google.common.collect.ImmutableMap;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.*;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserServiceImplTest
{
	@Autowired
	private UserServiceImpl service;

	@Before
	public void user() {
		SecurityContextHolder.getContext()
				.setAuthentication(new TestingAuthenticationToken("test.user", "secret"));
	}

	@Test
	public void csvHasCorrectHeader() throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		service.exportSearchToCSV("test.user",ImmutableMap.of("NDMIS-Reporting", Set.of(), "Fileshare", Set.of()), Sets.newHashSet(),false,printWriter);
		String s = stringWriter.toString();
		String expectedHeader = "\"Username\",\"Forenames\",\"Surname\",\"Team(s)\",\"StaffCode\",\"Sources\",\"EndDate\",\"Email\"";
		assertEquals(s.split("\\n")[0], expectedHeader);
	}

	@Test
	public void csvHasTestData() throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		service.exportSearchToCSV("test.user",ImmutableMap.of("NDMIS-Reporting", Set.of(), "Fileshare", Set.of()), Sets.newHashSet(),false,printWriter);
		String s = stringWriter.toString();
		String expectedTestUser = "\"test.user.gdpr\",\"Test\",\"User\",\"\",\"\",\"LDAP\",\"\",\"test.user.gdpr@test.com\"";
		assertTrue(Arrays.asList(s.split("\\n")).contains(expectedTestUser));
	}

	@Test
	public void csvHasInActiveUsersTestData() throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		service.exportSearchToCSV("test.user.inactive",ImmutableMap.of("NDMIS-Reporting", Set.of(), "Fileshare", Set.of()), Sets.newHashSet(),true,printWriter);
		String s = stringWriter.toString();
		System.out.println(stringWriter.toString());
		String expectedTestUser = "\"test.user.inactive\",\"Inactive\",\"User\",\"\",\"\",\"LDAP\",\"2000-01-01\",\"\"";
		assertTrue(Arrays.asList(s.split("\\n")).contains(expectedTestUser));
	}

	@Test
	public void csvEmptyWithNoSearchResults() throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		service.exportSearchToCSV("noResults",ImmutableMap.of("NDMIS-Reporting", Set.of(), "Fileshare", Set.of()), Sets.newHashSet(),false,printWriter);
		String s = stringWriter.toString();
		assertEquals(s, "");
		assertEquals((s.split("\\n")).length, 1);
	}


}