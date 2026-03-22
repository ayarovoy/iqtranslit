from __future__ import annotations

from typing import Dict, List

from .scheme_ruleset import SchemeRuleSet


class TransliterationEngine:
    def transliterate_strict(self, source: str, rule_set: SchemeRuleSet) -> str:
        out = []
        for i in range(len(source)):
            out.append(rule_set.strict_token_at(source, i))
        return "".join(out)

    def transliterate_extended(
        self, source: str, rule_set: SchemeRuleSet, limit: int, strict_value: str
    ) -> List[str]:
        partial: Dict[str, None] = {"": None}

        for i in range(len(source)):
            token_options = rule_set.extended_tokens(source, i)
            next_partial: Dict[str, None] = {}
            for prefix in partial.keys():
                for token in token_options:
                    next_partial.setdefault(prefix + token, None)
                    if len(next_partial) >= limit:
                        break
                if len(next_partial) >= limit:
                    break
            partial = next_partial

        deduped: Dict[str, None] = {}
        for value in partial.keys():
            if value != strict_value:
                deduped.setdefault(value, None)

        result = list(deduped.keys())
        if len(result) > limit:
            return result[:limit]
        return result
