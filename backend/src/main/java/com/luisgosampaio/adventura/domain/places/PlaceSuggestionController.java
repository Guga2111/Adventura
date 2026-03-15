package com.luisgosampaio.adventura.domain.places;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/api/places")
public class PlaceSuggestionController {

    private static final Logger log = LoggerFactory.getLogger(PlaceSuggestionController.class);

    private final PlaceSuggestionService service;

    public PlaceSuggestionController(PlaceSuggestionService service) {
        this.service = service;
    }

    @PostMapping("/suggest")
    public ResponseEntity<PlaceSuggestionResponse> suggest(@RequestBody PlaceSuggestionRequest req) {
        if (req.query() == null || req.query().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(service.suggest(req.query()));
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("OpenRouter rate limit hit: {}", e.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        } catch (Exception e) {
            log.error("Place suggestion failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}
