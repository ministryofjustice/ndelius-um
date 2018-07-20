package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.Team;

import java.util.List;

public interface TeamService
{
	List<Team> getTeams(String probationArea);
}
