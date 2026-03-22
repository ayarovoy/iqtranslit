from __future__ import annotations

from typing import Dict, Iterable, List

from .models import StandardScheme
from .scheme_ruleset import SchemeRuleSet


def _map(items: Iterable[tuple[str, str]]) -> Dict[str, str]:
    return {k: v for k, v in items}


def _base_practical() -> Dict[str, str]:
    return _map(
        [
            ("а", "a"),
            ("б", "b"),
            ("в", "v"),
            ("г", "g"),
            ("д", "d"),
            ("е", "e"),
            ("ё", "yo"),
            ("ж", "zh"),
            ("з", "z"),
            ("и", "i"),
            ("й", "y"),
            ("к", "k"),
            ("л", "l"),
            ("м", "m"),
            ("н", "n"),
            ("о", "o"),
            ("п", "p"),
            ("р", "r"),
            ("с", "s"),
            ("т", "t"),
            ("у", "u"),
            ("ф", "f"),
            ("х", "kh"),
            ("ц", "ts"),
            ("ч", "ch"),
            ("ш", "sh"),
            ("щ", "shch"),
            ("ъ", ""),
            ("ы", "y"),
            ("ь", ""),
            ("э", "e"),
            ("ю", "yu"),
            ("я", "ya"),
        ]
    )


def _icao_strict() -> Dict[str, str]:
    mapping = _base_practical()
    mapping["ё"] = "e"
    mapping["й"] = "i"
    mapping["ъ"] = "ie"
    mapping["ь"] = ""
    mapping["ю"] = "iu"
    mapping["я"] = "ia"
    return mapping


def _iso9_strict() -> Dict[str, str]:
    return _map(
        [
            ("а", "a"),
            ("б", "b"),
            ("в", "v"),
            ("г", "g"),
            ("д", "d"),
            ("е", "e"),
            ("ё", "ë"),
            ("ж", "ž"),
            ("з", "z"),
            ("и", "i"),
            ("й", "j"),
            ("к", "k"),
            ("л", "l"),
            ("м", "m"),
            ("н", "n"),
            ("о", "o"),
            ("п", "p"),
            ("р", "r"),
            ("с", "s"),
            ("т", "t"),
            ("у", "u"),
            ("ф", "f"),
            ("х", "h"),
            ("ц", "c"),
            ("ч", "č"),
            ("ш", "š"),
            ("щ", "ŝ"),
            ("ъ", "ʺ"),
            ("ы", "y"),
            ("ь", "ʹ"),
            ("э", "è"),
            ("ю", "û"),
            ("я", "â"),
        ]
    )


def _ala_lc_strict() -> Dict[str, str]:
    return _map(
        [
            ("а", "a"),
            ("б", "b"),
            ("в", "v"),
            ("г", "g"),
            ("д", "d"),
            ("е", "e"),
            ("ё", "ë"),
            ("ж", "zh"),
            ("з", "z"),
            ("и", "i"),
            ("й", "ĭ"),
            ("к", "k"),
            ("л", "l"),
            ("м", "m"),
            ("н", "n"),
            ("о", "o"),
            ("п", "p"),
            ("р", "r"),
            ("с", "s"),
            ("т", "t"),
            ("у", "u"),
            ("ф", "f"),
            ("х", "kh"),
            ("ц", "t͡s"),
            ("ч", "ch"),
            ("ш", "sh"),
            ("щ", "shch"),
            ("ъ", "ʺ"),
            ("ь", "ʹ"),
            ("ы", "y"),
            ("э", "ė"),
            ("ю", "i͡u"),
            ("я", "i͡a"),
        ]
    )


def _bgn_pcgn_strict() -> Dict[str, str]:
    mapping = _base_practical()
    mapping["е"] = "e"
    mapping["ё"] = "ë"
    mapping["й"] = "y"
    mapping["ъ"] = "”"
    mapping["ь"] = "’"
    return mapping


def _ungegn_strict() -> Dict[str, str]:
    return _map(
        [
            ("а", "a"),
            ("б", "b"),
            ("в", "v"),
            ("г", "g"),
            ("д", "d"),
            ("е", "e"),
            ("ё", "ë"),
            ("ж", "ž"),
            ("з", "z"),
            ("и", "i"),
            ("й", "j"),
            ("к", "k"),
            ("л", "l"),
            ("м", "m"),
            ("н", "n"),
            ("о", "o"),
            ("п", "p"),
            ("р", "r"),
            ("с", "s"),
            ("т", "t"),
            ("у", "u"),
            ("ф", "f"),
            ("х", "h"),
            ("ц", "c"),
            ("ч", "č"),
            ("ш", "š"),
            ("щ", "šč"),
            ("ъ", "ʺ"),
            ("ы", "y"),
            ("ь", "ʹ"),
            ("э", "è"),
            ("ю", "ju"),
            ("я", "ja"),
        ]
    )


def _gost_52290_strict() -> Dict[str, str]:
    mapping = _base_practical()
    mapping["е"] = "e"
    mapping["ё"] = "ye"
    mapping["й"] = "y"
    mapping["ъ"] = "'"
    mapping["ь"] = "'"
    return mapping


def _gost_7034_strict() -> Dict[str, str]:
    return _map(
        [
            ("а", "a"),
            ("б", "b"),
            ("в", "v"),
            ("г", "g"),
            ("д", "d"),
            ("е", "e"),
            ("ё", "yo"),
            ("ж", "zh"),
            ("з", "z"),
            ("и", "i"),
            ("й", "j"),
            ("к", "k"),
            ("л", "l"),
            ("м", "m"),
            ("н", "n"),
            ("о", "o"),
            ("п", "p"),
            ("р", "r"),
            ("с", "s"),
            ("т", "t"),
            ("у", "u"),
            ("ф", "f"),
            ("х", "x"),
            ("ц", "c"),
            ("ч", "ch"),
            ("ш", "sh"),
            ("щ", "shh"),
            ("ъ", "''"),
            ("ы", "y"),
            ("ь", "'"),
            ("э", "e"),
            ("ю", "yu"),
            ("я", "ya"),
        ]
    )


def _gost_16876_strict() -> Dict[str, str]:
    return _map(
        [
            ("а", "a"),
            ("б", "b"),
            ("в", "v"),
            ("г", "g"),
            ("д", "d"),
            ("е", "e"),
            ("ё", "ë"),
            ("ж", "ž"),
            ("з", "z"),
            ("и", "i"),
            ("й", "j"),
            ("к", "k"),
            ("л", "l"),
            ("м", "m"),
            ("н", "n"),
            ("о", "o"),
            ("п", "p"),
            ("р", "r"),
            ("с", "s"),
            ("т", "t"),
            ("у", "u"),
            ("ф", "f"),
            ("х", "h"),
            ("ц", "c"),
            ("ч", "č"),
            ("ш", "š"),
            ("щ", "ŝ"),
            ("ъ", "ʺ"),
            ("ы", "y"),
            ("ь", "ʹ"),
            ("э", "è"),
            ("ю", "û"),
            ("я", "â"),
        ]
    )


def _scholarly_strict() -> Dict[str, str]:
    mapping = _iso9_strict()
    mapping["щ"] = "šč"
    mapping["ю"] = "ju"
    mapping["я"] = "ja"
    return mapping


def _yandex_strict() -> Dict[str, str]:
    mapping = _base_practical()
    mapping["й"] = "y"
    mapping["щ"] = "sch"
    mapping["ъ"] = ""
    mapping["ь"] = ""
    return mapping


def _wikipedia_strict() -> Dict[str, str]:
    mapping = _base_practical()
    mapping["й"] = "i"
    mapping["ё"] = "yo"
    mapping["ъ"] = "ʺ"
    mapping["ь"] = "ʹ"
    return mapping


def _telegram_strict() -> Dict[str, str]:
    mapping = _base_practical()
    mapping["й"] = "i"
    mapping["ё"] = "yo"
    mapping["ъ"] = ""
    mapping["ь"] = ""
    return mapping


def _metro_strict() -> Dict[str, str]:
    mapping = _base_practical()
    mapping["ё"] = "e"
    mapping["й"] = "y"
    mapping["ъ"] = ""
    mapping["ь"] = ""
    return mapping


def _shared_extended() -> Dict[str, List[str]]:
    return {
        "е": ["e", "ye", "je", "ie"],
        "ё": ["e", "yo", "jo"],
        "ж": ["zh", "j"],
        "й": ["i", "y", "j"],
        "х": ["kh", "h", "x"],
        "ц": ["ts", "c", "cz"],
        "щ": ["shch", "sch", "shh"],
        "ъ": ["", "'", "ie"],
        "ь": ["", "'", "j"],
        "э": ["e", "eh"],
        "ю": ["yu", "iu", "ju"],
        "я": ["ya", "ia", "ja"],
    }


class RuleSetRegistry:
    _rules: Dict[StandardScheme, SchemeRuleSet] = {
        StandardScheme.ICAO_DOC_9303: SchemeRuleSet(
            StandardScheme.ICAO_DOC_9303, _icao_strict(), _shared_extended()
        ),
        StandardScheme.ISO_9_GOST_779: SchemeRuleSet(
            StandardScheme.ISO_9_GOST_779, _iso9_strict(), _shared_extended()
        ),
        StandardScheme.ALA_LC: SchemeRuleSet(
            StandardScheme.ALA_LC, _ala_lc_strict(), _shared_extended()
        ),
        StandardScheme.BGN_PCGN_1947: SchemeRuleSet(
            StandardScheme.BGN_PCGN_1947, _bgn_pcgn_strict(), _shared_extended()
        ),
        StandardScheme.UNGEGN_1987: SchemeRuleSet(
            StandardScheme.UNGEGN_1987, _ungegn_strict(), _shared_extended()
        ),
        StandardScheme.GOST_R_52290_2004: SchemeRuleSet(
            StandardScheme.GOST_R_52290_2004, _gost_52290_strict(), _shared_extended()
        ),
        StandardScheme.GOST_R_7034_2014: SchemeRuleSet(
            StandardScheme.GOST_R_7034_2014, _gost_7034_strict(), _shared_extended()
        ),
        StandardScheme.GOST_16876_71: SchemeRuleSet(
            StandardScheme.GOST_16876_71, _gost_16876_strict(), _shared_extended()
        ),
        StandardScheme.SCHOLARLY: SchemeRuleSet(
            StandardScheme.SCHOLARLY, _scholarly_strict(), _shared_extended()
        ),
        StandardScheme.YANDEX_MAPS_STYLE: SchemeRuleSet(
            StandardScheme.YANDEX_MAPS_STYLE, _yandex_strict(), _shared_extended()
        ),
        StandardScheme.WIKIPEDIA_STYLE: SchemeRuleSet(
            StandardScheme.WIKIPEDIA_STYLE, _wikipedia_strict(), _shared_extended()
        ),
        StandardScheme.TELEGRAM_STYLE: SchemeRuleSet(
            StandardScheme.TELEGRAM_STYLE, _telegram_strict(), _shared_extended()
        ),
        StandardScheme.MOSCOW_METRO_STYLE: SchemeRuleSet(
            StandardScheme.MOSCOW_METRO_STYLE, _metro_strict(), _shared_extended()
        ),
    }

    @classmethod
    def for_scheme(cls, scheme: StandardScheme) -> SchemeRuleSet:
        if scheme not in cls._rules:
            raise ValueError("Unsupported scheme: %s" % scheme)
        return cls._rules[scheme]

    @classmethod
    def supported_schemes(cls) -> List[StandardScheme]:
        return list(cls._rules.keys())
