package uk.co.bconline.ndelius.service.impl;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.val;
import uk.co.bconline.ndelius.exception.AppException;
import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.UserService;
import uk.co.bconline.ndelius.transformer.UserTransformer;

@Service
public class UserServiceImpl implements UserService
{
	private final DBUserDetailsService dbService;
	private final OIDUserDetailsService oidService;
	private final Optional<AD1UserDetailsService> ad1Service;
	private final Optional<AD2UserDetailsService> ad2Service;
	private final UserTransformer transformer;

	@Autowired
	public UserServiceImpl(
			DBUserDetailsService dbService,
			OIDUserDetailsService oidService,
			Optional<AD1UserDetailsService> ad1Service,
			Optional<AD2UserDetailsService> ad2Service,
			UserTransformer transformer)
	{
		this.dbService = dbService;
		this.oidService = oidService;
		this.ad1Service = ad1Service;
		this.ad2Service = ad2Service;
		this.transformer = transformer;
	}

	@Override
	public List<SearchResult> search(String query, int page, int pageSize)
	{
		val me = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		val myDatasets = dbService.getDatasets(me).stream().map(Dataset::getCode).collect(toList());

		return dbService.search(query).stream()
				.filter(user -> myDatasets.contains(oidService.getUserHomeArea(user.getUsername())))
				.skip((long) (page-1) * pageSize)
				.limit(pageSize)
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
			return CompletableFuture.allOf(dbFuture, oidFuture, ad1Future, ad2Future)
					.thenApply(v -> transformer.combine(dbFuture.join(), oidFuture.join(), ad1Future.join(), ad2Future.join()))
					.get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			throw new AppException(String.format("Error occurred retrieving user details for %s", username), e);
		}
	}
}
