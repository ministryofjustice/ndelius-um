package uk.co.bconline.ndelius.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.co.bconline.ndelius.exception.AppException;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.model.ldap.ADUser;
import uk.co.bconline.ndelius.model.ldap.OIDUser;
import uk.co.bconline.ndelius.service.DBUserService;
import uk.co.bconline.ndelius.service.DatasetService;
import uk.co.bconline.ndelius.service.UserService;
import uk.co.bconline.ndelius.transformer.UserTransformer;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.concurrent.CompletableFuture.*;
import static java.util.stream.Collectors.toList;
import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;

@Slf4j
@Service
public class UserServiceImpl implements UserService
{
	@Value("${ad.primary.readonly:false}")
	private boolean ad1IsReadonly;

	@Value("${ad.secondary.readonly:false}")
	private boolean ad2IsReadonly;

	private final DBUserService dbService;
	private final OIDUserDetailsService oidService;
	private final Optional<AD1UserDetailsService> ad1Service;
	private final Optional<AD2UserDetailsService> ad2Service;
	private final DatasetService datasetService;
	private final UserTransformer transformer;

	@Autowired
	public UserServiceImpl(
			DBUserService dbService,
			OIDUserDetailsService oidService,
			Optional<AD1UserDetailsService> ad1Service,
			Optional<AD2UserDetailsService> ad2Service,
			DatasetService datasetService,
			UserTransformer transformer)
	{
		this.dbService = dbService;
		this.oidService = oidService;
		this.ad1Service = ad1Service;
		this.ad2Service = ad2Service;
		this.datasetService = datasetService;
		this.transformer = transformer;
	}

	@Override
	public List<SearchResult> search(String query, int page, int pageSize, boolean includeInactiveUsers)
	{
		if (StringUtils.isEmpty(query) || query.length() < 3) return emptyList();

		val dbFuture = supplyAsync(() -> dbService.search(query, includeInactiveUsers));
		val oidFuture = supplyAsync(() -> oidService.search(query));
		val ad1Future = supplyAsync(() -> ad1Service.map(service -> service.search(query)).orElse(emptyList()));
		val ad2Future = supplyAsync(() -> ad2Service.map(service -> service.search(query)).orElse(emptyList()));
		Set<SearchResult> foundUsers;
		try
		{
			foundUsers = allOf(dbFuture, oidFuture, ad1Future, ad2Future)
					.thenApply(v -> {
						Set<SearchResult> results = new HashSet<>();
						results.addAll(dbFuture.join());
						results.addAll(oidFuture.join());
						results.addAll(ad1Future.join());
						results.addAll(ad2Future.join());
						return results;
					}).get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			throw new AppException(String.format("Unable to complete user search for %s", query), e);
		}

		val datasetsFilter = datasetsFilter();
		val t = LocalDateTime.now();
		val r = foundUsers.stream()
				.sorted(comparing(SearchResult::getScore, Float::compare).reversed())
				.filter(result -> datasetsFilter.test(result.getUsername()))
				.filter(result -> includeInactiveUsers || result.getEndDate() == null || !result.getEndDate().isBefore(now()))
				.peek(result -> log.debug("SearchResult: username={}, score={}", result.getUsername(), result.getScore()))
				.skip((long) (page-1) * pageSize)
				.limit(pageSize)
				.map(SearchResult::getUsername)
				.map(this::getSearchResult)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(toList());
		log.debug("{}ms	Lookup each result", MILLIS.between(t, LocalDateTime.now()));
		return r;
	}

	public Optional<SearchResult> getSearchResult(String username)
	{
		val dbFuture = supplyAsync(() -> dbService.getUser(username).orElse(null));
		val oidFuture = supplyAsync(() -> oidService.getBasicUser(username).orElse(null));
		val ad1Future = supplyAsync(() -> ad1Service.flatMap(service -> service.getUser(username)).orElse(null));
		val ad2Future = supplyAsync(() -> ad2Service.flatMap(service -> service.getUser(username)).orElse(null));

		try
		{
			return allOf(dbFuture, oidFuture, ad1Future, ad2Future)
					.thenApply(v -> transformer.mapToSearchResult(
							dbFuture.join(), oidFuture.join(), ad1Future.join(), ad2Future.join())).get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			throw new AppException(String.format("Unable to retrieve user details for %s", username), e);
		}
	}

	@Override
	public boolean usernameExists(String username)
	{
		val dbFuture = supplyAsync(() -> dbService.usernameExists(username));
		val oidFuture = supplyAsync(() -> oidService.usernameExists(username));
		val ad1Future = supplyAsync(() -> ad1Service.map(service -> service.usernameExists(username)).orElse(false));
		val ad2Future = supplyAsync(() -> ad2Service.map(service -> service.usernameExists(username)).orElse(false));

		try
		{
			return allOf(dbFuture, oidFuture, ad1Future, ad2Future)
					.thenApply(v -> dbFuture.join() || oidFuture.join() || ad1Future.join() || ad2Future.join()).get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			throw new AppException(String.format("Unable to check whether user exists with username %s", username), e);
		}
	}

	@Override
	public Optional<User> getUser(String username)
	{
		val dbFuture = supplyAsync(() -> dbService.getUser(username).orElse(null));
		val oidFuture = supplyAsync(() -> oidService.getUser(username).orElse(null));
		val ad1Future = supplyAsync(() -> ad1Service.flatMap(service -> service.getUser(username)).orElse(null));
		val ad2Future = supplyAsync(() -> ad2Service.flatMap(service -> service.getUser(username)).orElse(null));

		try
		{
			val datasetsFilter = datasetsFilter();
			return allOf(dbFuture, oidFuture, ad1Future, ad2Future)
					.thenApply(v -> transformer.combine(dbFuture.join(), oidFuture.join(), ad1Future.join(), ad2Future.join())).get()
					.filter(user -> datasetsFilter.test(user.getUsername()));
		}
		catch (InterruptedException | ExecutionException e)
		{
			throw new AppException(String.format("Unable to retrieve user details for %s", username), e);
		}
	}

	@Override
	public Optional<User> getUserByStaffCode(String staffCode)
	{
		return dbService.getUserByStaffCode(staffCode).flatMap(transformer::map);
	}

	@Override
	public void addUser(User user)
	{
		val dbFuture = runAsync(() -> dbService.save(transformer.mapToUserEntity(user, new UserEntity())));
		val oidFuture = runAsync(() -> oidService.save(transformer.mapToOIDUser(user, new OIDUser())));
		val ad1Future = runAsync(() -> ad1Service.ifPresent(service -> service.save(transformer.mapToAD1User(user, new ADUser()))));
		val ad2Future = runAsync(() -> ad2Service.ifPresent(service -> service.save(transformer.mapToAD2User(user, new ADUser()))));

		try
		{
			allOf(dbFuture, oidFuture, ad1Future, ad2Future).get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			throw new AppException(String.format("Unable to create user (%s)", getMostSpecificCause(e).getMessage()), e);
		}
	}

	@Override
	public void updateUser(User user)
	{
		val dbFuture = runAsync(() -> {
			log.debug("Fetching existing DB value");
			val existingUser = dbService.getUser(user.getExistingUsername()).orElse(new UserEntity());
			log.debug("Transforming into DB user");
			val updatedUser = transformer.mapToUserEntity(user, existingUser);
			dbService.save(updatedUser);
		});
		val oidFuture = runAsync(() -> {
			log.debug("Fetching existing OID value");
			val existingUser = oidService.getUser(user.getExistingUsername()).orElse(new OIDUser());
			log.debug("Transforming into OID user");
			val updatedUser = transformer.mapToOIDUser(user, existingUser);
			oidService.save(user.getExistingUsername(), updatedUser);
		});
		val ad1Future = runAsync(() -> {
			if (!ad1IsReadonly) ad1Service.ifPresent(service -> {
				val existingUser = service.getUser(user.getExistingUsername()).orElse(new ADUser());
				val updatedUser = transformer.mapToAD1User(user, existingUser);
				service.save(updatedUser);
			});
		});
		val ad2Future = runAsync(() -> {
			if (!ad2IsReadonly) ad2Service.ifPresent(service -> {
				val existingUser = service.getUser(user.getExistingUsername()).orElse(new ADUser());
				val updatedUser = transformer.mapToAD2User(user, existingUser);
				service.save(updatedUser);
			});
		});

		try
		{
			allOf(dbFuture, oidFuture, ad1Future, ad2Future).get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			throw new AppException(String.format("Unable to update user (%s)", getMostSpecificCause(e).getMessage()), e);
		}
	}

	private Predicate<String> datasetsFilter()
	{
		val me = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		val myDatasets = datasetService.getDatasetCodes(me);
		myDatasets.add(oidService.getUserHomeArea(me));

		return (String username) -> {
			val t = LocalDateTime.now();
			val theirDatasets = datasetService.getDatasetCodes(username);
			val theirHomeArea = oidService.getUserHomeArea(username);
			if (theirHomeArea != null) theirDatasets.add(oidService.getUserHomeArea(username));
			val r = theirDatasets.isEmpty() || myDatasets.stream().anyMatch(theirDatasets::contains);
			log.trace("--{}ms	Dataset filter", MILLIS.between(t, LocalDateTime.now()));
			return r;
		};
	}
}
