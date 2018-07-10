package uk.co.bconline.ndelius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.repository.db.UserRepository;
import uk.co.bconline.ndelius.transformer.UserTransformer;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserTransformer userTransformer;

    @Autowired
    public UserService(UserRepository userRepository, UserTransformer userTransformer) {
        this.userRepository = userRepository;
        this.userTransformer = userTransformer;
    }

    public Optional<User> getUser(String username) {
        User user = User.builder().id(username).build();
        return userRepository.getUserEntityByDistinguishedNameEqualsIgnoreCase(username)
                .map(u -> userTransformer.userOf(user, u));
    }
}
