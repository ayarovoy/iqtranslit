from .models import (
    StandardScheme,
    TransliterationRequest,
    TransliterationResult,
    TransliterationStringOutput,
    TransliterationVariant,
)
from .service import DefaultTransliterationService, TransliterationService


def create_default_service() -> TransliterationService:
    return DefaultTransliterationService()


__all__ = [
    "DefaultTransliterationService",
    "StandardScheme",
    "TransliterationRequest",
    "TransliterationResult",
    "TransliterationService",
    "TransliterationStringOutput",
    "TransliterationVariant",
    "create_default_service",
]
