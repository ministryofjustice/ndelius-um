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
import uk.co.bconline.ndelius.service.UserRoleService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
	public void csvHasCorrectHeader() throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		service.exportSearchToCSV("test.user",ImmutableMap.of("NDMIS-Reporting", Set.of(), "Fileshare", Set.of()), Sets.newHashSet(),false,printWriter);
		String s = stringWriter.toString();
		String expectedHeader = "\"Username\",\"Forenames\",\"Surname\",\"Team(s)\",\"StaffCode\",\"Sources\",\"EndDate\"";
		assertEquals(s.split("\\n")[0], expectedHeader);
	}

	@Test
	public void csvHasTestData() throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		service.exportSearchToCSV("test.user",ImmutableMap.of("NDMIS-Reporting", Set.of(), "Fileshare", Set.of()), Sets.newHashSet(),false,printWriter);
		String s = stringWriter.toString();
		String expectedTestUser = "\"test.user\",\"Test\",\"User\",\"Team(code=N02TST, description=Other team) Team(code=N01TST, description=Test team) Team(code=N03TST, description=Another)\",\"N01A001\",\"LDAP\",\"\"";
		assertTrue(Arrays.asList(s.split("\\n")).contains(expectedTestUser));
	}

	@Test
	public void csvHasInActiveUsersTestData() throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		service.exportSearchToCSV("test.user.inactive",ImmutableMap.of("NDMIS-Reporting", Set.of(), "Fileshare", Set.of()), Sets.newHashSet(),true,printWriter);
		String s = stringWriter.toString();
		String expectedTestUser = "\"test.user.inactive\",\"Inactive\",\"User\",\"\",\"\",\"LDAP\",\"2000-01-01\"";
		assertTrue(Arrays.asList(s.split("\\n")).contains(expectedTestUser));
	}

	@Test
	public void csvEmptyWithNoSearchResults() throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		service.exportSearchToCSV("noResults",ImmutableMap.of("NDMIS-Reporting", Set.of(), "Fileshare", Set.of()), Sets.newHashSet(),false,printWriter);
		String s = stringWriter.toString();
		assertEquals(s, "");
		assertEquals((s.split("\\n")).length, 1);
	}


}