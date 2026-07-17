package com.personal.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record CurrentWeather(
        @JsonProperty("temperature_2m") double temperature,
        @JsonProperty("relative_humidity_2m") double relativeHumidity,
        @JsonProperty("apparent_temperature") double apparentTemperature,
        @JsonProperty("wind_speed_10m") double windSpeed
) implements Serializable {}
