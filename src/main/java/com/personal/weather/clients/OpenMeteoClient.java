package com.personal.weather.clients;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import com.personal.weather.dto.OpenMeteoClimateResponseDTO;
import com.personal.weather.dto.OpenMeteoResponseDTO;

public interface OpenMeteoClient {

    @GetExchange("/v1/forecast")
    OpenMeteoResponseDTO getCurrentWeather(
            @RequestParam("latitude") String latitude,
            @RequestParam("longitude") String longitude,
            @RequestParam("current") String current);

    @GetExchange("/v1/forecast")
    OpenMeteoClimateResponseDTO getDailyForecast(
            @RequestParam("latitude") String latitude,
            @RequestParam("longitude") String longitude,
            @RequestParam("start_date") String startDate,
            @RequestParam("end_date") String endDate,
            @RequestParam("daily") String daily);

}