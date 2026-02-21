package com.luisgosampaio.adventura.domain.trip;

public record UnsplashPhotoData(
        String imageUrl,
        String authorName,
        String authorUrl,
        String downloadLocation
) {}
