package ru.iqtranslit.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TransliterationResult {
    private final String sourceText;
    private final List<TransliterationVariant> strictResults;
    private final List<TransliterationVariant> extendedResults;

    public TransliterationResult(
            String sourceText,
            List<TransliterationVariant> strictResults,
            List<TransliterationVariant> extendedResults
    ) {
        if (sourceText == null) {
            throw new IllegalArgumentException("sourceText must not be null");
        }
        this.sourceText = sourceText;
        this.strictResults = Collections.unmodifiableList(new ArrayList<TransliterationVariant>(strictResults));
        this.extendedResults = Collections.unmodifiableList(new ArrayList<TransliterationVariant>(extendedResults));
    }

    public String getSourceText() {
        return sourceText;
    }

    public List<TransliterationVariant> getStrictResults() {
        return strictResults;
    }

    public List<TransliterationVariant> getExtendedResults() {
        return extendedResults;
    }
}
