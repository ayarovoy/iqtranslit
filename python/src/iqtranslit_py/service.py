from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Iterable, List, Optional, Set

from .diacritics import simplify
from .engine import TransliterationEngine
from .models import (
    StandardScheme,
    TransliterationRequest,
    TransliterationResult,
    TransliterationStringOutput,
    TransliterationVariant,
)
from .rules import for_scheme, supported_schemes


class TransliterationService(ABC):
    @abstractmethod
    def transliterate_all(self, request: TransliterationRequest) -> TransliterationResult:
        pass

    @abstractmethod
    def transliterate(
        self, request: TransliterationRequest, scheme: StandardScheme
    ) -> TransliterationResult:
        pass

    @abstractmethod
    def transliterate_to_strings(
        self,
        request: TransliterationRequest,
        scheme: Optional[StandardScheme] = None,
        output: TransliterationStringOutput = TransliterationStringOutput.FULL,
    ) -> List[str]:
        pass


class DefaultTransliterationService(TransliterationService):
    def __init__(self) -> None:
        self.engine = TransliterationEngine()

    def transliterate_all(self, request: TransliterationRequest) -> TransliterationResult:
        if request is None:
            raise ValueError("request must not be null")

        schemes = self._resolve_schemes(request.schemes)
        strict_results: List[TransliterationVariant] = []
        extended_results: List[TransliterationVariant] = []

        for scheme in schemes:
            rule_set = for_scheme(scheme)
            strict_value = self.engine.transliterate_strict(request.source_text, rule_set)
            strict_variant = TransliterationVariant(scheme, request.source_text, strict_value)
            strict_results.append(strict_variant)
            self._add_strict_diacritic_fallback(strict_results, strict_variant)

            if request.include_extended:
                extended = self.engine.transliterate_extended(
                    request.source_text,
                    rule_set,
                    request.max_extended_variants_per_scheme,
                    strict_value,
                )
                enriched = self._enrich_with_diacritic_free_variants(
                    strict_value, extended, request.max_extended_variants_per_scheme
                )
                for value in enriched:
                    extended_results.append(
                        TransliterationVariant(scheme, request.source_text, value)
                    )

        return TransliterationResult(
            request.source_text, tuple(strict_results), tuple(extended_results)
        )

    def transliterate(
        self, request: TransliterationRequest, scheme: StandardScheme
    ) -> TransliterationResult:
        if request is None:
            raise ValueError("request must not be null")
        if scheme is None:
            raise ValueError("scheme must not be null")

        scoped_request = (
            TransliterationRequest.builder(request.source_text)
            .include_extended(request.include_extended)
            .max_extended_variants_per_scheme(request.max_extended_variants_per_scheme)
            .schemes([scheme])
            .build()
        )
        return self.transliterate_all(scoped_request)

    def transliterate_to_strings(
        self,
        request: TransliterationRequest,
        scheme: Optional[StandardScheme] = None,
        output: TransliterationStringOutput = TransliterationStringOutput.FULL,
    ) -> List[str]:
        result = (
            self.transliterate_all(request)
            if scheme is None
            else self.transliterate(request, scheme)
        )
        return self._to_distinct_strings(result, output)

    def _resolve_schemes(self, requested_schemes: Iterable[StandardScheme]) -> List[StandardScheme]:
        if not requested_schemes:
            return supported_schemes()
        return list(requested_schemes)

    def _to_distinct_strings(
        self, result: TransliterationResult, output: TransliterationStringOutput
    ) -> List[str]:
        if output is None:
            raise ValueError("output must not be null")
        values: List[str] = []
        seen: Set[str] = set()
        self._add_variant_strings(values, seen, result.strict_results, output)
        self._add_variant_strings(values, seen, result.extended_results, output)
        return values

    def _add_variant_strings(
        self,
        values: List[str],
        seen: Set[str],
        variants: Iterable[TransliterationVariant],
        output: TransliterationStringOutput,
    ) -> None:
        for variant in variants:
            if output == TransliterationStringOutput.FULL or not variant.has_diacritic:
                self._append_unique(values, seen, variant.transliterated_text)

    def _add_strict_diacritic_fallback(
        self,
        strict_results: List[TransliterationVariant],
        strict_variant: TransliterationVariant,
    ) -> None:
        if not strict_variant.has_diacritic:
            return
        simplified = simplify(strict_variant.transliterated_text)
        if simplified == strict_variant.transliterated_text:
            return
        strict_results.append(
            TransliterationVariant(
                strict_variant.scheme, strict_variant.source_text, simplified
            )
        )

    def _enrich_with_diacritic_free_variants(
        self, strict_value: str, extended: List[str], limit: int
    ) -> List[str]:
        values: List[str] = []
        seen: Set[str] = set()
        for value in extended:
            self._append_unique(values, seen, value)

        for value in list(values):
            self._add_simplified_variant(values, seen, value, strict_value)
            if len(values) >= limit:
                break

        return values[:limit]

    def _add_simplified_variant(
        self, values: List[str], seen: Set[str], candidate: str, strict_value: str
    ) -> None:
        simplified = simplify(candidate)
        if simplified != candidate and simplified != strict_value:
            self._append_unique(values, seen, simplified)

    @staticmethod
    def _append_unique(values: List[str], seen: Set[str], candidate: str) -> None:
        if candidate in seen:
            return
        seen.add(candidate)
        values.append(candidate)
