import unicodedata


def simplify(value: str) -> str:
    normalized = unicodedata.normalize("NFD", value)
    out = []
    for ch in normalized:
        category = unicodedata.category(ch)
        if category in ("Mn", "Mc", "Me"):
            continue
        if ch == "\u02BA":
            out.append('"')
            continue
        if ch == "\u02B9":
            out.append("'")
            continue
        out.append(ch)
    return "".join(out)


def detect_diacritic(value: str) -> bool:
    normalized = unicodedata.normalize("NFD", value)
    for ch in normalized:
        category = unicodedata.category(ch)
        if category in ("Mn", "Mc", "Me"):
            return True
    for ch in value:
        if ch in ("\u02BA", "\u02B9", "\u0361"):
            return True
    return False
