package uk.co.bconline.ndelius.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.Team;
import uk.co.bconline.ndelius.model.entity.TeamEntity;
import uk.co.bconline.ndelius.repository.db.TeamRepository;
import uk.co.bconline.ndelius.service.TeamService;
import uk.co.bconline.ndelius.transformer.TeamTransformer;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class TeamServiceImpl implements TeamService
{
	private final TeamTransformer transformer;
	private final TeamRepository repository;

	@Autowired
	public TeamServiceImpl(
			TeamRepository repository,
			TeamTransformer transformer)
	{
		this.repository = repository;
		this.transformer = transformer;
	}

	@Override
	@Cacheable("teamsByProbationArea")
	public List<Team> getTeams(String probationArea)
	{
		List<TeamEntity> teams = ofNullable(probationArea)
				.map(repository::findAllByEndDateIsNullAndProbationAreaCode)
				.orElse(repository.findAllByEndDateIsNull());

		return transformer.map(teams);
	}

	@Override
	@Cacheable("teamIdsByCode")
	public Optional<Long> getTeamId(String code) {
		return repository.findIdByCode(code);
	}
}
