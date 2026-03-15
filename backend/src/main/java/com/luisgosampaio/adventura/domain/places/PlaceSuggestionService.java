package com.luisgosampaio.adventura.domain.places;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
public class PlaceSuggestionService {

    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "nvidia/nemotron-3-super-120b-a12b:free";

    private final String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public PlaceSuggestionService(@Value("${openrouter.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    public PlaceSuggestionResponse suggest(String query) {
        String prompt = """
                Given the search query "%s", return a JSON object with exactly these fields:
                {
                  "name": "<canonical place name, e.g. Ferrari World Yas Island, Abu Dhabi>",
                  "description": "<2-3 sentences about the place>",
                  "lat": <latitude as number>,
                  "lon": <longitude as number>,
                  "rating": <number 1-5 or null>,
                  "openingTime": "<HH:mm or null>",
                  "closingTime": "<HH:mm or null>"
                }
                Return ONLY the JSON object with no markdown fences or extra text.
                """.formatted(query);

        String requestBody = """
                {
                  "model": "%s",
                  "messages": [{"role": "user", "content": %s}]
                }
                """.formatted(MODEL, mapper.valueToTree(prompt).toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(OPENROUTER_URL, HttpMethod.POST, entity, String.class);

        try {
            JsonNode root = mapper.readTree(response.getBody());
            String json = root
                    .path("choices").get(0)
                    .path("message").path("content").asText();

            json = json.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            return mapper.readValue(json, PlaceSuggestionResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OpenRouter response: " + e.getMessage(), e);
        }
    }
}
