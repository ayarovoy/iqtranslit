package ru.iqtranslit.api;

import ru.iqtranslit.model.StandardScheme;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public final class TransliterationRequest {
    private static final int DEFAULT_MAX_EXTENDED_PER_SCHEME = 64;

    private final String sourceText;
    private final boolean includeExtended;
    private final int maxExtendedVariantsPerScheme;
    private final Set<StandardScheme> schemes;

    private TransliterationRequest(Builder builder) {
        if (builder.sourceText == null) {
            throw new IllegalArgumentException("sourceText must not be null");
        }
        if (builder.maxExtendedVariantsPerScheme <= 0) {
            throw new IllegalArgumentException("maxExtendedVariantsPerScheme must be positive");
        }
        this.sourceText = builder.sourceText;
        this.includeExtended = builder.includeExtended;
        this.maxExtendedVariantsPerScheme = builder.maxExtendedVariantsPerScheme;
        this.schemes = builder.schemes.isEmpty()
                ? Collections.emptySet()
                : Collections.unmodifiableSet(EnumSet.copyOf(builder.schemes));
    }

    public String getSourceText() {
        return sourceText;
    }

    public boolean isIncludeExtended() {
        return includeExtended;
    }

    public int getMaxExtendedVariantsPerScheme() {
        return maxExtendedVariantsPerScheme;
    }

    public Set<StandardScheme> getSchemes() {
        return schemes;
    }

    public static Builder builder(String sourceText) {
        return new Builder(sourceText);
    }

    public static final class Builder {
        private final String sourceText;
        private boolean includeExtended;
        private int maxExtendedVariantsPerScheme = DEFAULT_MAX_EXTENDED_PER_SCHEME;
        private Set<StandardScheme> schemes = EnumSet.noneOf(StandardScheme.class);

        private Builder(String sourceText) {
            this.sourceText = sourceText;
        }

        public Builder includeExtended(boolean includeExtended) {
            this.includeExtended = includeExtended;
            return this;
        }

        public Builder maxExtendedVariantsPerScheme(int maxExtendedVariantsPerScheme) {
            this.maxExtendedVariantsPerScheme = maxExtendedVariantsPerScheme;
            return this;
        }

        public Builder schemes(Set<StandardScheme> schemes) {
            this.schemes = schemes == null || schemes.isEmpty()
                    ? EnumSet.noneOf(StandardScheme.class)
                    : EnumSet.copyOf(schemes);
            return this;
        }

        public TransliterationRequest build() {
            return new TransliterationRequest(this);
        }
    }
}
