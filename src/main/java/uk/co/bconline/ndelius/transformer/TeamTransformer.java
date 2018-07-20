package uk.co.bconline.ndelius.transformer;

import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.Team;
import uk.co.bconline.ndelius.model.entity.TeamEntity;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class TeamTransformer {

	public List<Team> map(List<TeamEntity> teams)
	{
		return teams.stream()
				.map(team -> Team.builder()
						.code(team.getCode())
						.description(team.getDescription())
						.build())
				.collect(toList());
	}
}
