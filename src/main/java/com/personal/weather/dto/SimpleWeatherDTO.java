package com.personal.weather.dto;

public record SimpleWeatherDTO (double temperature, String temperatureUnit) implements WeatherResponse {}
