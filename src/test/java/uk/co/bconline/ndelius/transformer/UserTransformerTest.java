package uk.co.bconline.ndelius.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserTransformerTest
{
	@Autowired
	private UserTransformer transformer;

	@Test
	public void mapUserToSearchResult()
	{
		SearchResult result = transformer.map(User.builder()
				.username("username")
				.forenames("forename forename2")
				.surname("surname").build());

		assertEquals("username", result.getUsername());
		assertEquals("forename forename2", result.getForenames());
		assertEquals("surname", result.getSurname());
		assertNull(result.getStaffCode());
	}
}
