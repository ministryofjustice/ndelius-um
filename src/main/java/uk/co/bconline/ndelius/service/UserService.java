package uk.co.bconline.ndelius.service;

import org.springframework.validation.annotation.Validated;
import uk.co.bconline.ndelius.model.ExportResult;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;

import jakarta.validation.Valid;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Validated
public interface UserService
{
	boolean usernameExists(String username);
	List<SearchResult> search(String query, Map<String, Set<String>> groupFilter, Set<String> datasetFilter,
							  String role, boolean includeInactiveUsers, Integer page, Integer pageSize);
	Optional<User> getUser(String username);
	Optional<User> getUserByStaffCode(String staffCode);
	void addUser(@Valid User user);
	void updateUser(@Valid User user);
	Stream<ExportResult> exportAll();
	void exportAllToCsv(OutputStream outputStream);
}
