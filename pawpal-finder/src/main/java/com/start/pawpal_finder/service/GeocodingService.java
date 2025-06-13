package com.start.pawpal_finder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.pawpal_finder.dto.LatLng;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.api.key}")
    private String apiKey;

    public GeocodingService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public LatLng geocode(String address) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://maps.googleapis.com/maps/api/geocode/json")
                .queryParam("address", address)
                .queryParam("key", apiKey)
                .toUriString();

        try {
            String json = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(json);
            JsonNode results = root.path("results");
            if (results.isArray() && !results.isEmpty()) {
                JsonNode loc = results.get(0)
                        .path("geometry")
                        .path("location");
                double lat = loc.path("lat").asDouble();
                double lng = loc.path("lng").asDouble();
                return new LatLng(lat, lng);
            } else {
                throw new IllegalStateException("No geocoding result for: " + address);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to geocode address: " + address, e);
        }
    }
}
