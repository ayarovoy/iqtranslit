package ru.iqtranslit.api;

import ru.iqtranslit.model.StandardScheme;

import java.util.List;

public interface TransliterationService {
    TransliterationResult transliterateAll(TransliterationRequest request);

    TransliterationResult transliterate(TransliterationRequest request, StandardScheme scheme);

    default List<String> transliterateToStrings(TransliterationRequest request) {
        return transliterateToStrings(request, TransliterationStringOutput.FULL);
    }

    List<String> transliterateToStrings(
            TransliterationRequest request,
            TransliterationStringOutput output
    );

    default List<String> transliterateToStrings(TransliterationRequest request, StandardScheme scheme) {
        return transliterateToStrings(request, scheme, TransliterationStringOutput.FULL);
    }

    List<String> transliterateToStrings(
            TransliterationRequest request,
            StandardScheme scheme,
            TransliterationStringOutput output
    );
}
