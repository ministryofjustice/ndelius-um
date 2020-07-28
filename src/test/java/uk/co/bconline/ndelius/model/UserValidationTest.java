package uk.co.bconline.ndelius.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import uk.co.bconline.ndelius.model.auth.UserInteraction;

import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static uk.co.bconline.ndelius.test.util.UserUtils.aValidUser;
import static uk.co.bconline.ndelius.util.Constants.*;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserValidationTest {

	@Autowired
	private LocalValidatorFactoryBean localValidatorFactory;

	@Before
	public void user() {
		SecurityContextHolder.getContext()
				.setAuthentication(new TestingAuthenticationToken("test.user", "secret"));
	}

	@Test
	public void testUsernameBlank() {
		User user = aValidUser().toBuilder()
				.username("")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be blank"))));
	}

	@Test
	public void testUsernameSize() {
		User user = aValidUser().toBuilder()
				.username("1234567890123456789012345678901234567890123456789012345678901234567890")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("size must be between 0 and 60"))));
	}

	@Test
	public void testInvalidUsernamePattern() {
		User user = aValidUser().toBuilder()
				.username("john.bob!")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must be unique and contain only alphanumeric characters, hyphens, apostrophes or full-stops"))));
	}

	@Test
	public void testInvalidForename() {
		User user = aValidUser().toBuilder()
				.forenames("")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be blank"))));
	}

	@Test
	public void testInvalidSurname() {
		User user = aValidUser().toBuilder()
				.surname("")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be blank"))));
	}

	@Test
	public void testNullHomeArea() {
		User user = aValidUser().toBuilder()
				.homeArea(null)
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be null"))));
	}

	@Test
	public void testEmptyDataSets() {
		User user = aValidUser().toBuilder()
				.datasets(null)
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be empty"))));
	}

	@Test
	public void testBlankDatasets() {
		User user = aValidUser().toBuilder()
				.datasets(singletonList(Dataset.builder().code("").build()))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be blank"))));
	}

	@Test
	public void testStaffGradeWithoutStaffCode() {
		User user = aValidUser().toBuilder()
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Staff Code is required if Staff Grade is populated"))));
	}

	@Test
	public void testStaffCodeWithoutStaffGrade() {
		User user = aValidUser().toBuilder()
				.staffCode("N01A500")
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Staff Grade is required if Staff Code is populated"))));
	}

	@Test
	public void testStaffCodeWithEmptyStaffGrade() {
		User user = aValidUser().toBuilder()
				.staffCode("N01A500")
				.staffGrade(ReferenceData.builder().code("").build())
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must not be blank"))));
	}

	@Test
	public void testStaffCodeWithoutTeam() {
		User user = aValidUser().toBuilder()
				.staffCode("N01A501")
				.staffGrade(ReferenceData.builder().code("GRADE2").build())
				.teams(null)
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, empty());
	}

	@Test
	public void testTeamWithoutStaffCode() {
		User user = aValidUser().toBuilder()
				.teams(singletonList(Team.builder().code("N01TST").build()))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Staff Code is required if Teams is populated"))));
	}

	@Test
	public void testSubContractedProviderWithoutStaffCode() {
		User user = aValidUser().toBuilder()
				.subContractedProvider(Dataset.builder().code("SC").build())
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Staff Code is required if Sub Contracted Provider is populated"))));
	}

	@Test
	public void testEmptyTeamWithoutStaffCode() {
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
	public void testInvalidStaffCodePattern() {
		User user = aValidUser().toBuilder()
				.staffCode("N01-AAA")
				.staffGrade(ReferenceData.builder().code("GRADE1").build())
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must consist of 7 alphanumeric characters, however the recommended format is 3 alphanumeric characters followed by one letter and three numbers eg. XXXA001"))));
	}

	@Test
	public void testInvalidStaffCodePrefix() {
		User user = aValidUser().toBuilder()
				.staffCode("ZZZA001")
				.staffGrade(ReferenceData.builder().code("GRADE1").build())
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);

		assertThat(constraintViolations, hasSize(1));
		assertThat(constraintViolations, hasItem(hasProperty("message", is("prefix should correspond to a valid provider code"))));
	}

	@Test
	public void invalidRoles() {
		User user = User.builder()
				.roles(singletonList(Role.builder()
						.name("not-real")
						.build()))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("attempting to assign invalid roles"))));
	}

	@Test
	public void testOneDateNull() {
		User user = aValidUser().toBuilder()
				.startDate(LocalDate.of(2017, 5, 15))
				.endDate(null)
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, empty());
	}

	@Test
	public void startDateAfterEndDate() {
		User user = aValidUser().toBuilder()
				.startDate(LocalDate.of(2019, 6, 17))
				.endDate(LocalDate.of(2017, 5, 15))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Start Date must not be after End Date"))));
	}

	@Test
	public void startDateBeforeEndDate() {
		User user = aValidUser().toBuilder()
				.startDate(LocalDate.of(2017, 5, 15))
				.endDate(LocalDate.of(2019, 6, 17))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, empty());
	}

	@Test
	public void startDateShouldBeBefore2100() {
		User user = aValidUser().toBuilder()
				.startDate(LocalDate.of(2100, 1, 1))
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Date must be between 1900-01-01 and 2099-12-31"))));
	}

	@Test
	public void startDateShouldBeAfter1899() {
		User user = aValidUser().toBuilder()
				.startDate(LocalDate.of(1899, 12, 31))
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Date must be between 1900-01-01 and 2099-12-31"))));
	}

	@Test
	public void endDateShouldBeBefore2100() {
		User user = aValidUser().toBuilder()
				.endDate(LocalDate.of(2100, 1, 1))
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Date must be between 1900-01-01 and 2099-12-31"))));
	}

	@Test
	public void endDateShouldBeAfter1899() {
		User user = aValidUser().toBuilder()
				.endDate(LocalDate.of(1899, 12, 31))
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("Date must be between 1900-01-01 and 2099-12-31"))));
	}

	@Test
	public void testValidEmail() {
		User user = aValidUser().toBuilder()
				.email("test_valid_email@test.com")
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, empty());
	}

	@Test
	public void testEmailNull() {
		User user = aValidUser().toBuilder()
				.email(null)
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, empty());
	}

	@Test
	public void testEmailEmpty() {
		User user = aValidUser().toBuilder()
				.email("")
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, empty());
	}

	@Test
	public void testEmailTooLong() {
		User user = aValidUser().toBuilder()
				.email("*".repeat(256))
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("size must be between 0 and 255"))));
	}

	@Test
	public void testEmailNotTooLong() {
		User user = aValidUser().toBuilder()
				.email("*".repeat(255))
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, empty());
	}

	@Test
	public void invalidTelephoneNumber() {
		User user = aValidUser().toBuilder()
				.telephoneNumber("123a")
				.build();
		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("must contain only numbers and spaces"))));
	}

	@Test
	public void invalidDatasets() {
		User user = aValidUser().toBuilder()
				.datasets(singletonList(Dataset.builder()
						.code("not-real")
						.build()))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("attempting to assign invalid datasets"))));
	}

	@Test
	public void localUserCannotAssignNationalRole() {
		User user = aValidUser().toBuilder()
				.roles(singletonList(Role.builder()
						.name(NATIONAL_ROLE)    // Only a national user can apply the national access role
						.build()))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("attempting to assign invalid roles"))));
	}

	@Test
	public void nationalUserCanAssignNationalRole() {
		SecurityContextHolder.getContext()
				.setAuthentication(new TestingAuthenticationToken("test.user", "secret",
						// Note: the National Role is marked as sector:public - so we need to also have the Public Role
						asList(new UserInteraction(NATIONAL_ACCESS), new UserInteraction(PUBLIC_ACCESS))));

		User user = aValidUser().toBuilder()
				.roles(singletonList(Role.builder()
						.name(NATIONAL_ROLE)    // Only a national user can apply the national access role
						.build()))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, empty());
	}

	@Test
	public void localUserCanAssignTheirOwnDatasets() {
		User user = aValidUser().toBuilder()
				.datasets(singletonList(Dataset.builder()
						.code("N01")    // test.user only has NXX datasets
						.build()))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, empty());
	}

	@Test
	public void localUserCanOnlyAssignTheirOwnDatasets() {
		User user = aValidUser().toBuilder()
				.datasets(singletonList(Dataset.builder()
						.code("C01")    // test.user only has NXX datasets
						.build()))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, hasItem(hasProperty("message", is("attempting to assign invalid datasets"))));
	}

	@Test
	public void nationalUserCanAssignAnyDataset() {
		SecurityContextHolder.getContext()
				.setAuthentication(new TestingAuthenticationToken("test.user", "secret",
						singletonList(new UserInteraction(NATIONAL_ACCESS))));

		User user = aValidUser().toBuilder()
				.datasets(singletonList(Dataset.builder()
						.code("C01")    // test.user only has NXX datasets, but is now a National User
						.build()))
				.build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertThat(constraintViolations, empty());
	}
}