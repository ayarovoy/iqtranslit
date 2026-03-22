package ru.iqtranslit.rules;

import ru.iqtranslit.model.StandardScheme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public final class SchemeRuleSet {
    private final StandardScheme scheme;
    private final Map<Character, String> strictMap;
    private final Map<Character, List<String>> extendedMap;

    public SchemeRuleSet(
            StandardScheme scheme,
            Map<Character, String> strictMap,
            Map<Character, List<String>> extendedMap
    ) {
        this.scheme = scheme;
        this.strictMap = strictMap;
        this.extendedMap = extendedMap;
    }

    public StandardScheme getScheme() {
        return scheme;
    }

    public String strictToken(char sourceChar) {
        String mapped = strictMap.get(Character.toLowerCase(sourceChar));
        if (mapped == null) {
            return String.valueOf(sourceChar);
        }
        return applyCase(mapped, sourceChar);
    }

    public String strictToken(String source, int index) {
        char sourceChar = source.charAt(index);
        if (scheme == StandardScheme.YANDEX_MAPS_STYLE) {
            return yandexStrictToken(source, index, sourceChar);
        }
        if (scheme == StandardScheme.BGN_PCGN_1947) {
            return bgnPcgnStrictToken(source, index, sourceChar);
        }
        if (scheme == StandardScheme.GOST_R_52290_2004) {
            return gost52290StrictToken(source, index, sourceChar);
        }
        return strictToken(sourceChar);
    }

    public List<String> extendedTokens(String source, int index) {
        char sourceChar = source.charAt(index);
        char lower = Character.toLowerCase(sourceChar);
        String strict = strictToken(source, index);
        if (strict.equals(String.valueOf(sourceChar)) && !strictMap.containsKey(lower)) {
            return Collections.singletonList(String.valueOf(sourceChar));
        }

        LinkedHashSet<String> options = new LinkedHashSet<String>();
        options.add(strict);

        List<String> mappedAlternatives = extendedMap.get(lower);
        if (mappedAlternatives != null) {
            for (String alternative : mappedAlternatives) {
                options.add(applyCase(alternative, sourceChar));
            }
        }

        addContextOptions(options, source, index, sourceChar, lower);
        return Collections.unmodifiableList(new ArrayList<String>(options));
    }

    private void addContextOptions(
            LinkedHashSet<String> options,
            String source,
            int index,
            char sourceChar,
            char lower
    ) {
        if (lower == 'е' && isWordStart(source, index)) {
            options.add(applyCase("ye", sourceChar));
            options.add(applyCase("je", sourceChar));
        }
        if (lower == 'ё') {
            options.add(applyCase("yo", sourceChar));
            options.add(applyCase("jo", sourceChar));
        }
        if (lower == 'й') {
            options.add(applyCase("y", sourceChar));
            options.add(applyCase("j", sourceChar));
            options.add(applyCase("i", sourceChar));
        }
        if (lower == 'х') {
            options.add(applyCase("kh", sourceChar));
            options.add(applyCase("h", sourceChar));
            options.add(applyCase("x", sourceChar));
        }
        if (lower == 'щ') {
            options.add(applyCase("shch", sourceChar));
            options.add(applyCase("sch", sourceChar));
            options.add(applyCase("shh", sourceChar));
        }
        if (lower == 'ъ' || lower == 'ь') {
            options.add("");
            options.add("'");
        }
        if (lower == 'ю') {
            options.add(applyCase("yu", sourceChar));
            options.add(applyCase("iu", sourceChar));
            options.add(applyCase("ju", sourceChar));
        }
        if (lower == 'я') {
            options.add(applyCase("ya", sourceChar));
            options.add(applyCase("ia", sourceChar));
            options.add(applyCase("ja", sourceChar));
        }
    }

    private static boolean isWordStart(String source, int index) {
        if (index == 0) {
            return true;
        }
        char prev = source.charAt(index - 1);
        return !Character.isLetter(prev);
    }

    private String yandexStrictToken(String source, int index, char sourceChar) {
        char lower = Character.toLowerCase(sourceChar);

        if (lower == 'е') {
            if (isWordStart(source, index) || followsYandexYeBoundary(source, index)) {
                return applyCase("ye", sourceChar);
            }
            return applyCase("e", sourceChar);
        }

        if (lower == 'щ') {
            return applyCase("sch", sourceChar);
        }

        if (lower == 'ъ') {
            return "";
        }

        if (lower == 'ь') {
            if (index + 1 < source.length() && Character.toLowerCase(source.charAt(index + 1)) == 'и') {
                return applyCase("y", sourceChar);
            }
            return "";
        }

        if ((lower == 'ы' || lower == 'и') && isYandexIyEnding(source, index)) {
            return applyCase("i", sourceChar);
        }

        return strictToken(sourceChar);
    }

    private String bgnPcgnStrictToken(String source, int index, char sourceChar) {
        char lower = Character.toLowerCase(sourceChar);

        if (lower == 'е') {
            if (isWordStart(source, index) || followsYeBoundary(source, index)) {
                return applyCase("ye", sourceChar);
            }
            return applyCase("e", sourceChar);
        }

        if (lower == 'ё') {
            if (isWordStart(source, index) || followsYeBoundary(source, index)) {
                return applyCase("yë", sourceChar);
            }
            return applyCase("ë", sourceChar);
        }

        return strictToken(sourceChar);
    }

    private String gost52290StrictToken(String source, int index, char sourceChar) {
        char lower = Character.toLowerCase(sourceChar);

        if (lower == 'е') {
            if (isWordStart(source, index) || followsYeBoundary(source, index)) {
                return applyCase("ye", sourceChar);
            }
            return applyCase("e", sourceChar);
        }

        if (lower == 'ё') {
            if (isWordStart(source, index) || followsYeBoundary(source, index)) {
                return applyCase("yo", sourceChar);
            }
            char prev = index > 0 ? Character.toLowerCase(source.charAt(index - 1)) : 0;
            if (prev == 'ч' || prev == 'ш' || prev == 'щ' || prev == 'ж') {
                return applyCase("e", sourceChar);
            }
            return applyCase("ye", sourceChar);
        }

        return strictToken(sourceChar);
    }

    private static boolean followsYandexYeBoundary(String source, int index) {
        if (index == 0) {
            return true;
        }
        char prev = Character.toLowerCase(source.charAt(index - 1));
        return isVowel(prev) || prev == 'ь' || prev == 'ъ';
    }

    private static boolean followsYeBoundary(String source, int index) {
        if (index == 0) {
            return true;
        }
        char prev = Character.toLowerCase(source.charAt(index - 1));
        return isVowel(prev) || prev == 'ь' || prev == 'ъ' || prev == 'й';
    }

    private static boolean isYandexIyEnding(String source, int index) {
        if (index + 1 >= source.length()) {
            return false;
        }
        char next = Character.toLowerCase(source.charAt(index + 1));
        if (next != 'й') {
            return false;
        }
        return index + 2 == source.length() || !Character.isLetter(source.charAt(index + 2));
    }

    private static boolean isVowel(char value) {
        return value == 'а'
                || value == 'е'
                || value == 'ё'
                || value == 'и'
                || value == 'о'
                || value == 'у'
                || value == 'ы'
                || value == 'э'
                || value == 'ю'
                || value == 'я';
    }

    private static String applyCase(String token, char sourceChar) {
        if (token.isEmpty()) {
            return token;
        }
        if (!Character.isUpperCase(sourceChar)) {
            return token;
        }
        if (token.length() == 1) {
            return token.toUpperCase();
        }
        return Character.toUpperCase(token.charAt(0)) + token.substring(1);
    }
}
