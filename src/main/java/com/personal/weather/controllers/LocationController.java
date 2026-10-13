package com.personal.weather.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.personal.weather.services.LocationService;

import jakarta.servlet.http.HttpServletResponse;

@RestController()
@RequestMapping("/locations")
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/{zipCode}/weather")
    public ResponseEntity<?> getWeatherByZipCode(
            @PathVariable String zipCode,
            @RequestParam(defaultValue = "false") boolean extendedDetails,
            HttpServletResponse response
    ) {
        try {
            return ResponseEntity.ok(locationService.getWeatherInformationByZipCode(zipCode, extendedDetails, response));
        } catch (Exception e) {
            return switch (e.getMessage()) {
                case "ZIP_CODE_NOT_FOUND" -> ResponseEntity.status(404).body("Zip code not found: " + zipCode);
                case "GEOLOCATION_API_FAILED" ->
                        ResponseEntity.status(500).body("Failed to get geolocation information from API");
                case "OPEN_METEO_API_FAILED" ->
                        ResponseEntity.status(500).body("Failed to get weather information from API");
                default -> ResponseEntity.status(500).body(e.getMessage());
            };

        }
    }
}
