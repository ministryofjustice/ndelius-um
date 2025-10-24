package uk.co.bconline.ndelius.test.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.User;

import java.time.LocalDate;

import static java.util.Collections.singletonList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.bconline.ndelius.test.util.TokenUtils.token;

public class UserUtils {
    private static int nextTestUserId = 1;

    public static User aValidUser() {
        return User.builder()
            .username("test")
            .forenames("forenames")
            .surname("surname")
            .datasets(singletonList(Dataset.builder().code("N01").description("NPS London").build()))
            .homeArea(Dataset.builder().code("N01").description("NPS London").build())
            .startDate(LocalDate.of(2000, 1, 1))
            .privateSector(false)
            .build();
    }

    public static synchronized String nextTestUsername() {
        return "test.user" + String.valueOf(nextTestUserId++);
    }

    public static User createUser(MockMvc mvc) throws Exception {
        User user = aValidUser().toBuilder().username(nextTestUsername()).build();
        return createUser(mvc, user);
    }

    public static User createUser(MockMvc mvc, User user) throws Exception {
        mvc.perform(post("/api/user")
                .header("Authorization", "Bearer " + token(mvc))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user)))
            .andExpect(status().isCreated());
        return user;
    }

    public static ResultActions updateUser(MockMvc mvc, User user) throws Exception {
        return mvc.perform(post("/api/user/" + user.getUsername())
            .header("Authorization", "Bearer " + token(mvc))
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user)));
    }
}
