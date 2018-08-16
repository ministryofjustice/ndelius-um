package uk.co.bconline.ndelius.model;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Before;
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

	@Before
	public void user()
	{
		SecurityContextHolder.getContext()
				.setAuthentication(new AuthenticationToken(OIDUser.builder().username("test.user").build(), ""));
	}

	private static User aValidUser()
	{
		return User.builder()
				.username("test")
				.aliasUsername("test")
				.forenames("forenames")
				.surname("surname")
				.datasets(singletonList(Dataset.builder().code("N01").description("NPS London").build()))
				.homeArea(Dataset.builder().code("N01").description("NPS London").build())
				.startDate(LocalDate.of(2000, 1, 1))
				.privateSector(false)
				.build();
	}

	@Test
	public void testUsernameBlank()
	{
		User user = aValidUser().toBuilder()
				.username("")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be blank"))));
	}

	@Test
	public void testUsernameSize()
	{
		User user = aValidUser().toBuilder()
				.username("1234567890123456789012345678901234567890123456789012345678901234567890")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("size must be between 0 and 60"))));
	}

	@Test
	public void testInvalidUsernamePattern()
	{
		User user = aValidUser().toBuilder()
				.username("john.bob!")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("invalid format"))));
	}

	@Test
	public void testInvalidAliasUsername()
	{
		User user = aValidUser().toBuilder()
				.aliasUsername("1234567890123456789012345678901234567890123456789012345678901234567890")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("size must be between 0 and 60"))));
	}

	@Test
	public void testInvalidAliasPattern()
	{
		User user = aValidUser().toBuilder()
				.aliasUsername("aliasinvalid!!#'")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("invalid format"))));
	}

	@Test
	public void testInvalidForename()
	{
		User user = aValidUser().toBuilder()
				.forenames("")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be blank"))));
	}

	@Test
	public void testInvalidSurname()
	{
		User user = aValidUser().toBuilder()
				.surname("")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be blank"))));
	}
	@Test
	public void testNullHomeArea()
	{
		User user = aValidUser().toBuilder()
				.homeArea(null)
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be null"))));
	}
	@Test
	public void testEmptyDataSets()
	{
		User user = aValidUser().toBuilder()
				.datasets(null)
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be empty"))));
	}

	@Test
	public void testBlankDatasets()
	{
		User user = aValidUser().toBuilder()
				.datasets(singletonList(Dataset.builder().code("").build()))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be blank"))));
	}

	@Test
	public void testStaffCodeNotMatchingHomeAreaCode()
	{
		User user = aValidUser().toBuilder()
				.staffCode("C01A500")
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Home area code doesn't match staff code prefix"))));
	}
	@Test
	public void testStaffGradeWithoutStaffCode()
	{
		User user = aValidUser().toBuilder()
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Staff Code is required if Staff Grade is populated"))));
	}

	@Test
	public void testStaffCodeWithoutStaffGrade()
	{
		User user = aValidUser().toBuilder()
				.staffCode("N01A500")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Staff Grade is required if Staff Code is populated"))));
	}

	@Test
	public void testStaffCodeWithEmptyStaffGrade()
	{
		User user = aValidUser().toBuilder()
				.staffCode("N01A500")
				.staffGrade(ReferenceData.builder().code("").build())
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be blank"))));
	}

	@Test
	public void testStaffCodeWithoutTeam()
	{
		User user = aValidUser().toBuilder()
				.staffCode("N01A501")
				.staffGrade(ReferenceData.builder().code("GRADE2").build())
				.teams(null)
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, empty());
	}

	@Test
	public void testTeamWithoutStaffCode()
	{
		User user = aValidUser().toBuilder()
				.teams(singletonList(Team.builder().code("N01TST").build()))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Staff Code is required if Teams is populated"))));
	}

	@Test
	public void testEmptyTeamWithoutStaffCode()
	{
		User user = aValidUser().toBuilder()
				.staffCode("N01A500")
				.staffGrade(ReferenceData.builder().code("GRADE1").build())
				.teams(singletonList(Team.builder().code("").build()))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be blank"))));
	}

	@Test
	public void testInvalidStaffCodePattern()
	{
		User user = aValidUser().toBuilder()
				.staffCode("N01AAAA")
				.staffGrade(ReferenceData.builder().code("GRADE1").build())
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("invalid format"))));
	}

	@Test
	public void invalidRoles()
	{
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
		User user = aValidUser().toBuilder()
				.startDate(LocalDate.of(2017, 5, 15))
				.endDate(null)
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, empty());
	}

	@Test
	public void testBothDatesNull()
	{
		User user = aValidUser().toBuilder()
				.startDate(null)
				.endDate(null)
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be null"))));
	}

	@Test
	public void startDateAfterEndDate()
	{
		User user = aValidUser().toBuilder()
				.startDate(LocalDate.of(2019, 6, 17))
				.endDate(LocalDate.of(2017, 5, 15))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Start Date must not be after End Date"))));
	}

	@Test
	public void startDateBeforeEndDate()
	{
		User user = aValidUser().toBuilder()
				.startDate(LocalDate.of(2017, 5, 15))
				.endDate(LocalDate.of(2019, 6, 17))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, empty());
	}

	@Test
	public void testDuplicateAliasUsername(){
		User user = aValidUser().toBuilder()
				.aliasUsername("test.user.alias")
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Alias username must be unique"))));
	}

	@Test
	public void staffCodeShouldBeUnique()
	{
		User user = aValidUser().toBuilder()
				.staffCode("N01A000")
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Staff Code must be unique"))));
	}

	@Test
	public void staffCodeShouldBeUniqueToUser()
	{
		User user = aValidUser().toBuilder()
				.staffCode("N01A001")
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Staff Code must be unique"))));
	}

	@Test
	public void staffCodeShouldBeUniqueToUserBeingUpdated()
	{
		User user = aValidUser().toBuilder()
				.username("test.user")
				.staffCode("N01A001")
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, not(hasItem(hasProperty("message", is("Staff Code must be unique")))));
	}
}