package com.personal.weather.dto;

public record ExtendedWeatherDTO (
    SimpleWeatherDTO weatherInformation,
    OpenMeteoClimateResponseDTO climateAdditionalDetails
) implements WeatherResponse {}
