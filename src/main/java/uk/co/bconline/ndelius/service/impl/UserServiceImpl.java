package uk.co.bconline.ndelius.service.impl;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.util.Optionals;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.bconline.ndelius.exception.AppException;
import uk.co.bconline.ndelius.model.ExportResult;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.model.entry.UserEntry;
import uk.co.bconline.ndelius.service.*;
import uk.co.bconline.ndelius.transformer.SearchResultTransformer;
import uk.co.bconline.ndelius.transformer.UserTransformer;
import uk.co.bconline.ndelius.util.CSVUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.concurrent.CompletableFuture.*;
import static java.util.stream.Collectors.*;
import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;
import static uk.co.bconline.ndelius.util.AuthUtils.isNational;
import static uk.co.bconline.ndelius.util.AuthUtils.myUsername;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
	private final UserEntityService userEntityService;
	private final UserEntryService userEntryService;
	private final DatasetService datasetService;
	private final GroupService groupService;
	private final UserTransformer transformer;
	private final SearchResultTransformer searchResultTransformer;
	private final TaskExecutor taskExecutor;

	@Autowired
	public UserServiceImpl(
			UserEntityService userEntityService,
			UserEntryService userEntryService,
			DatasetService datasetService,
			GroupService groupService,
			UserTransformer transformer,
			SearchResultTransformer searchResultTransformer,
			TaskExecutor taskExecutor) {
		this.userEntityService = userEntityService;
		this.userEntryService = userEntryService;
		this.datasetService = datasetService;
		this.groupService = groupService;
		this.transformer = transformer;
		this.searchResultTransformer = searchResultTransformer;
		this.taskExecutor = taskExecutor;
	}

	@Override
	public List<SearchResult> search(String query, Map<String, Set<String>> groupFilter, Set<String> datasetFilter,
									 boolean includeInactiveUsers, Integer page, Integer pageSize) {
		val queryIsEmpty = query == null || query.length() < 3;
		val groupFilterIsEmpty = groupFilter.values().stream().allMatch(Set::isEmpty);
		if (queryIsEmpty && groupFilterIsEmpty && datasetFilter.isEmpty()) {
			// not enough criteria to do a useful search
			return emptyList();
		}

		// We only need to filter on datasets for non-national (local) users, so don't bother fetching them for national users
		if (!isNational()) {
			val myDatasets = myDatasets();

			if (datasetFilter.isEmpty()) {
				datasetFilter.addAll(myDatasets);    // Filter any search results using the current user's datasets
			} else {
				datasetFilter.retainAll(myDatasets); // Remove any datasets that the user is not allowed to access
				if (datasetFilter.isEmpty()) {
					log.debug("User cannot access any of the datasets they are attempting to filter on");
					return emptyList();
				}
			}
		}

		// Create a Future to fetch the members of all the groups that are being filtered on
		val groupMembersFuture = supplyAsync(() -> groupFilter.keySet().parallelStream()
				.flatMap(type -> groupFilter.get(type).parallelStream()
						.map(name -> groupService.getGroup(type, name)))
				.flatMap(Optionals::toStream)
				.flatMap(group -> group.getMembers().stream())
				.map(name -> LdapUtils.getStringValue(name, "cn").toLowerCase())
				.collect(toSet()), taskExecutor);

		// Create Futures to search the LDAP and Database asynchronously
		val dbFuture = supplyAsync(() -> userEntityService.search(query, includeInactiveUsers, datasetFilter), taskExecutor);
		val ldapFuture = supplyAsync(() -> userEntryService.search(query, includeInactiveUsers, datasetFilter), taskExecutor);

		try {
			// fetch and map
			var stream = allOf(groupMembersFuture, ldapFuture, dbFuture)
					.thenApply(v -> Stream.of(ldapFuture.join(), dbFuture.join())).join()
					.flatMap(Collection::stream)
					.flatMap(sr -> expandEmailSearchToDB(sr, query))
					.collect(groupingBy(SearchResult::getUsername)).values().stream()
					.map(l -> l.stream().reduce(searchResultTransformer::reduce))
					.flatMap(Optionals::toStream);

			// apply conditional filters
			if (!includeInactiveUsers) {
				stream = stream.filter(result -> result.getEndDate() == null || !result.getEndDate().isBefore(now()));
			}
			if (!groupFilterIsEmpty) {
				stream = stream.filter(result -> groupMembersFuture.join().contains(result.getUsername().toLowerCase()));
			}

			// apply sorting and paging
			stream = stream
				.sorted(queryIsEmpty ?
						comparing(SearchResult::getUsername, String::compareToIgnoreCase) :
						comparing(SearchResult::getScore, Float::compare).reversed());
			if (page != null && pageSize != null)
			{
				stream = stream
						.skip((long) (page - 1) * pageSize)
						.limit(pageSize);
			}
			return	stream
				.peek(result -> log.debug("SearchResult: username={}, score={}, endDate={}, email={}", result.getUsername(), result.getScore(), result.getEndDate(), result.getEmail()))
				.collect(toList());
		} catch (CancellationException | CompletionException e) {
			throw new AppException(String.format("Unable to complete user search for %s", query), e);
		}
	}

	@Override
	public boolean usernameExists(String username) {
		val dbFuture = supplyAsync(() -> userEntityService.usernameExists(username), taskExecutor);
		val ldapFuture = supplyAsync(() -> userEntryService.usernameExists(username), taskExecutor);

		try {
			return allOf(dbFuture, ldapFuture)
					.thenApply(v -> dbFuture.join() || ldapFuture.join())
					.join();
		} catch (CancellationException | CompletionException e) {
			throw new AppException(String.format("Unable to check whether user exists with username %s", username), e);
		}
	}

	@Override
	public Optional<User> getUser(String username) {
		val dbFuture = supplyAsync(() -> userEntityService.getUser(username).orElse(null), taskExecutor);
		val ldapFuture = supplyAsync(() -> userEntryService.getUser(username).orElse(null), taskExecutor);

		try {
			val datasetsFilter = datasetsFilter();
			return allOf(dbFuture, ldapFuture)
					.thenApply(v -> transformer.combine(dbFuture.join(), ldapFuture.join()))
					.join()
					.filter(user -> datasetsFilter.test(user.getUsername()));
		} catch (CancellationException | CompletionException e) {
			throw new AppException(String.format("Unable to retrieve user details for %s", username), e);
		}
	}

	@Override
	public Optional<User> getUserByStaffCode(String staffCode) {
		return userEntityService.getUserByStaffCode(staffCode).flatMap(transformer::map);
	}

	@Override
	public void addUser(User user) {
		val dbFuture = runAsync(() -> userEntityService.save(transformer.mapToUserEntity(user, new UserEntity())), taskExecutor);
		val ldapFuture = runAsync(() -> userEntryService.save(transformer.mapToUserEntry(user, new UserEntry())), taskExecutor);

		try {
			allOf(dbFuture, ldapFuture).join();
		} catch (CancellationException | CompletionException e) {
			throw new AppException(String.format("Unable to create user (%s)", getMostSpecificCause(e).getMessage()), e);
		}
	}

	@Override
	public void updateUser(User user) {
		val existingHomeArea = userEntryService.getUserHomeArea(user.getExistingUsername());
		val dbFuture = runAsync(() -> {
			log.debug("Fetching existing DB value");
			val existingUser = userEntityService.getUser(user.getExistingUsername()).orElse(new UserEntity());
			log.debug("Transforming into DB user");
			val updatedUser = transformer.mapToUserEntity(user, existingUser, existingHomeArea);
			userEntityService.save(updatedUser);
		}, taskExecutor);
		val ldapFuture = runAsync(() -> {
			log.debug("Fetching existing LDAP value");
			val existingUser = userEntryService.getUser(user.getExistingUsername()).orElse(new UserEntry());
			log.debug("Transforming into LDAP user");
			val updatedUser = transformer.mapToUserEntry(user, existingUser);
			userEntryService.save(user.getExistingUsername(), updatedUser);
		}, taskExecutor);

		try {
			allOf(dbFuture, ldapFuture).join();
		} catch (CancellationException | CompletionException e) {
			throw new AppException(String.format("Unable to update user (%s)", getMostSpecificCause(e).getMessage()), e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Stream<ExportResult> exportAll() {
		log.debug("User export started");
		// Fetch all LDAP entries into memory
		val ldapUsers = userEntryService.export();
		log.debug("Fetched {} entries from LDAP", ldapUsers.size());
		// Stream all user entities from the DB, and combine each one with the corresponding LDAP entry
		val username = new String[]{""};
		return userEntityService.export()
				.map(entity -> transformer.combine(entity, ldapUsers.get(entity.getUsername())))
				.filter(Objects::nonNull)
				.filter(result -> {
					// Remove any repeated entries caused by cartesian product queries, without traversing the entire stream
					val isDuplicate = result.getUsername().equals(username[0]);
					username[0] = result.getUsername();
					return !isDuplicate;
				});
	}

	@Override
	@Bulkhead(name = "export")
	@Transactional(readOnly = true)
	public void exportAllToCsv(OutputStream outputStream) {
		try (val writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
			CSVUtils.stream(exportAll(), writer);
		} catch (IOException e) {
			throw new AppException("Unable to stream CSV data", e);
		}
	}

	private Predicate<String> datasetsFilter() {
		if (isNational()) return (String username) -> true;

		val myDatasets = myDatasets();

		return (String username) -> {
			val t = LocalDateTime.now();
			val theirDatasets = datasetService.getDatasetCodes(username);
			val theirHomeArea = userEntryService.getUserHomeArea(username);
			if (theirHomeArea != null) theirDatasets.add(theirHomeArea);
			val r = theirDatasets.isEmpty() || myDatasets.stream().anyMatch(theirDatasets::contains);
			log.trace("--{}ms	Dataset filter", MILLIS.between(t, LocalDateTime.now()));
			return r;
		};
	}

	private Set<String> myDatasets() {
		val myDatasets = datasetService.getDatasetCodes(myUsername());
		myDatasets.add(userEntryService.getUserHomeArea(myUsername()));
		return myDatasets;
	}


	private Stream<SearchResult> expandEmailSearchToDB(SearchResult sr, String query)
	{
		List<SearchResult> results = new ArrayList<>();

		for (String token : query.trim().split("\\s+"))
		{
			// Search the database to obtain staff and team records if the token is a substring of the search result's email
			if (sr.getSources().contains("LDAP") && sr.getEmail() != null && sr.getEmail().contains(token))
			{
				Optional<UserEntity> userEntity = userEntityService.getUser(sr.getUsername());
				userEntity.ifPresent(entity -> results.add(searchResultTransformer.map(entity)));
			}
		}

		results.add(sr);
		return results.stream();
	}
}
