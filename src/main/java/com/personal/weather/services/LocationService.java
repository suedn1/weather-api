package com.personal.weather.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.personal.weather.clients.NominatimClient;
import com.personal.weather.clients.OpenMeteoClient;
import com.personal.weather.dto.ExtendedWeatherDTO;
import com.personal.weather.dto.NominatimResponseDTO;
import com.personal.weather.dto.OpenMeteoClimateResponseDTO;
import com.personal.weather.dto.OpenMeteoResponseDTO;
import com.personal.weather.dto.SimpleWeatherDTO;
import com.personal.weather.dto.WeatherResponse;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class LocationService {

    private static final String CURRENT_FIELDS = "temperature_2m,relative_humidity_2m,apparent_temperature,wind_speed_10m";
    private static final String DAILY_FIELDS = "temperature_2m_min,temperature_2m_max";

    @Autowired
    private NominatimClient nominatimClient;

    @Autowired
    private OpenMeteoClient openMeteoClient;

    private List<NominatimResponseDTO> getGeolocationByZipCode(String zipCode) {
        try {
            List<NominatimResponseDTO> nominatimResponse = nominatimClient.search(zipCode, "json");

            return nominatimResponse;
        } catch (Exception e) {
            throw new RuntimeException("GEOLOCATION_API_FAILED");
        }
    }

    private OpenMeteoResponseDTO getCurrentTemperatureByGeolocationCoordinates(String latitude, String longitude) {
        try {
            return openMeteoClient.getCurrentWeather(latitude, longitude, CURRENT_FIELDS);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("OPEN_METEO_API_FAILED");
        }
    }

    private OpenMeteoClimateResponseDTO getAdditionalWeatherDetailsByGeolocationCoordinates(String latitude, String longitude) {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try {
            return openMeteoClient.getDailyForecast(latitude, longitude, today, today, DAILY_FIELDS);
            
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
