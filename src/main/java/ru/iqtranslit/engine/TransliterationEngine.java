package ru.iqtranslit.engine;

import ru.iqtranslit.rules.SchemeRuleSet;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

final class TransliterationEngine {
    String transliterateStrict(String source, SchemeRuleSet ruleSet) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            builder.append(ruleSet.strictToken(source, i));
        }
        return builder.toString();
    }

    List<String> transliterateExtended(String source, SchemeRuleSet ruleSet, int limit, String strictValue) {
        LinkedHashSet<String> partial = new LinkedHashSet<String>();
        partial.add("");

        for (int i = 0; i < source.length(); i++) {
            List<String> tokenOptions = ruleSet.extendedTokens(source, i);
            LinkedHashSet<String> next = new LinkedHashSet<String>();
            for (String prefix : partial) {
                for (String token : tokenOptions) {
                    next.add(prefix + token);
                    if (next.size() >= limit) {
                        break;
                    }
                }
                if (next.size() >= limit) {
                    break;
                }
            }
            partial = next;
        }

        LinkedHashSet<String> deduped = new LinkedHashSet<String>(partial);
        deduped.remove(strictValue);
        List<String> result = new ArrayList<String>(deduped);
        if (result.size() > limit) {
            return new ArrayList<String>(result.subList(0, limit));
        }
        return result;
    }
}
