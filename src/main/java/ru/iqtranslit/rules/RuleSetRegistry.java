package ru.iqtranslit.rules;

import ru.iqtranslit.model.StandardScheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RuleSetRegistry {
    private static final EnumMap<StandardScheme, SchemeRuleSet> RULES =
            new EnumMap<StandardScheme, SchemeRuleSet>(StandardScheme.class);

    static {
        register(StandardScheme.ICAO_DOC_9303, icaoStrict(), sharedExtended());
        register(StandardScheme.ISO_9_GOST_779, iso9Strict(), sharedExtended());
        register(StandardScheme.ALA_LC, alaLcStrict(), sharedExtended());
        register(StandardScheme.BGN_PCGN_1947, bgnPcgnStrict(), sharedExtended());
        register(StandardScheme.UNGEGN_1987, ungegnStrict(), sharedExtended());
        register(StandardScheme.GOST_R_52290_2004, gost52290Strict(), sharedExtended());
        register(StandardScheme.GOST_R_7034_2014, gost7034Strict(), sharedExtended());
        register(StandardScheme.GOST_16876_71, gost16876Strict(), sharedExtended());
        register(StandardScheme.SCHOLARLY, scholarlyStrict(), sharedExtended());
        register(StandardScheme.YANDEX_MAPS_STYLE, yandexStrict(), sharedExtended());
        register(StandardScheme.WIKIPEDIA_STYLE, wikipediaStrict(), sharedExtended());
        register(StandardScheme.TELEGRAM_STYLE, telegramStrict(), sharedExtended());
        register(StandardScheme.MOSCOW_METRO_STYLE, metroStrict(), sharedExtended());
    }

    private RuleSetRegistry() {
    }

    public static SchemeRuleSet forScheme(StandardScheme scheme) {
        SchemeRuleSet rules = RULES.get(scheme);
        if (rules == null) {
            throw new IllegalArgumentException("Unsupported scheme: " + scheme);
        }
        return rules;
    }

    public static List<StandardScheme> supportedSchemes() {
        return Collections.unmodifiableList(new ArrayList<StandardScheme>(RULES.keySet()));
    }

    private static void register(
            StandardScheme scheme,
            Map<Character, String> strictMap,
            Map<Character, List<String>> extendedMap
    ) {
        RULES.put(scheme, new SchemeRuleSet(scheme, strictMap, extendedMap));
    }

    private static Map<Character, String> basePractical() {
        LinkedHashMap<Character, String> map = new LinkedHashMap<Character, String>();
        map.put('а', "a");
        map.put('б', "b");
        map.put('в', "v");
        map.put('г', "g");
        map.put('д', "d");
        map.put('е', "e");
        map.put('ё', "yo");
        map.put('ж', "zh");
        map.put('з', "z");
        map.put('и', "i");
        map.put('й', "y");
        map.put('к', "k");
        map.put('л', "l");
        map.put('м', "m");
        map.put('н', "n");
        map.put('о', "o");
        map.put('п', "p");
        map.put('р', "r");
        map.put('с', "s");
        map.put('т', "t");
        map.put('у', "u");
        map.put('ф', "f");
        map.put('х', "kh");
        map.put('ц', "ts");
        map.put('ч', "ch");
        map.put('ш', "sh");
        map.put('щ', "shch");
        map.put('ъ', "");
        map.put('ы', "y");
        map.put('ь', "");
        map.put('э', "e");
        map.put('ю', "yu");
        map.put('я', "ya");
        return map;
    }

    private static Map<Character, String> icaoStrict() {
        Map<Character, String> map = basePractical();
        map.put('ё', "e");
        map.put('й', "i");
        map.put('ъ', "ie");
        map.put('ь', "");
        map.put('ю', "iu");
        map.put('я', "ia");
        return map;
    }

    private static Map<Character, String> iso9Strict() {
        Map<Character, String> map = new LinkedHashMap<Character, String>();
        map.put('а', "a");
        map.put('б', "b");
        map.put('в', "v");
        map.put('г', "g");
        map.put('д', "d");
        map.put('е', "e");
        map.put('ё', "ë");
        map.put('ж', "ž");
        map.put('з', "z");
        map.put('и', "i");
        map.put('й', "j");
        map.put('к', "k");
        map.put('л', "l");
        map.put('м', "m");
        map.put('н', "n");
        map.put('о', "o");
        map.put('п', "p");
        map.put('р', "r");
        map.put('с', "s");
        map.put('т', "t");
        map.put('у', "u");
        map.put('ф', "f");
        map.put('х', "h");
        map.put('ц', "c");
        map.put('ч', "č");
        map.put('ш', "š");
        map.put('щ', "ŝ");
        map.put('ъ', "ʺ");
        map.put('ы', "y");
        map.put('ь', "ʹ");
        map.put('э', "è");
        map.put('ю', "û");
        map.put('я', "â");
        return map;
    }

    private static Map<Character, String> alaLcStrict() {
        Map<Character, String> map = new LinkedHashMap<Character, String>();
        map.put('а', "a");
        map.put('б', "b");
        map.put('в', "v");
        map.put('г', "g");
        map.put('д', "d");
        map.put('е', "e");
        map.put('ё', "ë");
        map.put('ж', "zh");
        map.put('з', "z");
        map.put('и', "i");
        map.put('й', "ĭ");
        map.put('к', "k");
        map.put('л', "l");
        map.put('м', "m");
        map.put('н', "n");
        map.put('о', "o");
        map.put('п', "p");
        map.put('р', "r");
        map.put('с', "s");
        map.put('т', "t");
        map.put('у', "u");
        map.put('ф', "f");
        map.put('х', "kh");
        map.put('ц', "t͡s");
        map.put('ч', "ch");
        map.put('ш', "sh");
        map.put('щ', "shch");
        map.put('ъ', "ʺ");
        map.put('ь', "ʹ");
        map.put('ы', "y");
        map.put('э', "ė");
        map.put('ю', "i͡u");
        map.put('я', "i͡a");
        return map;
    }

    private static Map<Character, String> bgnPcgnStrict() {
        Map<Character, String> map = basePractical();
        map.put('е', "e");
        map.put('ё', "ë");
        map.put('й', "y");
        map.put('ъ', "”");
        map.put('ь', "’");
        return map;
    }

    private static Map<Character, String> ungegnStrict() {
        Map<Character, String> map = new LinkedHashMap<Character, String>();
        map.put('а', "a");
        map.put('б', "b");
        map.put('в', "v");
        map.put('г', "g");
        map.put('д', "d");
        map.put('е', "e");
        map.put('ё', "ë");
        map.put('ж', "ž");
        map.put('з', "z");
        map.put('и', "i");
        map.put('й', "j");
        map.put('к', "k");
        map.put('л', "l");
        map.put('м', "m");
        map.put('н', "n");
        map.put('о', "o");
        map.put('п', "p");
        map.put('р', "r");
        map.put('с', "s");
        map.put('т', "t");
        map.put('у', "u");
        map.put('ф', "f");
        map.put('х', "h");
        map.put('ц', "c");
        map.put('ч', "č");
        map.put('ш', "š");
        map.put('щ', "šč");
        map.put('ъ', "ʺ");
        map.put('ы', "y");
        map.put('ь', "ʹ");
        map.put('э', "è");
        map.put('ю', "ju");
        map.put('я', "ja");
        return map;
    }

    private static Map<Character, String> gost52290Strict() {
        Map<Character, String> map = basePractical();
        map.put('е', "e");
        map.put('ё', "ye");
        map.put('й', "y");
        map.put('ъ', "'");
        map.put('ь', "'");
        return map;
    }

    private static Map<Character, String> gost7034Strict() {
        LinkedHashMap<Character, String> map = new LinkedHashMap<Character, String>();
        map.put('а', "a");
        map.put('б', "b");
        map.put('в', "v");
        map.put('г', "g");
        map.put('д', "d");
        map.put('е', "e");
        map.put('ё', "yo");
        map.put('ж', "zh");
        map.put('з', "z");
        map.put('и', "i");
        map.put('й', "j");
        map.put('к', "k");
        map.put('л', "l");
        map.put('м', "m");
        map.put('н', "n");
        map.put('о', "o");
        map.put('п', "p");
        map.put('р', "r");
        map.put('с', "s");
        map.put('т', "t");
        map.put('у', "u");
        map.put('ф', "f");
        map.put('х', "x");
        map.put('ц', "c");
        map.put('ч', "ch");
        map.put('ш', "sh");
        map.put('щ', "shh");
        map.put('ъ', "''");
        map.put('ы', "y");
        map.put('ь', "'");
        map.put('э', "e");
        map.put('ю', "yu");
        map.put('я', "ya");
        return map;
    }

    private static Map<Character, String> gost16876Strict() {
        Map<Character, String> map = new LinkedHashMap<Character, String>();
        map.put('а', "a");
        map.put('б', "b");
        map.put('в', "v");
        map.put('г', "g");
        map.put('д', "d");
        map.put('е', "e");
        map.put('ё', "ë");
        map.put('ж', "ž");
        map.put('з', "z");
        map.put('и', "i");
        map.put('й', "j");
        map.put('к', "k");
        map.put('л', "l");
        map.put('м', "m");
        map.put('н', "n");
        map.put('о', "o");
        map.put('п', "p");
        map.put('р', "r");
        map.put('с', "s");
        map.put('т', "t");
        map.put('у', "u");
        map.put('ф', "f");
        map.put('х', "h");
        map.put('ц', "c");
        map.put('ч', "č");
        map.put('ш', "š");
        map.put('щ', "ŝ");
        map.put('ъ', "ʺ");
        map.put('ы', "y");
        map.put('ь', "ʹ");
        map.put('э', "è");
        map.put('ю', "û");
        map.put('я', "â");
        return map;
    }

    private static Map<Character, String> scholarlyStrict() {
        Map<Character, String> map = iso9Strict();
        map.put('щ', "šč");
        map.put('ю', "ju");
        map.put('я', "ja");
        return map;
    }

    private static Map<Character, String> yandexStrict() {
        Map<Character, String> map = basePractical();
        map.put('й', "y");
        map.put('щ', "sch");
        map.put('ъ', "");
        map.put('ь', "");
        return map;
    }

    private static Map<Character, String> wikipediaStrict() {
        Map<Character, String> map = basePractical();
        map.put('й', "i");
        map.put('ё', "yo");
        map.put('ъ', "ʺ");
        map.put('ь', "ʹ");
        return map;
    }

    private static Map<Character, String> telegramStrict() {
        Map<Character, String> map = basePractical();
        map.put('й', "i");
        map.put('ё', "yo");
        map.put('ъ', "");
        map.put('ь', "");
        return map;
    }

    private static Map<Character, String> metroStrict() {
        Map<Character, String> map = basePractical();
        map.put('ё', "e");
        map.put('й', "y");
        map.put('ъ', "");
        map.put('ь', "");
        return map;
    }

    private static Map<Character, List<String>> sharedExtended() {
        LinkedHashMap<Character, List<String>> map = new LinkedHashMap<Character, List<String>>();
        map.put('е', list("e", "ye", "je", "ie"));
        map.put('ё', list("e", "yo", "jo"));
        map.put('ж', list("zh", "j"));
        map.put('й', list("i", "y", "j"));
        map.put('х', list("kh", "h", "x"));
        map.put('ц', list("ts", "c", "cz"));
        map.put('щ', list("shch", "sch", "shh"));
        map.put('ъ', list("", "'", "ie"));
        map.put('ь', list("", "'", "j"));
        map.put('э', list("e", "eh"));
        map.put('ю', list("yu", "iu", "ju"));
        map.put('я', list("ya", "ia", "ja"));
        return map;
    }

    private static List<String> list(String... values) {
        return Collections.unmodifiableList(Arrays.asList(values));
    }
}
