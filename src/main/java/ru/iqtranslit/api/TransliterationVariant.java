package ru.iqtranslit.api;

import ru.iqtranslit.model.StandardScheme;

import java.text.Normalizer;

public final class TransliterationVariant {
    private final StandardScheme scheme;
    private final String sourceText;
    private final String transliteratedText;
    private final boolean hasDiacritic;

    public TransliterationVariant(StandardScheme scheme, String sourceText, String transliteratedText) {
        if (scheme == null) {
            throw new IllegalArgumentException("scheme must not be null");
        }
        if (sourceText == null) {
            throw new IllegalArgumentException("sourceText must not be null");
        }
        if (transliteratedText == null) {
            throw new IllegalArgumentException("transliteratedText must not be null");
        }
        this.scheme = scheme;
        this.sourceText = sourceText;
        this.transliteratedText = transliteratedText;
        this.hasDiacritic = detectDiacritic(transliteratedText);
    }

    public StandardScheme getScheme() {
        return scheme;
    }

    public String getSourceText() {
        return sourceText;
    }

    public String getTransliteratedText() {
        return transliteratedText;
    }

    public boolean hasDiacritic() {
        return hasDiacritic;
    }

    private static boolean detectDiacritic(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        for (int i = 0; i < normalized.length(); i++) {
            char current = normalized.charAt(i);
            int type = Character.getType(current);
            if (type == Character.NON_SPACING_MARK
                    || type == Character.COMBINING_SPACING_MARK
                    || type == Character.ENCLOSING_MARK) {
                return true;
            }
        }

        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current == '\u02BA' || current == '\u02B9' || current == '\u0361') {
                return true;
            }
        }

        return false;
    }
}
