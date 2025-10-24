package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.Team;

import java.util.List;
import java.util.Optional;

public interface TeamService {
    List<Team> getTeams(String probationArea);

    Optional<Long> getTeamId(String code);
}
