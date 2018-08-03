package uk.co.bconline.ndelius.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserValidationTest
{
	private LocalValidatorFactoryBean localValidatorFactory;
	@Autowired
	private WebApplicationContext context;

	@Autowired
	private BasicAuthenticationFilter basicAuthenticationFilter;

	@Autowired
	private OncePerRequestFilter jwtAuthenticationFilter;

	private MockMvc mvc;

	@Before
	public void setup()
	{
		mvc = MockMvcBuilders.webAppContextSetup(context).addFilter(jwtAuthenticationFilter)
				.addFilter(basicAuthenticationFilter).alwaysDo(print()).build();

		localValidatorFactory = new LocalValidatorFactoryBean();
		localValidatorFactory.setProviderClass(HibernateValidator.class);
		localValidatorFactory.afterPropertiesSet();

	}

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
		assertEquals("username invalid format", constraintViolations.iterator().next().getMessage());
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
		assertEquals("aliasUsername invalid format", constraintViolations.iterator().next().getMessage());

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
		assertEquals("staff code invalid format", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testInvalidStaffCodeSize()
	{
		User user = User.builder().username("john.smith123").aliasUsername("jsmith1").forenames("john").surname
				("smith")
				.staffCode("123").build();

		Set<ConstraintViolation<User>> constraintViolations = localValidatorFactory.validate(user);
		assertEquals("staffCode validation error found", 2, constraintViolations.size());
		assertThat(constraintViolations, hasItems(hasProperty("message", is("size must be between 7 and 7")),
				hasProperty("message", is("staff code invalid format"))));

	}
}