package uk.co.bconline.ndelius.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.co.bconline.ndelius.model.User;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.bconline.ndelius.test.util.CustomMatchers.isWithin;
import static uk.co.bconline.ndelius.test.util.TokenUtils.token;
import static uk.co.bconline.ndelius.test.util.UserUtils.aValidUser;
import static uk.co.bconline.ndelius.test.util.UserUtils.nextTestUsername;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserHistoryControllerTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.alwaysDo(print())
				.build();
	}

	@Test
	public void historyIsReturned() throws Exception {
		mvc.perform(get("/api/user/Joe.Bloggs/history")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(4)));
	}

	@Test
	public void historyIsPersistedOnCreate() throws Exception {
		String token = token(mvc);
		String username = nextTestUsername();

		// When a new user is created
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(username).build())))
				.andExpect(status().isCreated());

		// Then the last updated details on the user are correct
		mvc.perform(get("/api/user/" + username)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.created.user.username", equalTo("test.user")))
				.andExpect(jsonPath("$.created.time", isWithin(5, SECONDS).of(now())))
				.andExpect(jsonPath("$.updated.user.username", equalTo("test.user")))
				.andExpect(jsonPath("$.updated.time", isWithin(5, SECONDS).of(now())));

		// And the history shows the creation
		mvc.perform(get("/api/user/" + username + "/history")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].user.username", equalTo("test.user")))
				.andExpect(jsonPath("$[0].time", isWithin(5, SECONDS).of(now())));
	}

	@Test
	public void historyIsPersistedOnUpdate() throws Exception {
		String token = token(mvc);
		String username = nextTestUsername();
		User user = aValidUser().toBuilder().username(username).build();

		// Given a user
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user)))
				.andExpect(status().isCreated());

		// When they are updated with a change note
		mvc.perform(post("/api/user/" + username)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user.toBuilder()
						.changeNote("Test note 123").build())))
				.andExpect(status().isNoContent());

		// Then the last updated details on the user are correct
		mvc.perform(get("/api/user/" + username)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.created.user.username", equalTo("test.user")))
				.andExpect(jsonPath("$.created.time", isWithin(5, SECONDS).of(now())))
				.andExpect(jsonPath("$.updated.user.username", equalTo("test.user")))
				.andExpect(jsonPath("$.updated.time", isWithin(5, SECONDS).of(now())));

		// And the history reflects the update
		mvc.perform(get("/api/user/" + username + "/history")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].user.username", equalTo("test.user")))
				.andExpect(jsonPath("$[0].time", isWithin(5, SECONDS).of(now())))
				.andExpect(jsonPath("$[0].note", equalTo("Test note 123")))
				.andExpect(jsonPath("$[1].user.username", equalTo("test.user")))
				.andExpect(jsonPath("$[1].time", isWithin(5, SECONDS).of(now())));
	}


	@Test
	public void changeNoteCannotBeLongerThan4000Characters() throws Exception {
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token(mvc))
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(nextTestUsername())
						.changeNote(String.join("*", new String[4001])).build())))
				.andExpect(status().isBadRequest());
	}
}