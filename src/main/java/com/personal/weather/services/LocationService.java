package com.personal.weather.services;

import com.personal.weather.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class LocationService {

    @Autowired
    RestClient restClient;

    private List<NominatimResponseDTO> getGeolocationByZipCode(String zipCode) {
        try {
            List<NominatimResponseDTO> nominatimResponse = restClient
                    .get()
                    .uri("https://nominatim.openstreetmap.org/search?&postalcode=" + zipCode + "&format=json")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<NominatimResponseDTO>>() {
                    });

            return nominatimResponse;
        } catch (Exception e) {
            throw new RuntimeException("GEOLOCATION_API_FAILED");
        }
    }

    private OpenMeteoResponseDTO getCurrentTemperatureByGeolocationCoordinates(String latitude, String longitude) {
        try {
            return restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("api.open-meteo.com")
                            .path("/v1/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current", "temperature_2m,relative_humidity_2m,apparent_temperature,wind_speed_10m")
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<OpenMeteoResponseDTO>() {
                    });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("OPEN_METEO_API_FAILED");
        }
    }

    private OpenMeteoClimateResponseDTO getAdditionalWeatherDetailsByGeolocationCoordinates(String latitude, String longitude) {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try {
            return restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("api.open-meteo.com")
                            .path("/v1/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("start_date", today)
                            .queryParam("end_date", today)
                            .queryParam("daily", "temperature_2m_min,temperature_2m_max")
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<OpenMeteoClimateResponseDTO>() {
                    });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("OPEN_METEO_API_FAILED");
        }
    }

    @Cacheable(value = "weatherByZipCode", key = "#zipCode + '-' + #extendedDetails")
    public WeatherResponse getWeatherInformationByZipCode(String zipCode, boolean extendedDetails, HttpServletResponse response) {
        response.setHeader("X-Cache-Status", "MISS");

        List<NominatimResponseDTO> nominatimResponse = getGeolocationByZipCode(zipCode);
        if (nominatimResponse.isEmpty()) {
            throw new RuntimeException("ZIP_CODE_NOT_FOUND");
        }

        NominatimResponseDTO geolocationInformation = nominatimResponse.get(0);
        OpenMeteoResponseDTO weatherResponse = getCurrentTemperatureByGeolocationCoordinates(geolocationInformation.lat(), geolocationInformation.lon());
        SimpleWeatherDTO simpleWeatherDTO = new SimpleWeatherDTO(weatherResponse.weatherInformation().temperature(), weatherResponse.weatherUnits().temperature());

        if (!extendedDetails) {
            return simpleWeatherDTO;
        }

        OpenMeteoClimateResponseDTO weatherClimateResponse = getAdditionalWeatherDetailsByGeolocationCoordinates(geolocationInformation.lat(), geolocationInformation.lon());
        return new ExtendedWeatherDTO(
                simpleWeatherDTO,
                weatherClimateResponse
        );
    }
}
