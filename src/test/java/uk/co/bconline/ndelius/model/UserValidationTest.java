package uk.co.bconline.ndelius.model;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
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
		User user = User.builder().username("").aliasUsername("a").forenames("a").surname("a").staffCode("C01A123")
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false).startDate(LocalDate.of(2018,8,13))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testUsernameBlank error - expected 1 violation", 1, constraintViolations.size());
		assertEquals("must not be blank", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testUsernameSize()
	{
		User user = User.builder().username("1234567890123456789012345678901234567890123456789012345678901234567890")
				.aliasUsername("a").forenames("a").surname("a")
				.staffCode("C01A123")
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false).startDate(LocalDate.of(2018,8,13))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testUsernameSize error - expected 1 violation", 1, constraintViolations.size());
		assertEquals("size must be between 0 and 60", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testInvalidUsernamePattern()
	{
		User user = User.builder().username("john.bob!").aliasUsername("a").forenames("a").surname("a").staffCode("C01A123")
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false).startDate(LocalDate.of(2018,8,13))
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testInvalidUsernamePattern error - expected 1 violation", 1, constraintViolations.size());
		assertEquals("invalid format", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testInvalidAliasUsername()
	{
		User user = User.builder().username("john.smith123")
				.aliasUsername("1234567890123456789012345678901234567890123456789012345678901234567890")
				.forenames("a").surname("a").staffCode("C01A123")
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false).startDate(LocalDate.of(2018,8,13))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testInvalidAliasUsername error - expected 1 violation", 1, constraintViolations.size());
		assertEquals("size must be between 0 and 60", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testInvalidAliasPattern()
	{
		User user = User.builder().username("john.smith123").aliasUsername("aliasinvalid!!#'").forenames("a")
				.surname("a").staffCode("C01A123")
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false).startDate(LocalDate.of(2018,8,13))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testInvalidAliasPattern error - expected 1 violation", 1, constraintViolations.size());
		assertEquals("invalid format", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testInvalidForename()
	{
		User user = User.builder().username("john.smith123").aliasUsername("jsmith1").forenames("").surname("a")
				.staffCode("C01A123")
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false).startDate(LocalDate.of(2018,8,13))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testInvalidForename error - expected 1 violation", 1, constraintViolations.size());
		assertEquals("must not be blank", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testInvalidSurname()
	{
		User user = User.builder().username("john.smith123").aliasUsername("jsmith1").forenames("john").surname("")
				.staffCode("C01A123")
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false).startDate(LocalDate.of(2018,8,13))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testInvalidSurname error - expected 1 violation", 1, constraintViolations.size());
		assertEquals("must not be blank", constraintViolations.iterator().next().getMessage());
	}
	@Test
	public void testNullHomeArea()
	{
		User user = User.builder().username("john.smith123")
				.aliasUsername("123")
				.forenames("a").surname("a")
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.privateSector(false)
				.homeArea(null).startDate(LocalDate.of(2018,8,13))
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testNullHomeArea returned a violation", 1, constraintViolations.size());
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be null"))));
	}
	@Test
	public void testEmptyDataSets()
	{
		User user = User.builder().username("john.smith123")
				.aliasUsername("123")
				.forenames("a").surname("a")
				.datasets(null)
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false).startDate(LocalDate.of(2018,8,13))
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testEmptyDataSets returned a violation", 1, constraintViolations.size());
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be empty"))));
	}
	@Test
	public void testStaffCodeNotMatchingHomeAreaCode()
	{

		User user = User.builder().username("john.smith123").aliasUsername("jsmith1").forenames("john").surname
				("smith")
				.staffCode("C01A123")
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.homeArea(Dataset.builder().code("C02").description("CRC London").build())
				.privateSector(false).startDate(LocalDate.of(2018,8,13))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testStaffCodeNotMatchingHomeAreaCode - Expected 1 Violation", 1, constraintViolations.size());
		assertEquals("Home area code doesn't match staff code prefix", constraintViolations.iterator().next().getMessage());
	}
	@Test
	public void testStaffGradeWithoutStaffCode()
	{
		User user = User.builder().username("john.smith123").aliasUsername("jsmith1").forenames("john").surname("smith")
				.staffCode(null)
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false).startDate(LocalDate.of(2018,8,13))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testStaffGradeWithoutStaffCode - Expected 1 Violation", 1, constraintViolations.size());
		assertEquals("Staff code is required when entering staff grade", constraintViolations.iterator().next()
				.getMessage());
	}

	@Test
	public void testStaffCodeWithoutStaffGrade()
	{
		User user = User.builder().username("john.smith123").aliasUsername("jsmith1").forenames("john").surname("smith")
				.staffCode("C01A770")
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.staffGrade(ReferenceData.builder().code("").build())
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false).startDate(LocalDate.of(2018,8,13))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testStaffGradeWithoutStaffCode - Expected 1 Violation", 1, constraintViolations.size());
		assertEquals("Staff grade is required when entering staff code", constraintViolations.iterator().next()
				.getMessage());
	}

	@Test
	public void testStaffCodeWithoutTeam()
	{
		User user = User.builder().username("john.smith123").aliasUsername("jsmith1").forenames("john").surname("smith")
				.staffCode("C01A876")
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false).startDate(LocalDate.of(2018,8,13))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		for(int i = 0; i < constraintViolations.size(); i++){
			System.out.println(constraintViolations.iterator().next().getMessage());
		}
		assertEquals("testStaffCodeWithoutTeam - Expected 0 Violations", 0, constraintViolations.size());

	}

	@Test
	public void testTeamWithoutStaffCode()
	{
		User user = User.builder().username("john.smith123").aliasUsername("jsmith1").forenames("john").surname("smith")
				.staffCode(null)
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.teams(singletonList(Team.builder().code("N01TST").build()))
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false).startDate(LocalDate.of(2018,8,13))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testTeamWithoutStaffCode - Expected 1 Violation", 1, constraintViolations.size());
		assertEquals("Teams should be empty when staff code is not included", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testEmptyTeamWithoutStaffCode()
	{
		User user = User.builder().username("john.smith123").aliasUsername("jsmith1").forenames("john").surname("smith")
				.staffCode(null)
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.teams(singletonList(Team.builder().code("").build()))
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false)
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be blank"))));
	}
	@Test
	public void testInvalidStaffCodePattern()
	{
		User user = User.builder().username("john.smith123").aliasUsername("jsmith1").forenames("john")
					.surname("smith").staffCode("C01AAAA")
					.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
					.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
					.homeArea(Dataset.builder().code("C01").description("CRC London").build())
					.privateSector(false).startDate(LocalDate.of(2018,8,13))
					.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testInvalidStaffCodePattern - Expected 1 violation ", 1, constraintViolations.size());
		assertEquals("invalid format", constraintViolations.iterator().next().getMessage());
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
		User user = User.builder().username("test.user").forenames("1").surname("1")
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false)
				.startDate(startDate)
				.endDate(null)
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testOneDateNull - Expected 0 Violations", 0, constraintViolations.size());
	}

	@Test
	public void testBothDatesNull()
	{
		SecurityContextHolder.getContext()
				.setAuthentication(new AuthenticationToken(OIDUser.builder().username("test.user").build(), ""));
		User user = User.builder().username("test.user").forenames("1").surname("1")
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false)
				.startDate(null).endDate(null)
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("testBothDatesNull returned a violation", 1, constraintViolations.size());
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be null"))));
	}

	@Test
	public void startDateAfterEndDate()
	{
		LocalDate startDate = LocalDate.of(2019, 6, 17);
		LocalDate endDate = LocalDate.of(2017, 5, 15);

		SecurityContextHolder.getContext()
				.setAuthentication(new AuthenticationToken(OIDUser.builder().username("test.user").build(), ""));
		User user = User.builder().username("test.user").forenames("1").surname("1")
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.startDate(startDate)
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
		User user = User.builder().username("test.user").forenames("1").surname("1")
				.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
				.homeArea(Dataset.builder().code("C01").description("CRC London").build())
				.privateSector(false)
				.startDate(startDate)
				.endDate(endDate).build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("startDateBeforeEndDate returned a violation", 0, constraintViolations.size());
	}
}