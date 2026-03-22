package ru.iqtranslit.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum StandardScheme {
    ICAO_DOC_9303("ICAO Doc 9303", true),
    ISO_9_GOST_779("ISO 9:1995 / ГОСТ 7.79-2000", true),
    ALA_LC("ALA-LC", true),
    BGN_PCGN_1947("BGN/PCGN 1947", true),
    UNGEGN_1987("UNGEGN 1987", true),
    GOST_R_52290_2004("ГОСТ Р 52290-2004", true),
    GOST_R_7034_2014("ГОСТ Р 7.0.34-2014", true),
    GOST_16876_71("ГОСТ 16876-71", true),
    SCHOLARLY("Scholarly transliteration", false),
    YANDEX_MAPS_STYLE("Yandex.Maps style", false),
    WIKIPEDIA_STYLE("Wikipedia style", false),
    TELEGRAM_STYLE("Telegram style", false),
    MOSCOW_METRO_STYLE("Moscow Metro style", false);

    private final String displayName;
    private final boolean formal;

    StandardScheme(String displayName, boolean formal) {
        this.displayName = displayName;
        this.formal = formal;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isFormal() {
        return formal;
    }

    public static List<StandardScheme> defaults() {
        return Collections.unmodifiableList(Arrays.asList(values()));
    }
}
