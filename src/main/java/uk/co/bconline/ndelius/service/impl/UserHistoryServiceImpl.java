package uk.co.bconline.ndelius.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.UserHistoryItem;
import uk.co.bconline.ndelius.repository.db.UserHistoryEntityRepository;
import uk.co.bconline.ndelius.service.UserEntityService;
import uk.co.bconline.ndelius.service.UserHistoryService;
import uk.co.bconline.ndelius.transformer.UserHistoryTransformer;

import java.util.List;

@Slf4j
@Service
public class UserHistoryServiceImpl implements UserHistoryService {

	private final UserHistoryEntityRepository repository;
	private final UserHistoryTransformer transformer;
	private final UserEntityService userEntityService;

	public UserHistoryServiceImpl(
			UserHistoryEntityRepository repository,
			UserHistoryTransformer transformer,
			UserEntityService userEntityService) {
		this.repository = repository;
		this.transformer = transformer;
		this.userEntityService = userEntityService;
	}

	@Override
	public List<UserHistoryItem> getHistory(String username) {
		val userId = userEntityService.getUserId(username);
		val entities = repository.getUserHistoryEntitiesByUserId(userId);
		return transformer.map(entities);
	}
}
