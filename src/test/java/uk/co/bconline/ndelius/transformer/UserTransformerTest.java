package uk.co.bconline.ndelius.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.entity.UserEntity;

public class UserTransformerTest
{

	@Test
	public void mapUserToSearchResult()
	{
		UserEntity user = new UserEntity();
		user.setUsername("username");
		user.setForename("forename");
		user.setForename2("forename2");
		user.setSurname("surname");

		SearchResult result = new UserTransformer().map(user);

		assertEquals("username", result.getUsername());
		assertEquals("forename forename2", result.getForenames());
		assertEquals("surname", result.getSurname());
		assertNull(result.getStaffCode());
	}
}