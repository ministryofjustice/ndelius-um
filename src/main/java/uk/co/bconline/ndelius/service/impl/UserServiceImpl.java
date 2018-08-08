package uk.co.bconline.ndelius.service.impl;

import static java.util.concurrent.CompletableFuture.*;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.val;
import uk.co.bconline.ndelius.exception.AppException;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.model.ldap.ADUser;
import uk.co.bconline.ndelius.model.ldap.OIDUser;
import uk.co.bconline.ndelius.service.DatasetService;
import uk.co.bconline.ndelius.service.UserService;
import uk.co.bconline.ndelius.transformer.UserTransformer;

@Service
public class UserServiceImpl implements UserService
{
	private final DBUserDetailsService dbService;
	private final OIDUserDetailsService oidService;
	private final Optional<AD1UserDetailsService> ad1Service;
	private final Optional<AD2UserDetailsService> ad2Service;
	private final DatasetService datasetService;
	private final UserTransformer transformer;

	@Autowired
	public UserServiceImpl(
			DBUserDetailsService dbService,
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
	public List<SearchResult> search(String query, int page, int pageSize)
	{
		val datasetsFilter = datasetsFilter();
		return dbService.search(query).stream()
				.filter(userEntity -> datasetsFilter.test(userEntity.getUsername()))
				.skip((long) (page-1) * pageSize)
				.limit(pageSize)
				.map(UserEntity::getUsername)
				.map(this::getUser)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(transformer::map)
				.collect(toList());
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
			throw new AppException(String.format("Unable to create user (%s)", e.getMessage()), e);
		}
	}

	@Override
	public void updateUser(User user)
	{
		// TODO update AD
		val dbFuture = runAsync(() -> dbService.save(transformer.mapToUserEntity(user,
				dbService.getUser(user.getUsername()).orElse(new UserEntity()))));
		val oidFuture = runAsync(() -> oidService.save(transformer.mapToOIDUser(user,
				oidService.getUser(user.getUsername()).orElse(new OIDUser()))));
//		val ad1Future = runAsync(() -> ad1Service.ifPresent(service -> service.save(transformer.mapToAD1User(user, new ADUser()))));
//		val ad2Future = runAsync(() -> ad2Service.ifPresent(service -> service.save(transformer.mapToAD2User(user, new ADUser()))));

		try
		{
			allOf(dbFuture, oidFuture).get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			throw new AppException(String.format("Unable to update user (%s)", e.getMessage()), e);
		}
	}

	private Predicate<String> datasetsFilter()
	{
		val me = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		val myDatasets = datasetService.getDatasetCodes(me);
		myDatasets.add(oidService.getUserHomeArea(me));

		return (String username) -> {
			val theirDatasets = datasetService.getDatasetCodes(username);
			theirDatasets.add(oidService.getUserHomeArea(username));
			return myDatasets.stream().anyMatch(theirDatasets::contains);
		};
	}
}
