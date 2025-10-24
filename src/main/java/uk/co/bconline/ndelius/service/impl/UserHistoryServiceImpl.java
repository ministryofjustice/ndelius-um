package uk.co.bconline.ndelius.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.ChangeNote;
import uk.co.bconline.ndelius.repository.db.ChangeNoteRepository;
import uk.co.bconline.ndelius.service.UserEntityService;
import uk.co.bconline.ndelius.service.UserHistoryService;
import uk.co.bconline.ndelius.transformer.ChangeNoteTransformer;

import java.util.List;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class UserHistoryServiceImpl implements UserHistoryService {

    private final ChangeNoteRepository repository;
    private final ChangeNoteTransformer transformer;
    private final UserEntityService userEntityService;

    public UserHistoryServiceImpl(
        ChangeNoteRepository repository,
        ChangeNoteTransformer transformer,
        UserEntityService userEntityService) {
        this.repository = repository;
        this.transformer = transformer;
        this.userEntityService = userEntityService;
    }

    @Override
    public List<ChangeNote> getHistory(String username) {
        val userId = userEntityService.getUserId(username);
        return getHistory(userId);
    }

    @Override
    public List<ChangeNote> getHistory(Long userId) {
        val entities = repository.getByUserId(userId);
        return transformer.map(entities);
    }

    @Override
    public boolean hasHistory(Long userId) {
        return ofNullable(userId).map(repository::existsByUserId).orElse(false);
    }
}
