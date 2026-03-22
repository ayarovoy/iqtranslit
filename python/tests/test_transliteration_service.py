import re
from pathlib import Path

import pytest

from iqtranslit_py import (
    StandardScheme,
    TransliterationRequest,
    TransliterationStringOutput,
    create_default_service,
)


SERVICE = create_default_service()


def request(source, include_extended=False, max_extended=64, schemes=None):
    return (
        TransliterationRequest.builder(source)
        .include_extended(include_extended)
        .max_extended_variants_per_scheme(max_extended)
        .schemes(schemes)
        .build()
    )


def test_returns_strict_result_for_every_scheme():
    result = SERVICE.transliterate_all(request("Привет, Ёжик!", include_extended=False))
    assert len({variant.scheme for variant in result.strict_results}) == len(StandardScheme)
    assert result.extended_results == tuple()


def test_supports_single_scheme_mode():
    result = SERVICE.transliterate(request("Щука"), StandardScheme.ICAO_DOC_9303)
    strict = result.strict_results
    assert len(strict) == 1
    assert strict[0].transliterated_text == "Shchuka"


def test_returns_distinct_strings_across_schemes():
    req = request("Яровой Андрей", include_extended=True, max_extended=50)
    raw_result = SERVICE.transliterate_all(req)
    values = SERVICE.transliterate_to_strings(req)
    raw_count = len(raw_result.strict_results) + len(raw_result.extended_results)

    assert len(values) == len(set(values))
    assert len(values) < raw_count
    assert "Yarovoy Andrey" in values


def test_returns_only_diacritic_free_strings_when_requested():
    req = request("Яровой Андрей", include_extended=True, max_extended=50)
    values = SERVICE.transliterate_to_strings(
        req,
        StandardScheme.ALA_LC,
        TransliterationStringOutput.WITHOUT_DIACRITICS_ONLY,
    )
    assert "Iarovoi Andrei" in values
    assert "I͡arovoĭ Andreĭ" not in values


def test_string_api_respects_extended_toggle():
    strict_only = request("Яровой Андрей", include_extended=False)
    extended = request("Яровой Андрей", include_extended=True, max_extended=50)

    strict_only_values = SERVICE.transliterate_to_strings(
        strict_only,
        StandardScheme.ALA_LC,
        TransliterationStringOutput.WITHOUT_DIACRITICS_ONLY,
    )
    extended_values = SERVICE.transliterate_to_strings(
        extended,
        StandardScheme.ALA_LC,
        TransliterationStringOutput.WITHOUT_DIACRITICS_ONLY,
    )
    strict_only_full = SERVICE.transliterate_to_strings(strict_only, StandardScheme.ALA_LC)
    extended_full = SERVICE.transliterate_to_strings(extended, StandardScheme.ALA_LC)

    assert "Iarovoi Andrei" in strict_only_values
    assert "Iarovoi Andrei" in extended_values
    assert len(extended_full) > len(strict_only_full)


def test_generates_extended_variants_with_limit():
    req = request("Ель", include_extended=True, max_extended=5)
    result = SERVICE.transliterate(req, StandardScheme.ALA_LC)
    extended = result.extended_results

    assert len(extended) > 0
    assert len(extended) <= 5
    assert any(v.transliterated_text.startswith("Ye") for v in extended) or any(
        v.transliterated_text.startswith("Je") for v in extended
    )


def test_string_api_includes_extended_variants_when_enabled():
    req = request("Ель", include_extended=True, max_extended=10)
    values = SERVICE.transliterate_to_strings(req, StandardScheme.ALA_LC)

    assert "Elʹ" in values
    assert "Yel" in values
    assert len(values) > 1


def test_keeps_punctuation_and_digits_untouched():
    result = SERVICE.transliterate(
        request("Тест-123!", include_extended=False), StandardScheme.GOST_R_7034_2014
    )
    assert result.strict_results[0].transliterated_text == "Test-123!"


def test_matches_verified_name_examples():
    req = request("Яровой Андрей", include_extended=False)
    assert SERVICE.transliterate(req, StandardScheme.ICAO_DOC_9303).strict_results[
        0
    ].transliterated_text == "Iarovoi Andrei"
    assert SERVICE.transliterate(req, StandardScheme.ISO_9_GOST_779).strict_results[
        0
    ].transliterated_text == "Ârovoj Andrej"
    assert SERVICE.transliterate(req, StandardScheme.ALA_LC).strict_results[
        0
    ].transliterated_text == "I͡arovoĭ Andreĭ"
    assert SERVICE.transliterate(req, StandardScheme.BGN_PCGN_1947).strict_results[
        0
    ].transliterated_text == "Yarovoy Andrey"
    assert SERVICE.transliterate(req, StandardScheme.UNGEGN_1987).strict_results[
        0
    ].transliterated_text == "Jarovoj Andrej"
    assert SERVICE.transliterate(req, StandardScheme.GOST_R_52290_2004).strict_results[
        0
    ].transliterated_text == "Yarovoy Andrey"
    assert SERVICE.transliterate(req, StandardScheme.GOST_R_7034_2014).strict_results[
        0
    ].transliterated_text == "Yarovoj Andrej"
    assert SERVICE.transliterate(req, StandardScheme.GOST_16876_71).strict_results[
        0
    ].transliterated_text == "Ârovoj Andrej"
    assert SERVICE.transliterate(req, StandardScheme.SCHOLARLY).strict_results[
        0
    ].transliterated_text == "Jarovoj Andrej"
    assert SERVICE.transliterate(req, StandardScheme.YANDEX_MAPS_STYLE).strict_results[
        0
    ].transliterated_text == "Yarovoy Andrey"


def test_applies_yandex_specific_context_rules():
    value = SERVICE.transliterate(
        request("подъезд Щербаковский", include_extended=False),
        StandardScheme.YANDEX_MAPS_STYLE,
    ).strict_results[0].transliterated_text
    assert value == "podyezd Scherbakovskiy"


def test_applies_bgn_pcgn_context_rules():
    value = SERVICE.transliterate(
        request("Щёлково Подъём Юрьевич Хрущёв", include_extended=False),
        StandardScheme.BGN_PCGN_1947,
    ).strict_results[0].transliterated_text
    assert value == "Shchëlkovo Pod”yëm Yur’yevich Khrushchëv"


def test_applies_gost_52290_context_rules():
    value = SERVICE.transliterate(
        request("Щёлково Подъём Юрьевич Татьяна Хрущёв", include_extended=False),
        StandardScheme.GOST_R_52290_2004,
    ).strict_results[0].transliterated_text
    assert value == "Shchelkovo Pod'yom Yur'yevich Tat'yana Khrushchev"


def test_adds_diacritic_free_alternative_for_diacritic_schemes():
    strict = SERVICE.transliterate(
        request("Яровой Андрей", include_extended=True, max_extended=50),
        StandardScheme.ALA_LC,
    ).strict_results

    assert any(v.transliterated_text == "Iarovoi Andrei" for v in strict)
    assert any(not v.has_diacritic for v in strict)


def test_keeps_extended_variants_complete_when_limit_is_reached():
    extended = SERVICE.transliterate(
        request("улица Советская", include_extended=True, max_extended=12),
        StandardScheme.ALA_LC,
    ).extended_results

    assert not any(v.transliterated_text == "ulit͡sa Sove" for v in extended)
    assert all(" " in v.transliterated_text for v in extended)
    assert any(v.transliterated_text == "ulit͡sa Sovetskaya" for v in extended)


def test_exposes_has_diacritic_flag():
    strict = SERVICE.transliterate(
        request("Яровой Андрей", include_extended=True, max_extended=50),
        StandardScheme.ALA_LC,
    ).strict_results

    strict_with_diacritic = strict[0]
    strict_without_diacritic = next(
        (v for v in strict if v.transliterated_text == "Iarovoi Andrei"), None
    )
    assert strict_without_diacritic is not None
    assert strict_with_diacritic.has_diacritic is True
    assert strict_without_diacritic.has_diacritic is False


def test_validates_strict_regression_dataset_for_complex_words_across_all_schemes():
    cases = load_strict_regression_cases()
    assert len(cases) >= 260

    for scheme, source_text, expected_text in cases:
        result = SERVICE.transliterate(
            request(source_text, include_extended=False),
            scheme,
        )
        actual = result.strict_results[0].transliterated_text
        assert actual == expected_text, f"{scheme} -> {source_text}"


@pytest.mark.parametrize("scheme", list(StandardScheme))
def test_covers_russian_alphabet(scheme):
    result = SERVICE.transliterate(
        request("АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ", include_extended=False),
        scheme,
    )
    output = result.strict_results[0].transliterated_text

    assert output is not None
    assert output != ""
    assert re.search(r"[\u0400-\u052f]", output) is None


def load_strict_regression_cases():
    resource = (
        Path(__file__).resolve().parents[2]
        / "src"
        / "test"
        / "resources"
        / "strict_regression_cases.tsv"
    )
    cases = []
    for raw in resource.read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if not line:
            continue
        parts = line.split("\t", 2)
        if len(parts) != 3:
            raise RuntimeError(f"Invalid regression line: {raw}")
        scheme = StandardScheme[parts[0]]
        cases.append((scheme, parts[1], parts[2]))
    return cases
