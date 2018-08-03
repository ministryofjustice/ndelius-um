package uk.co.bconline.ndelius.model;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

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
	public void testInvalidStaffCodePattern()
	{
		User user = User.builder().username("john.smith123").aliasUsername("jsmith1").forenames("john").surname
				("smith")
				.staffCode("123A12#").build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("staffCode validation error found", 1, constraintViolations.size());
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
}