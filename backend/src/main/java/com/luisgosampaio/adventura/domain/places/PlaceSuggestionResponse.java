package com.luisgosampaio.adventura.domain.places;

public record PlaceSuggestionResponse(
    String name,
    String description,
    double lat,
    double lon,
    Double rating,
    String openingTime,
    String closingTime
) {}
