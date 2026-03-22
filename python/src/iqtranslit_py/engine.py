from __future__ import annotations

from typing import List, Set

from .scheme_ruleset import SchemeRuleSet


class TransliterationEngine:
    def transliterate_strict(self, source: str, rule_set: SchemeRuleSet) -> str:
        return "".join(rule_set.strict_token_at(source, idx) for idx, _ in enumerate(source))

    def transliterate_extended(
        self, source: str, rule_set: SchemeRuleSet, limit: int, strict_value: str
    ) -> List[str]:
        partial = [""]

        for idx, _ in enumerate(source):
            token_options = rule_set.extended_tokens(source, idx)
            next_partial: List[str] = []
            seen_next: Set[str] = set()
            for prefix in partial:
                for token in token_options:
                    candidate = prefix + token
                    if candidate in seen_next:
                        continue
                    seen_next.add(candidate)
                    next_partial.append(candidate)
                    if len(next_partial) >= limit:
                        break
                if len(next_partial) >= limit:
                    break
            partial = next_partial

        return [value for value in partial if value != strict_value][:limit]
