package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.ChangeNote;

import java.util.List;

public interface UserHistoryService {
	List<ChangeNote> getHistory(String username);
	List<ChangeNote> getHistory(Long userId);
	boolean hasHistory(Long userId);
}
