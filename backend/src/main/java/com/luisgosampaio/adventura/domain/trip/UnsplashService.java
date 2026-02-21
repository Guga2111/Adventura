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

    public UnsplashPhotoData fetchCoverPhoto(String destination) {
        try {
            JsonNode response = restClient.get()
                    .uri("/search/photos?query={query}&per_page=1", destination)
                    .header("Authorization", "Client-ID " + accessKey)
                    .retrieve()
                    .body(JsonNode.class);

            if (response != null && response.has("results") && !response.get("results").isEmpty()) {
                JsonNode photo = response.get("results").get(0);

                String imageUrl = photo.get("urls").get("regular").asText();
                String authorName = photo.get("user").get("name").asText();
                String authorUrl = photo.get("user").get("links").get("html").asText();
                String downloadLocation = photo.get("links").get("download_location").asText();

                triggerDownload(downloadLocation);

                return new UnsplashPhotoData(imageUrl, authorName, authorUrl, downloadLocation);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch cover image from Unsplash for '{}': {}", destination, e.getMessage());
        }
        return null;
    }

    private void triggerDownload(String downloadLocation) {
        try {
            restClient.get()
                    .uri(downloadLocation)
                    .header("Authorization", "Client-ID " + accessKey)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to trigger Unsplash download tracking: {}", e.getMessage());
        }
    }
}
