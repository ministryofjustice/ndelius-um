package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.UserHistoryItem;

import java.util.List;

public interface UserHistoryService {
	List<UserHistoryItem> getHistory(String username);
}
