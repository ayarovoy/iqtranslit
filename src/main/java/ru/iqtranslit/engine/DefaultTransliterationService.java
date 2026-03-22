package ru.iqtranslit.engine;

import ru.iqtranslit.api.TransliterationRequest;
import ru.iqtranslit.api.TransliterationResult;
import ru.iqtranslit.api.TransliterationService;
import ru.iqtranslit.api.TransliterationStringOutput;
import ru.iqtranslit.api.TransliterationVariant;
import ru.iqtranslit.model.StandardScheme;
import ru.iqtranslit.rules.RuleSetRegistry;
import ru.iqtranslit.rules.SchemeRuleSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class DefaultTransliterationService implements TransliterationService {
    private final TransliterationEngine engine;

    public DefaultTransliterationService() {
        this.engine = new TransliterationEngine();
    }

    @Override
    public TransliterationResult transliterateAll(TransliterationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        List<StandardScheme> schemes = resolveSchemes(request.getSchemes());

        List<TransliterationVariant> strictResults = new ArrayList<TransliterationVariant>();
        List<TransliterationVariant> extendedResults = new ArrayList<TransliterationVariant>();

        for (StandardScheme scheme : schemes) {
            SchemeRuleSet ruleSet = RuleSetRegistry.forScheme(scheme);
            String strictValue = engine.transliterateStrict(request.getSourceText(), ruleSet);
            TransliterationVariant strictVariant =
                    new TransliterationVariant(scheme, request.getSourceText(), strictValue);
            strictResults.add(strictVariant);
            addStrictDiacriticFallback(strictResults, strictVariant);

            if (request.isIncludeExtended()) {
                List<String> extended =
                        engine.transliterateExtended(
                                request.getSourceText(),
                                ruleSet,
                                request.getMaxExtendedVariantsPerScheme(),
                                strictValue
                        );
                for (String value : enrichWithDiacriticFreeVariants(
                        strictValue,
                        extended,
                        request.getMaxExtendedVariantsPerScheme()
                )) {
                    extendedResults.add(new TransliterationVariant(scheme, request.getSourceText(), value));
                }
            }
        }

        return new TransliterationResult(request.getSourceText(), strictResults, extendedResults);
    }

    @Override
    public TransliterationResult transliterate(TransliterationRequest request, StandardScheme scheme) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (scheme == null) {
            throw new IllegalArgumentException("scheme must not be null");
        }

        TransliterationRequest scopedRequest = TransliterationRequest.builder(request.getSourceText())
                .includeExtended(request.isIncludeExtended())
                .maxExtendedVariantsPerScheme(request.getMaxExtendedVariantsPerScheme())
                .schemes(Collections.singleton(scheme))
                .build();
        return transliterateAll(scopedRequest);
    }

    @Override
    public List<String> transliterateToStrings(
            TransliterationRequest request,
            TransliterationStringOutput output
    ) {
        return toDistinctStrings(transliterateAll(request), output);
    }

    @Override
    public List<String> transliterateToStrings(
            TransliterationRequest request,
            StandardScheme scheme,
            TransliterationStringOutput output
    ) {
        return toDistinctStrings(transliterate(request, scheme), output);
    }

    private List<StandardScheme> resolveSchemes(Set<StandardScheme> requestedSchemes) {
        if (requestedSchemes == null || requestedSchemes.isEmpty()) {
            return RuleSetRegistry.supportedSchemes();
        }
        return new ArrayList<StandardScheme>(requestedSchemes);
    }

    private List<String> toDistinctStrings(TransliterationResult result, TransliterationStringOutput output) {
        if (output == null) {
            throw new IllegalArgumentException("output must not be null");
        }

        LinkedHashSet<String> values = new LinkedHashSet<String>();
        addVariantStrings(values, result.getStrictResults(), output);
        addVariantStrings(values, result.getExtendedResults(), output);
        return Collections.unmodifiableList(new ArrayList<String>(values));
    }

    private void addVariantStrings(
            Set<String> values,
            List<TransliterationVariant> variants,
            TransliterationStringOutput output
    ) {
        for (TransliterationVariant variant : variants) {
            if (output == TransliterationStringOutput.FULL || !variant.hasDiacritic()) {
                values.add(variant.getTransliteratedText());
            }
        }
    }

    private void addStrictDiacriticFallback(
            List<TransliterationVariant> strictResults,
            TransliterationVariant strictVariant
    ) {
        if (!strictVariant.hasDiacritic()) {
            return;
        }
        String simplified = DiacriticSimplifier.simplify(strictVariant.getTransliteratedText());
        if (simplified.equals(strictVariant.getTransliteratedText())) {
            return;
        }
        strictResults.add(new TransliterationVariant(
                strictVariant.getScheme(),
                strictVariant.getSourceText(),
                simplified
        ));
    }

    private List<String> enrichWithDiacriticFreeVariants(String strictValue, List<String> extended, int limit) {
        LinkedHashSet<String> values = new LinkedHashSet<String>(extended);

        List<String> snapshot = new ArrayList<String>(values);
        for (String value : snapshot) {
            addSimplifiedVariant(values, value, strictValue);
            if (values.size() >= limit) {
                break;
            }
        }

        List<String> result = new ArrayList<String>(values);
        if (result.size() > limit) {
            return new ArrayList<String>(result.subList(0, limit));
        }
        return result;
    }

    private void addSimplifiedVariant(Set<String> values, String candidate, String strictValue) {
        String simplified = DiacriticSimplifier.simplify(candidate);
        if (!simplified.equals(candidate) && !simplified.equals(strictValue)) {
            values.add(simplified);
        }
    }
}
