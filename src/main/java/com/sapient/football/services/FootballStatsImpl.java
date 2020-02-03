package com.sapient.football.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapient.football.model.Country;
import com.sapient.football.model.League;
import com.sapient.football.model.TeamStats;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class FootballStatsImpl implements FootballStatsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FootballStatsImpl.class);

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public TeamStats getStatsFromAPi(String countryName, String leagueName, String teamName) {
        Integer countryid = getCountryIdFromCountryName(countryName);
        if (Objects.isNull(countryid)) {
            LOGGER.info("country not found{}", countryName);
            return null;
        }

        Integer leagueId = getLeagueIdUsingCountryIdAndLeagueName(countryid, leagueName);

        if (Objects.isNull(leagueId)) {
            LOGGER.info("league not found{}", leagueName);
            return null;
        }

        return getTeamStandingFromLeagueIdAndTeamName(leagueId, teamName);
    }

    private TeamStats getTeamStandingFromLeagueIdAndTeamName(Integer leagueId, String teamName) {
        String statsFromApi = getResultFromAPI(Action.GET_STANDING, String.valueOf(leagueId));

        List<TeamStats> teamStats = null;

        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            teamStats = Arrays.asList(objectMapper.readValue(statsFromApi, TeamStats[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (Objects.nonNull(teamStats)) {
            return teamStats.stream()
                    .filter(teamStat -> teamName.equalsIgnoreCase(teamStat.getTeam_name()))
                    .findFirst().get();
        }
        return null;
    }

    private Integer getLeagueIdUsingCountryIdAndLeagueName(Integer countryid, String leagueName) {
        String leaguesFromAPI = getResultFromAPI(Action.GET_LEAGUE, String.valueOf(countryid));

        List<League> leagues = null;
        try {
            leagues = Arrays.asList(objectMapper.readValue(leaguesFromAPI, League[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (Objects.nonNull(leagues)) {
            return leagues.stream()
                    .filter(league -> leagueName.equalsIgnoreCase(league.getLeague_name()))
                    .findAny().get().getLeague_id();
        }
        return null;
    }

    private Integer getCountryIdFromCountryName(String countryName) {
        String countriesFromAPI = getResultFromAPI(Action.GET_COUNTRIES, "");

        List<Country> countries = null;
        try {
            countries = Arrays.asList(objectMapper.readValue(countriesFromAPI, Country[].class));
            Country c1 = countries.get(0);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Optional<Country> countryOptional = null;
        if (Objects.nonNull(countries)) {
            return countries.stream()
                    .filter(country -> countryName.equalsIgnoreCase(country.getCountry_name()))
                    .findFirst().get().getCountry_id();
        }
        return null;
    }

    String getResultFromAPI(Action action, String params) {
        final String API_key = "9bb66184e0c8145384fd2cc0f7b914ada57b4e8fd2e4d6d586adcc27c257a978";
        StringBuffer url = new StringBuffer();

        url.append("https://apiv2.apifootball.com/")
                .append("?action=" + action.getAction());
        if (action.equals(Action.GET_LEAGUE)) {
            url.append("&country_id=" + params);
        }
        if (action.equals(Action.GET_STANDING)) {
            url.append("&league_id=" + params);
        }
        url.append("&APIkey=" + API_key);

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url.toString(), String.class);
        return result;
    }

    enum Action {
        GET_COUNTRIES("get_countries"),
        GET_LEAGUE("get_leagues"),
        GET_STANDING("get_standings");

        private String action;

        public String getAction() {
            return this.action;
        }

        private Action(String action) {
            this.action = action;
        }
    }
}
