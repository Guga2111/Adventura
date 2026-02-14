package com.luisgosampaio.adventura.domain.trip;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CountryCodeMapper {

    private static final Map<String, String> NAME_TO_CODE = new HashMap<>();

    static {
        for (String isoCode : Locale.getISOCountries()) {
            Locale locale = new Locale("", isoCode);
            String countryName = locale.getDisplayCountry(Locale.ENGLISH).toLowerCase();
            NAME_TO_CODE.put(countryName, isoCode.toLowerCase());
        }
    }

    public static String toCode(String countryName) {
        if (countryName == null) return null;
        return NAME_TO_CODE.get(countryName.trim().toLowerCase());
    }

    public static List<String> toCodes(List<String> countryNames) {
        return countryNames.stream()
                .map(CountryCodeMapper::toCode)
                .toList();
    }
}
