package com.sapient.football.controller;

import com.sapient.football.model.TeamStats;
import com.sapient.football.services.FootballStatsImpl;
import com.sapient.football.services.FootballStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping(path = "/stats")
public class StatsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FootballStatsImpl.class);

    @Autowired
    private FootballStatsService footballStatsService;

    @GetMapping(path = "/getStats/{countryName}/{leagueName}/{teamName}", produces = "application/json")
    public TeamStats getStats(@PathVariable String countryName, @PathVariable String leagueName, @PathVariable String teamName) {
        LOGGER.info("fetching stats for {} {}  {}", countryName, leagueName, teamName);
        TeamStats teamStats = footballStatsService.getStatsFromAPi(countryName, leagueName, teamName);
        if (Objects.isNull(teamStats)) {
            TeamStats msg = new TeamStats();
            msg.setMessage("FAILURE. Please validate your input. unable to find records.");
            return msg;
        } else {
            teamStats.setMessage("SUCCESS");
        }
        return teamStats;
    }

}
