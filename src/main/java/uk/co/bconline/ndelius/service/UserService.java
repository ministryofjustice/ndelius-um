package uk.co.bconline.ndelius.service;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.validation.annotation.Validated;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;

import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Validated
public interface UserService
{
	boolean usernameExists(String username);
	List<SearchResult> search(String query, Map<String, Set<String>> groupFilter, Set<String> datasetFilter,
							  String role, boolean includeInactiveUsers, Integer page, Integer pageSize);
	void exportSearchToCSV(String query, Map<String, Set<String>> groupFilter, Set<String> datasetFilter,
							  boolean includeInactiveUsers, PrintWriter writer) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException;
	Optional<User> getUser(String username);
	Optional<User> getUserByStaffCode(String staffCode);
	void addUser(@Valid User user);
	void updateUser(@Valid User user);
}
