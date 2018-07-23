package uk.co.bconline.ndelius.service;

import java.util.List;
import java.util.Optional;

import uk.co.bconline.ndelius.model.Team;

public interface TeamService
{
	List<Team> getTeams(String probationArea);
	Optional<Long> getTeamId(String code);
}
