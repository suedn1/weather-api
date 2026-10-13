package com.personal.weather.clients;

import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import com.personal.weather.dto.NominatimResponseDTO;

public interface NominatimClient {

    @GetExchange ("/search")
    List<NominatimResponseDTO> search(
        @RequestParam ("postalcode") String postalCode,
        @RequestParam("format") String format
    );
    
}