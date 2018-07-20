package uk.co.bconline.ndelius.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.Team;
import uk.co.bconline.ndelius.model.entity.TeamEntity;
import uk.co.bconline.ndelius.repository.db.TeamRepository;
import uk.co.bconline.ndelius.service.TeamService;
import uk.co.bconline.ndelius.transformer.TeamTransformer;

import java.util.List;

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
	public List<Team> getTeams(String probationArea)
	{
		List<TeamEntity> teams = null;

		if (probationArea == null) {
			teams = repository.findAllByEndDateIsNull();
		}
		else
		{
			teams = repository.findAllByEndDateIsNullAndProbationAreaCode(probationArea);
		}
		return transformer.map(teams);
	}
}
