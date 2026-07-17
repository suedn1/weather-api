package com.personal.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record OpenMeteoResponseDTO (
    @JsonProperty("current") CurrentWeather weatherInformation,
    @JsonProperty("current_units") CurrentWeatherUnits weatherUnits
) implements Serializable {}

