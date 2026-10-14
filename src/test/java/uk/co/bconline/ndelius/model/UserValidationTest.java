package uk.co.bconline.ndelius.model;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import uk.co.bconline.ndelius.model.auth.UserInteraction;

import java.time.LocalDate;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.bconline.ndelius.test.util.UserUtils.aValidUser;
import static uk.co.bconline.ndelius.util.Constants.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserValidationTest {

    @Autowired
    private LocalValidatorFactoryBean localValidatorFactory;

    @BeforeEach
    public void user() {
        SecurityContextHolder.getContext()
            .setAuthentication(new TestingAuthenticationToken("test.user", "secret"));
    }

    @Test
    public void testUsernameBlank() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .username("")
            .build(), "must not be blank");
    }

    @Test
    public void testUsernameSize() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .username("1234567890123456789012345678901234567890123456789012345678901234567890")
            .build(), "size must be between 0 and 60");
    }

    @Test
    public void testInvalidUsernamePattern() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .username("john.bob!")
            .build(), "must be unique and contain only alphanumeric characters, hyphens, apostrophes or full-stops");
    }

    @Test
    public void testInvalidForename() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .forenames("")
            .build(), "must not be blank");
    }

    @Test
    public void testInvalidSurname() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .surname("")
            .build(), "must not be blank");
    }

    @Test
    public void testNullHomeArea() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .homeArea(null)
            .build(), "must not be null");
    }

    @Test
    public void testEmptyDataSets() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .datasets(null)
            .build(), "must not be empty");
    }

    @Test
    public void testBlankDatasets() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .datasets(singletonList(Dataset.builder().code("").build()))
            .build(), "must not be blank");
    }

    @Test
    public void testStaffGradeWithoutStaffCode() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
            .build(), "Staff Code is required if Staff Grade is populated");
    }

    @Test
    public void testStaffCodeWithoutStaffGrade() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .staffCode("N01A500")
            .build(), "Staff Grade is required if Staff Code is populated");
    }

    @Test
    public void testStaffCodeWithEmptyStaffGrade() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .staffCode("N01A500")
            .staffGrade(ReferenceData.builder().code("").build())
            .build(), "must not be blank");
    }

    @Test
    public void testStaffCodeWithoutTeam() {
        assertNoViolations(aValidUser().toBuilder()
            .staffCode("N01A501")
            .staffGrade(ReferenceData.builder().code("GRADE2").build())
            .teams(null)
            .build());
    }

    @Test
    public void testTeamWithoutStaffCode() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .teams(singletonList(Team.builder().code("N01TST").build()))
            .build(), "Staff Code is required if Teams is populated");
    }

    @Test
    public void testSubContractedProviderWithoutStaffCode() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .subContractedProvider(Dataset.builder().code("SC").build())
            .build(), "Staff Code is required if Sub Contracted Provider is populated");
    }

    @Test
    public void testEmptyTeamWithoutStaffCode() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .staffCode("N01A500")
            .staffGrade(ReferenceData.builder().code("GRADE1").build())
            .teams(singletonList(Team.builder().code("").build()))
            .build(), "must not be blank");
    }

    @Test
    public void testInvalidStaffCodePattern() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .staffCode("N01-AAA")
            .staffGrade(ReferenceData.builder().code("GRADE1").build())
            .build(), "must consist of 7 alphanumeric characters, however the recommended format is 3 alphanumeric characters followed by one letter and three numbers eg. XXXA001");
    }

    @Test
    public void testInvalidStaffCodePrefix() {
        assertSingleViolationMessage(aValidUser().toBuilder()
            .staffCode("ZZZA001")
            .staffGrade(ReferenceData.builder().code("GRADE1").build())
            .build(), "prefix should correspond to a valid provider code");
    }

    @Test
    public void invalidRoles() {
        assertHasViolationMessage(User.builder()
            .roles(singletonList(Role.builder()
                .name("not-real")
                .build()))
            .build(), "attempting to assign invalid roles");
    }

    @Test
    public void testOneDateNull() {
        assertNoViolations(aValidUser().toBuilder()
            .startDate(LocalDate.of(2017, 5, 15))
            .endDate(null)
            .build());
    }

    @Test
    public void startDateAfterEndDate() {
        assertHasViolationMessage(aValidUser().toBuilder()
            .startDate(LocalDate.of(2019, 6, 17))
            .endDate(LocalDate.of(2017, 5, 15))
            .build(), "Start Date must not be after End Date");
    }

    @Test
    public void startDateBeforeEndDate() {
        assertNoViolations(aValidUser().toBuilder()
            .startDate(LocalDate.of(2017, 5, 15))
            .endDate(LocalDate.of(2019, 6, 17))
            .build());
    }

    @Test
    public void startDateShouldBeBefore2100() {
        assertHasViolationMessage(aValidUser().toBuilder()
            .startDate(LocalDate.of(2100, 1, 1))
            .build(), "Date must be between 1900-01-01 and 2099-12-31");
    }

    @Test
    public void startDateShouldBeAfter1899() {
        assertHasViolationMessage(aValidUser().toBuilder()
            .startDate(LocalDate.of(1899, 12, 31))
            .build(), "Date must be between 1900-01-01 and 2099-12-31");
    }

    @Test
    public void endDateShouldBeBefore2100() {
        assertHasViolationMessage(aValidUser().toBuilder()
            .endDate(LocalDate.of(2100, 1, 1))
            .build(), "Date must be between 1900-01-01 and 2099-12-31");
    }

    @Test
    public void endDateShouldBeAfter1899() {
        assertHasViolationMessage(aValidUser().toBuilder()
            .endDate(LocalDate.of(1899, 12, 31))
            .build(), "Date must be between 1900-01-01 and 2099-12-31");
    }

    @Test
    public void testValidEmail() {
        assertNoViolations(aValidUser().toBuilder()
            .email("test_valid_email@test.com")
            .build());
    }

    @Test
    public void testEmailNull() {
        assertNoViolations(aValidUser().toBuilder()
            .email(null)
            .build());
    }

    @Test
    public void testEmailEmpty() {
        assertNoViolations(aValidUser().toBuilder()
            .email("")
            .build());
    }

    @Test
    public void testEmailTooLong() {
        assertHasViolationMessage(aValidUser().toBuilder()
            .email("*".repeat(256))
            .build(), "size must be between 0 and 255");
    }

    @Test
    public void testEmailNotTooLong() {
        assertNoViolations(aValidUser().toBuilder()
            .email("*".repeat(255))
            .build());
    }

    @Test
    public void invalidTelephoneNumber() {
        assertHasViolationMessage(aValidUser().toBuilder()
            .telephoneNumber("123a")
            .build(), "must contain only numbers and spaces");
    }

    @Test
    public void invalidDatasets() {
        assertHasViolationMessage(aValidUser().toBuilder()
            .datasets(singletonList(Dataset.builder()
                .code("not-real")
                .build()))
            .build(), "attempting to assign invalid datasets");
    }

    @Test
    public void localUserCannotAssignNationalRole() {
        assertHasViolationMessage(aValidUser().toBuilder()
            .roles(singletonList(Role.builder()
                .name(NATIONAL_ROLE)
                .build()))
            .build(), "attempting to assign invalid roles");
    }

    @Test
    public void nationalUserCanAssignNationalRole() {
        SecurityContextHolder.getContext()
            .setAuthentication(new TestingAuthenticationToken("test.user", "secret",
                asList(new UserInteraction(NATIONAL_ACCESS), new UserInteraction(PUBLIC_ACCESS))));

        assertNoViolations(aValidUser().toBuilder()
            .roles(singletonList(Role.builder()
                .name(NATIONAL_ROLE)
                .build()))
            .build());
    }

    @Test
    public void localUserCanAssignTheirOwnDatasets() {
        assertNoViolations(aValidUser().toBuilder()
            .datasets(singletonList(Dataset.builder()
                .code("N01")
                .build()))
            .build());
    }

    @Test
    public void localUserCanOnlyAssignTheirOwnDatasets() {
        assertHasViolationMessage(aValidUser().toBuilder()
            .datasets(singletonList(Dataset.builder()
                .code("C01")
                .build()))
            .build(), "attempting to assign invalid datasets");
    }

    @Test
    public void nationalUserCanAssignAnyDataset() {
        SecurityContextHolder.getContext()
            .setAuthentication(new TestingAuthenticationToken("test.user", "secret",
                singletonList(new UserInteraction(NATIONAL_ACCESS))));

        assertNoViolations(aValidUser().toBuilder()
            .datasets(singletonList(Dataset.builder()
                .code("C01")
                .build()))
            .build());
    }

    private Set<ConstraintViolation<User>> validate(User user) {
        return localValidatorFactory.validate(user);
    }

    private void assertSingleViolationMessage(User user, String message) {
        assertThat(validate(user))
            .extracting(ConstraintViolation::getMessage)
            .containsExactly(message);
    }

    private void assertHasViolationMessage(User user, String message) {
        assertThat(validate(user))
            .extracting(ConstraintViolation::getMessage)
            .contains(message);
    }

    private void assertNoViolations(User user) {
        assertThat(validate(user)).isEmpty();
    }
}
