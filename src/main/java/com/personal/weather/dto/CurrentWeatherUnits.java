package com.personal.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record CurrentWeatherUnits (
        @JsonProperty("temperature_2m") String temperature,
        @JsonProperty("relative_humidity_2m") String relativeHumidity,
        @JsonProperty("apparent_temperature") String apparentTemperature,
        @JsonProperty("wind_speed_10m") String windSpeed
) implements Serializable {}
