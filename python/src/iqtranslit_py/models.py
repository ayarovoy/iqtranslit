from __future__ import annotations

from dataclasses import dataclass, field
from enum import Enum
from typing import Iterable, Optional, Set, Tuple

from .diacritics import detect_diacritic


class StandardScheme(Enum):
    ICAO_DOC_9303 = ("ICAO Doc 9303", True)
    ISO_9_GOST_779 = ("ISO 9:1995 / ГОСТ 7.79-2000", True)
    ALA_LC = ("ALA-LC", True)
    BGN_PCGN_1947 = ("BGN/PCGN 1947", True)
    UNGEGN_1987 = ("UNGEGN 1987", True)
    GOST_R_52290_2004 = ("ГОСТ Р 52290-2004", True)
    GOST_R_7034_2014 = ("ГОСТ Р 7.0.34-2014", True)
    GOST_16876_71 = ("ГОСТ 16876-71", True)
    SCHOLARLY = ("Scholarly transliteration", False)
    YANDEX_MAPS_STYLE = ("Yandex.Maps style", False)
    WIKIPEDIA_STYLE = ("Wikipedia style", False)
    TELEGRAM_STYLE = ("Telegram style", False)
    MOSCOW_METRO_STYLE = ("Moscow Metro style", False)

    @property
    def display_name(self) -> str:
        return self.value[0]

    @property
    def formal(self) -> bool:
        return self.value[1]

    @classmethod
    def defaults(cls) -> Tuple["StandardScheme", ...]:
        return tuple(cls)


class TransliterationStringOutput(Enum):
    FULL = "FULL"
    WITHOUT_DIACRITICS_ONLY = "WITHOUT_DIACRITICS_ONLY"


@dataclass(frozen=True)
class TransliterationVariant:
    scheme: StandardScheme
    source_text: str
    transliterated_text: str
    has_diacritic: bool = field(init=False)

    def __post_init__(self) -> None:
        if self.scheme is None:
            raise ValueError("scheme must not be null")
        if self.source_text is None:
            raise ValueError("sourceText must not be null")
        if self.transliterated_text is None:
            raise ValueError("transliteratedText must not be null")
        object.__setattr__(self, "has_diacritic", detect_diacritic(self.transliterated_text))


@dataclass(frozen=True)
class TransliterationResult:
    source_text: str
    strict_results: Tuple[TransliterationVariant, ...]
    extended_results: Tuple[TransliterationVariant, ...]

    def __post_init__(self) -> None:
        if self.source_text is None:
            raise ValueError("sourceText must not be null")


DEFAULT_MAX_EXTENDED_PER_SCHEME = 64


@dataclass(frozen=True)
class TransliterationRequest:
    source_text: str
    include_extended: bool = False
    max_extended_variants_per_scheme: int = DEFAULT_MAX_EXTENDED_PER_SCHEME
    schemes: Tuple[StandardScheme, ...] = tuple()

    def __post_init__(self) -> None:
        if self.source_text is None:
            raise ValueError("sourceText must not be null")
        if self.max_extended_variants_per_scheme <= 0:
            raise ValueError("maxExtendedVariantsPerScheme must be positive")
        normalized_schemes = _normalize_schemes(self.schemes)
        object.__setattr__(self, "schemes", normalized_schemes)

    @classmethod
    def builder(cls, source_text: str) -> "TransliterationRequestBuilder":
        return TransliterationRequestBuilder(source_text)


class TransliterationRequestBuilder:
    def __init__(self, source_text: str) -> None:
        self._source_text = source_text
        self._include_extended = False
        self._max_extended_variants_per_scheme = DEFAULT_MAX_EXTENDED_PER_SCHEME
        self._schemes: Tuple[StandardScheme, ...] = tuple()

    def include_extended(self, include_extended: bool) -> "TransliterationRequestBuilder":
        self._include_extended = include_extended
        return self

    def max_extended_variants_per_scheme(self, limit: int) -> "TransliterationRequestBuilder":
        self._max_extended_variants_per_scheme = limit
        return self

    def schemes(
        self, schemes: Optional[Iterable[StandardScheme]]
    ) -> "TransliterationRequestBuilder":
        if not schemes:
            self._schemes = tuple()
            return self
        unique: Set[StandardScheme] = set(schemes)
        ordered = [scheme for scheme in StandardScheme if scheme in unique]
        self._schemes = tuple(ordered)
        return self

    def build(self) -> TransliterationRequest:
        return TransliterationRequest(
            source_text=self._source_text,
            include_extended=self._include_extended,
            max_extended_variants_per_scheme=self._max_extended_variants_per_scheme,
            schemes=self._schemes,
        )


def _normalize_schemes(
    schemes: Optional[Iterable[StandardScheme]],
) -> Tuple[StandardScheme, ...]:
    if not schemes:
        return tuple()
    unique: Set[StandardScheme] = set(schemes)
    ordered = [scheme for scheme in StandardScheme if scheme in unique]
    return tuple(ordered)


def make_request(
    source_text: str,
    *,
    include_extended: bool = False,
    max_extended_variants_per_scheme: int = DEFAULT_MAX_EXTENDED_PER_SCHEME,
    schemes: Optional[Iterable[StandardScheme]] = None,
) -> TransliterationRequest:
    return TransliterationRequest(
        source_text=source_text,
        include_extended=include_extended,
        max_extended_variants_per_scheme=max_extended_variants_per_scheme,
        schemes=_normalize_schemes(schemes),
    )
