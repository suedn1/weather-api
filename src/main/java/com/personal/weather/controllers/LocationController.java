package com.personal.weather.controllers;

import com.personal.weather.services.LocationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

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
