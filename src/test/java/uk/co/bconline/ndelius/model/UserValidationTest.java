package uk.co.bconline.ndelius.model;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import uk.co.bconline.ndelius.model.ldap.OIDUser;
import uk.co.bconline.ndelius.security.AuthenticationToken;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.service.UserService;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserValidationTest
{
	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	@Autowired
	private LocalValidatorFactoryBean localValidatorFactory;

	@Test
	public void testUsernameBlank()
	{
		User user = User.builder().username("").aliasUsername("a").forenames("a").surname("a").build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testUsernameBlank error - expected 1 violation", 1, constraintViolations.size());
		assertEquals("must not be blank", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testUsernameSize()
	{
		User user = User.builder().username("1234567890123456789012345678901234567890123456789012345678901234567890")
				.aliasUsername("a").forenames("a").surname("a").build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testUsernameSize error - expected 1 violation", 1, constraintViolations.size());
		assertEquals("size must be between 0 and 60", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testInvalidUsernamePattern()
	{
		User user = User.builder().username("john.bob!").aliasUsername("a").forenames("a").surname("a").build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testInvalidUsernamePattern error - expected 1 violation", 1, constraintViolations.size());
		assertEquals("invalid format", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testInvalidAliasUsername()
	{
		User user = User.builder().username("john.smith123")
				.aliasUsername("1234567890123456789012345678901234567890123456789012345678901234567890").forenames("a")
				.surname("a").build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("aliasUsername error - expected 1 violation", 1, constraintViolations.size());
		assertEquals("size must be between 0 and 60", constraintViolations.iterator().next().getMessage());

	}

	@Test
	public void testInvalidAliasPattern()
	{
		User user = User.builder().username("john.smith123").aliasUsername("aliasinvalid!!#'").forenames("a")
				.surname("a").build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testInvalidAliasPattern error - expected 1 violation", 1, constraintViolations.size());
		assertEquals("invalid format", constraintViolations.iterator().next().getMessage());

	}

	@Test
	public void testInvalidForename()
	{
		User user = User.builder().username("john.smith123").aliasUsername("jsmith1").forenames("").surname("a")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testInvalidForename error - expected 1 violation", 1, constraintViolations.size());
		assertEquals("must not be blank", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testInvalidSurname()
	{
		User user = User.builder().username("john.smith123").aliasUsername("jsmith1").forenames("john").surname("")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testInvalidSurname error - expected 1 violation", 1, constraintViolations.size());
		assertEquals("must not be blank", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testStaffCodeCrossValidation() // give me a team and a staff grade to pass
	{

		ReferenceData rd = new ReferenceData();
		ArrayList<Team> team1 = new ArrayList<>();

		User user = User.builder().username("john.smith123").aliasUsername("jsmith1").forenames("john").surname
				("smith")
				.staffCode("C01A123").staffGrade(rd).teams(team1).build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testStaffCodeCrossValidation - Expected 1 Violation", 1, constraintViolations.size());
		assertEquals("attempting to submit invalid staff details", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testInvalidStaffCodePattern() // give me a team and a staff grade to pass
	{
		User user = User.builder().username("john.smith123")
					.aliasUsername("jsmith1")
					.forenames("john")
					.surname("smith")
					.staffCode("1231231")
					.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
					.teams(singletonList(Team.builder().code("N01TST").build()))
					.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testInvalidStaffCodePattern - Expected 1 violation ", 1, constraintViolations.size());
		assertThat(constraintViolations, hasItem(hasProperty("message", is("invalid format"))));
	}

	@Test
	public void invalidRoles()
	{
		SecurityContextHolder.getContext().setAuthentication(new AuthenticationToken(OIDUser.builder().username("test.user").build(), ""));
		User user = User.builder()
				.roles(singletonList(Role.builder()
						.name("not-real")
						.build()))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("attempting to assign invalid roles"))));
	}

	@Test
	public void testOneDateNull()
	{

		LocalDate startDate = LocalDate.of(2017, 5, 15);

		SecurityContextHolder.getContext()
				.setAuthentication(new AuthenticationToken(OIDUser.builder().username("test.user").build(), ""));
		User user = User.builder().username("test.user").forenames("1").surname("1").startDate(startDate).endDate(null)
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("attempting to submit invalid dates"))));
	}

	@Test
	public void testBothDatesNull()
	{
		SecurityContextHolder.getContext()
				.setAuthentication(new AuthenticationToken(OIDUser.builder().username("test.user").build(), ""));
		User user = User.builder().username("test.user").forenames("1").surname("1").startDate(null).endDate(null)
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testBothDatesNull returned a violation", 0, constraintViolations.size());
	}

	@Test
	public void startDateAfterEndDate()
	{
		LocalDate startDate = LocalDate.of(2019, 6, 17);
		LocalDate endDate = LocalDate.of(2017, 5, 15);

		SecurityContextHolder.getContext()
				.setAuthentication(new AuthenticationToken(OIDUser.builder().username("test.user").build(), ""));
		User user = User.builder().username("test.user").forenames("1").surname("1").startDate(startDate)
				.endDate(endDate).build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("attempting to submit invalid dates"))));
	}

	@Test
	public void startDateBeforeEndDate()
	{
		LocalDate startDate = LocalDate.of(2017, 4, 14);
		LocalDate endDate = LocalDate.of(2019, 5, 15);

		SecurityContextHolder.getContext()
				.setAuthentication(new AuthenticationToken(OIDUser.builder().username("test.user").build(), ""));
		User user = User.builder().username("test.user").forenames("1").surname("1").startDate(startDate)
				.endDate(endDate).build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("startDateBeforeEndDate returned a violation", 0, constraintViolations.size());
	}
}