package com.personal.weather.services;

import com.personal.weather.dto.WeatherResponse;

import jakarta.servlet.http.HttpServletResponse;

public interface LocationService {

    WeatherResponse getWeatherInformationByZipCode(String zipCode, boolean extendedDetails, HttpServletResponse response);
}
