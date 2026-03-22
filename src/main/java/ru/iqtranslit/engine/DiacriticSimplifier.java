package ru.iqtranslit.engine;

import java.text.Normalizer;

final class DiacriticSimplifier {
    private DiacriticSimplifier() {
    }

    static String simplify(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < normalized.length(); i++) {
            char current = normalized.charAt(i);
            if (Character.getType(current) == Character.NON_SPACING_MARK
                    || Character.getType(current) == Character.COMBINING_SPACING_MARK
                    || Character.getType(current) == Character.ENCLOSING_MARK) {
                continue;
            }
            if (current == '\u02BA') {
                builder.append('"');
                continue;
            }
            if (current == '\u02B9') {
                builder.append('\'');
                continue;
            }
            builder.append(current);
        }

        return builder.toString();
    }
}
