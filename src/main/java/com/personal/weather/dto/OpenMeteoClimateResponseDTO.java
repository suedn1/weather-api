package com.personal.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record OpenMeteoClimateResponseDTO(
        Daily daily,
        @JsonProperty("daily_units") DailyUnits dailyUnits
) implements Serializable {}

record Daily (
        @JsonProperty("temperature_2m_min") double[] temperatureMin,
        @JsonProperty("temperature_2m_max") double[] temperatureMax
) implements Serializable {}

record DailyUnits (
        @JsonProperty("temperature_2m_min") String temperatureMin,
        @JsonProperty("temperature_2m_max") String temperatureMax
) implements Serializable {}