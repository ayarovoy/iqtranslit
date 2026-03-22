from __future__ import annotations

from dataclasses import dataclass
from typing import Dict, List

from .models import StandardScheme


def _ordered_add(target: Dict[str, None], value: str) -> None:
    target.setdefault(value, None)


@dataclass(frozen=True)
class SchemeRuleSet:
    scheme: StandardScheme
    strict_map: Dict[str, str]
    extended_map: Dict[str, List[str]]

    def strict_token(self, source_char: str) -> str:
        mapped = self.strict_map.get(source_char.lower())
        if mapped is None:
            return source_char
        return self._apply_case(mapped, source_char)

    def strict_token_at(self, source: str, index: int) -> str:
        source_char = source[index]
        if self.scheme == StandardScheme.YANDEX_MAPS_STYLE:
            return self._yandex_strict_token(source, index, source_char)
        if self.scheme == StandardScheme.BGN_PCGN_1947:
            return self._bgn_pcgn_strict_token(source, index, source_char)
        if self.scheme == StandardScheme.GOST_R_52290_2004:
            return self._gost_52290_strict_token(source, index, source_char)
        return self.strict_token(source_char)

    def extended_tokens(self, source: str, index: int) -> List[str]:
        source_char = source[index]
        lower = source_char.lower()
        strict = self.strict_token_at(source, index)
        if strict == source_char and lower not in self.strict_map:
            return [source_char]

        options: Dict[str, None] = {}
        _ordered_add(options, strict)

        mapped_alternatives = self.extended_map.get(lower)
        if mapped_alternatives is not None:
            for alternative in mapped_alternatives:
                _ordered_add(options, self._apply_case(alternative, source_char))

        self._add_context_options(options, source, index, source_char, lower)
        return list(options.keys())

    def _add_context_options(
        self,
        options: Dict[str, None],
        source: str,
        index: int,
        source_char: str,
        lower: str,
    ) -> None:
        if lower == "е" and self._is_word_start(source, index):
            _ordered_add(options, self._apply_case("ye", source_char))
            _ordered_add(options, self._apply_case("je", source_char))
        if lower == "ё":
            _ordered_add(options, self._apply_case("yo", source_char))
            _ordered_add(options, self._apply_case("jo", source_char))
        if lower == "й":
            _ordered_add(options, self._apply_case("y", source_char))
            _ordered_add(options, self._apply_case("j", source_char))
            _ordered_add(options, self._apply_case("i", source_char))
        if lower == "х":
            _ordered_add(options, self._apply_case("kh", source_char))
            _ordered_add(options, self._apply_case("h", source_char))
            _ordered_add(options, self._apply_case("x", source_char))
        if lower == "щ":
            _ordered_add(options, self._apply_case("shch", source_char))
            _ordered_add(options, self._apply_case("sch", source_char))
            _ordered_add(options, self._apply_case("shh", source_char))
        if lower == "ъ" or lower == "ь":
            _ordered_add(options, "")
            _ordered_add(options, "'")
        if lower == "ю":
            _ordered_add(options, self._apply_case("yu", source_char))
            _ordered_add(options, self._apply_case("iu", source_char))
            _ordered_add(options, self._apply_case("ju", source_char))
        if lower == "я":
            _ordered_add(options, self._apply_case("ya", source_char))
            _ordered_add(options, self._apply_case("ia", source_char))
            _ordered_add(options, self._apply_case("ja", source_char))

    @staticmethod
    def _is_word_start(source: str, index: int) -> bool:
        if index == 0:
            return True
        prev = source[index - 1]
        return not prev.isalpha()

    def _yandex_strict_token(self, source: str, index: int, source_char: str) -> str:
        lower = source_char.lower()
        if lower == "е":
            if self._is_word_start(source, index) or self._follows_yandex_ye_boundary(source, index):
                return self._apply_case("ye", source_char)
            return self._apply_case("e", source_char)
        if lower == "щ":
            return self._apply_case("sch", source_char)
        if lower == "ъ":
            return ""
        if lower == "ь":
            if index + 1 < len(source) and source[index + 1].lower() == "и":
                return self._apply_case("y", source_char)
            return ""
        if (lower == "ы" or lower == "и") and self._is_yandex_iy_ending(source, index):
            return self._apply_case("i", source_char)
        return self.strict_token(source_char)

    def _bgn_pcgn_strict_token(self, source: str, index: int, source_char: str) -> str:
        lower = source_char.lower()
        if lower == "е":
            if self._is_word_start(source, index) or self._follows_ye_boundary(source, index):
                return self._apply_case("ye", source_char)
            return self._apply_case("e", source_char)
        if lower == "ё":
            if self._is_word_start(source, index) or self._follows_ye_boundary(source, index):
                return self._apply_case("yë", source_char)
            return self._apply_case("ë", source_char)
        return self.strict_token(source_char)

    def _gost_52290_strict_token(self, source: str, index: int, source_char: str) -> str:
        lower = source_char.lower()
        if lower == "е":
            if self._is_word_start(source, index) or self._follows_ye_boundary(source, index):
                return self._apply_case("ye", source_char)
            return self._apply_case("e", source_char)
        if lower == "ё":
            if self._is_word_start(source, index) or self._follows_ye_boundary(source, index):
                return self._apply_case("yo", source_char)
            prev = source[index - 1].lower() if index > 0 else ""
            if prev in ("ч", "ш", "щ", "ж"):
                return self._apply_case("e", source_char)
            return self._apply_case("ye", source_char)
        return self.strict_token(source_char)

    @staticmethod
    def _follows_yandex_ye_boundary(source: str, index: int) -> bool:
        if index == 0:
            return True
        prev = source[index - 1].lower()
        return SchemeRuleSet._is_vowel(prev) or prev == "ь" or prev == "ъ"

    @staticmethod
    def _follows_ye_boundary(source: str, index: int) -> bool:
        if index == 0:
            return True
        prev = source[index - 1].lower()
        return SchemeRuleSet._is_vowel(prev) or prev in ("ь", "ъ", "й")

    @staticmethod
    def _is_yandex_iy_ending(source: str, index: int) -> bool:
        if index + 1 >= len(source):
            return False
        next_char = source[index + 1].lower()
        if next_char != "й":
            return False
        return index + 2 == len(source) or not source[index + 2].isalpha()

    @staticmethod
    def _is_vowel(value: str) -> bool:
        return value in ("а", "е", "ё", "и", "о", "у", "ы", "э", "ю", "я")

    @staticmethod
    def _apply_case(token: str, source_char: str) -> str:
        if token == "":
            return token
        if not source_char.isupper():
            return token
        if len(token) == 1:
            return token.upper()
        return token[0].upper() + token[1:]
