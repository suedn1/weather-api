package com.personal.weather.dto;


import java.io.Serializable;

public record NominatimResponseDTO(
        String lat,
        String lon
) implements Serializable {}
