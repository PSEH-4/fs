package com.sapient.football.services;

import com.sapient.football.model.TeamStats;

public interface FootballStatsService {

    TeamStats getStatsFromAPi(String countryName, String leagueName, String teamName);


}
