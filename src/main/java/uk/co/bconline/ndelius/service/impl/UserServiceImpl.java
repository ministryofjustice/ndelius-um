package uk.co.bconline.ndelius.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.co.bconline.ndelius.exception.AppException;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.model.entry.UserEntry;
import uk.co.bconline.ndelius.service.DatasetService;
import uk.co.bconline.ndelius.service.UserEntityService;
import uk.co.bconline.ndelius.service.UserEntryService;
import uk.co.bconline.ndelius.service.UserService;
import uk.co.bconline.ndelius.transformer.SearchResultTransformer;
import uk.co.bconline.ndelius.transformer.UserTransformer;

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
import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.*;
import static java.util.stream.Collectors.toList;
import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;
import static uk.co.bconline.ndelius.util.AuthUtils.isNational;
import static uk.co.bconline.ndelius.util.AuthUtils.myUsername;

@Slf4j
@Service
public class UserServiceImpl implements UserService
{
	private final UserEntityService userEntityService;
	private final UserEntryService userEntryService;
	private final DatasetService datasetService;
	private final UserTransformer transformer;
	private final SearchResultTransformer searchResultTransformer;

	@Autowired
	public UserServiceImpl(
			UserEntityService userEntityService,
			UserEntryService userEntryService,
			DatasetService datasetService,
			UserTransformer transformer,
			SearchResultTransformer searchResultTransformer)
	{
		this.userEntityService = userEntityService;
		this.userEntryService = userEntryService;
		this.datasetService = datasetService;
		this.transformer = transformer;
		this.searchResultTransformer = searchResultTransformer;
	}

	@Override
	public List<SearchResult> search(String query, int page, int pageSize, boolean includeInactiveUsers)
	{
		if (StringUtils.isEmpty(query) || query.length() < 3) return emptyList();

		Set<String> myDatasets = new HashSet<>();
		if (!isNational()) {
			// We only need to filter on datasets for non-national (local) users, so don't bother fetching them for national users
			myDatasets.addAll(datasetService.getDatasetCodes(myUsername()));
			myDatasets.add(userEntryService.getUserHomeArea(myUsername()));
		}

		val dbFuture = supplyAsync(() -> userEntityService.search(query, includeInactiveUsers, myDatasets));
		val ldapFuture = supplyAsync(() -> userEntryService.search(query, includeInactiveUsers, myDatasets));

		try
		{
			return allOf(ldapFuture, dbFuture)
					.thenApply(v -> Stream.of(ldapFuture.join(), dbFuture.join())
							.flatMap(Collection::stream)
							.collect(HashMap<String, SearchResult>::new, (map, result) -> {
								map.put(result.getUsername(), ofNullable(map.get(result.getUsername()))
										.map(r -> searchResultTransformer.reduce(r, result))
										.orElse(result));
							}, HashMap::putAll)
							.values()
							.stream()
							.filter(result -> includeInactiveUsers || result.getEndDate() == null || !result.getEndDate().isBefore(now()))
							.sorted(comparing(SearchResult::getScore, Float::compare).reversed())
							.skip((long) (page-1) * pageSize)
							.limit(pageSize)
							.peek(result -> log.debug("SearchResult: username={}, score={}, endDate={}", result.getUsername(), result.getScore(), result.getEndDate()))
							.collect(toList()))
					.join();
		}
		catch (CancellationException | CompletionException e)
		{
			throw new AppException(String.format("Unable to complete user search for %s", query), e);
		}
	}

	@Override
	public boolean usernameExists(String username)
	{
		val dbFuture = supplyAsync(() -> userEntityService.usernameExists(username));
		val ldapFuture = supplyAsync(() -> userEntryService.usernameExists(username));

		try
		{
			return allOf(dbFuture, ldapFuture)
					.thenApply(v -> dbFuture.join() || ldapFuture.join())
					.join();
		}
		catch (CancellationException | CompletionException e)
		{
			throw new AppException(String.format("Unable to check whether user exists with username %s", username), e);
		}
	}

	@Override
	public Optional<User> getUser(String username)
	{
		val dbFuture = supplyAsync(() -> userEntityService.getUser(username).orElse(null));
		val ldapFuture = supplyAsync(() -> userEntryService.getUser(username).orElse(null));

		try
		{
			val datasetsFilter = datasetsFilter();
			return allOf(dbFuture, ldapFuture)
					.thenApply(v -> transformer.combine(dbFuture.join(), ldapFuture.join()))
					.join()
					.filter(user -> datasetsFilter.test(user.getUsername()));
		}
		catch (CancellationException | CompletionException e)
		{
			throw new AppException(String.format("Unable to retrieve user details for %s", username), e);
		}
	}

	@Override
	public Optional<User> getUserByStaffCode(String staffCode)
	{
		return userEntityService.getUserByStaffCode(staffCode).flatMap(transformer::map);
	}

	@Override
	public void addUser(User user)
	{
		val dbFuture = runAsync(() -> userEntityService.save(transformer.mapToUserEntity(user, new UserEntity())));
		val ldapFuture = runAsync(() -> userEntryService.save(transformer.mapToUserEntry(user, new UserEntry())));

		try
		{
			allOf(dbFuture, ldapFuture).join();
		}
		catch (CancellationException | CompletionException e)
		{
			throw new AppException(String.format("Unable to create user (%s)", getMostSpecificCause(e).getMessage()), e);
		}
	}

	@Override
	public void updateUser(User user)
	{
		val dbFuture = runAsync(() -> {
			log.debug("Fetching existing DB value");
			val existingUser = userEntityService.getUser(user.getExistingUsername()).orElse(new UserEntity());
			log.debug("Transforming into DB user");
			val updatedUser = transformer.mapToUserEntity(user, existingUser);
			userEntityService.save(updatedUser);
		});
		val ldapFuture = runAsync(() -> {
			log.debug("Fetching existing LDAP value");
			val existingUser = userEntryService.getUser(user.getExistingUsername()).orElse(new UserEntry());
			log.debug("Transforming into LDAP user");
			val updatedUser = transformer.mapToUserEntry(user, existingUser);
			userEntryService.save(user.getExistingUsername(), updatedUser);
		});

		try
		{
			allOf(dbFuture, ldapFuture).join();
		}
		catch (CancellationException | CompletionException e)
		{
			throw new AppException(String.format("Unable to update user (%s)", getMostSpecificCause(e).getMessage()), e);
		}
	}

	private Predicate<String> datasetsFilter()
	{
		if (isNational()) return (String username) -> true;

		val myDatasets = datasetService.getDatasetCodes(myUsername());
		myDatasets.add(userEntryService.getUserHomeArea(myUsername()));

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
}
