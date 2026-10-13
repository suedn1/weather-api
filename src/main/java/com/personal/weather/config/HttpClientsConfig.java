package com.personal.weather.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

import com.personal.weather.clients.NominatimClient;
import com.personal.weather.clients.OpenMeteoClient;

@Configuration
@ImportHttpServices (group = "nominatim", types = NominatimClient.class)
@ImportHttpServices (group = "open-meteo", types = OpenMeteoClient.class)
public class HttpClientsConfig {
    
}
