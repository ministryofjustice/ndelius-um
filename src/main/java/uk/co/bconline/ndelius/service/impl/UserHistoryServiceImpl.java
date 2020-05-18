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
		val entities = repository.getChangeNoteEntitiesByUserId(userId);
		return transformer.map(entities);
	}
}
