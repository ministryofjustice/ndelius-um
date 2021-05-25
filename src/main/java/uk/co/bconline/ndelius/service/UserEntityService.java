package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.entity.StaffEntity;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.model.entity.export.UserExportEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface UserEntityService
{
	long getUserId(String username);
	long getMyUserId();
	boolean usernameExists(String username);
	Optional<UserEntity> getUser(String username);
	Optional<StaffEntity> getStaffByStaffCode(String code);
	Optional<UserEntity> getUserByStaffCode(String staffCode);
	Stream<UserExportEntity> export();
	List<SearchResult> search(String searchTerm, boolean includeInactiveUsers, Set<String> datasets);
	UserEntity save(UserEntity user);
}
