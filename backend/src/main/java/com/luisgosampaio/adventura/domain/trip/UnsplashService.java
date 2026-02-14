package com.luisgosampaio.adventura.domain.trip;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UnsplashService {

    private static final Logger log = LoggerFactory.getLogger(UnsplashService.class);

    private final RestClient restClient;
    private final String accessKey;

    public UnsplashService(@Value("${unsplash.access-key}") String accessKey) {
        this.accessKey = accessKey;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.unsplash.com")
                .build();
    }

    public String fetchCoverImageUrl(String destination) {
        try {
            JsonNode response = restClient.get()
                    .uri("/search/photos?query={query}&per_page=1", destination)
                    .header("Authorization", "Client-ID " + accessKey)
                    .retrieve()
                    .body(JsonNode.class);

            if (response != null && response.has("results") && !response.get("results").isEmpty()) {
                return response.get("results").get(0).get("urls").get("regular").asText();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch cover image from Unsplash for '{}': {}", destination, e.getMessage());
        }
        return null;
    }
}
