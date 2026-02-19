package com.luisgosampaio.adventura.domain.trip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CountryCodeMapper {

    private static final Map<String, String> NAME_TO_CODE = new HashMap<>();

    private static final Map<String, String> ALIASES = Map.ofEntries(
            Map.entry("czech republic", "cz"),
            Map.entry("south korea", "kr"),
            Map.entry("north korea", "kp"),
            Map.entry("united states", "us"),
            Map.entry("usa", "us"),
            Map.entry("uk", "gb"),
            Map.entry("united kingdom", "gb"),
            Map.entry("russia", "ru"),
            Map.entry("taiwan", "tw"),
            Map.entry("vietnam", "vn"),
            Map.entry("ivory coast", "ci"),
            Map.entry("palestine", "ps"),
            Map.entry("bolivia", "bo"),
            Map.entry("venezuela", "ve"),
            Map.entry("iran", "ir"),
            Map.entry("syria", "sy"),
            Map.entry("tanzania", "tz"),
            Map.entry("laos", "la"),
            Map.entry("brunei", "bn"),
            Map.entry("east timor", "tl")
    );

    static {
        for (String isoCode : Locale.getISOCountries()) {
            Locale locale = new Locale("", isoCode);
            String countryName = locale.getDisplayCountry(Locale.ENGLISH).toLowerCase();
            NAME_TO_CODE.put(countryName, isoCode.toLowerCase());
        }
        NAME_TO_CODE.putAll(ALIASES);
    }

    public static String toCode(String countryName) {
        if (countryName == null) return null;
        return NAME_TO_CODE.get(countryName.trim().toLowerCase());
    }

    public static List<String> toCodes(List<String> countryNames) {
        return new ArrayList<>(countryNames.stream()
                .map(CountryCodeMapper::toCode)
                .toList());
    }
}
