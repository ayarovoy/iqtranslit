package ru.iqtranslit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.iqtranslit.api.TransliterationRequest;
import ru.iqtranslit.api.TransliterationResult;
import ru.iqtranslit.api.TransliterationService;
import ru.iqtranslit.api.TransliterationStringOutput;
import ru.iqtranslit.api.TransliterationVariant;
import ru.iqtranslit.model.StandardScheme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransliterationServiceTest {
    private static final List<StrictRegressionCase> STRICT_REGRESSION_CASES = loadStrictRegressionCases();
    private final TransliterationService service = IqTranslit.createDefaultService();

    @Test
    void returnsStrictResultForEveryScheme() {
        TransliterationRequest request = TransliterationRequest.builder("Привет, Ёжик!")
                .includeExtended(false)
                .build();

        TransliterationResult result = service.transliterateAll(request);

        assertEquals(
                StandardScheme.values().length,
                result.getStrictResults().stream()
                        .map(TransliterationVariant::getScheme)
                        .distinct()
                        .count()
        );
        assertTrue(result.getExtendedResults().isEmpty());
    }

    @Test
    void supportsSingleSchemeMode() {
        TransliterationRequest request = TransliterationRequest.builder("Щука")
                .includeExtended(false)
                .build();

        TransliterationResult result = service.transliterate(request, StandardScheme.ICAO_DOC_9303);
        List<TransliterationVariant> strict = result.getStrictResults();

        assertEquals(1, strict.size());
        assertEquals("Shchuka", strict.get(0).getTransliteratedText());
    }

    @Test
    void returnsDistinctStringsAcrossSchemes() {
        TransliterationRequest request = TransliterationRequest.builder("Яровой Андрей")
                .includeExtended(true)
                .maxExtendedVariantsPerScheme(50)
                .build();

        TransliterationResult rawResult = service.transliterateAll(request);
        List<String> values = service.transliterateToStrings(request);

        int rawCount = rawResult.getStrictResults().size() + rawResult.getExtendedResults().size();

        assertEquals(values.size(), new LinkedHashSet<String>(values).size());
        assertTrue(values.size() < rawCount);
        assertTrue(values.contains("Yarovoy Andrey"));
    }

    @Test
    void returnsOnlyDiacriticFreeStringsWhenRequested() {
        TransliterationRequest request = TransliterationRequest.builder("Яровой Андрей")
                .includeExtended(true)
                .maxExtendedVariantsPerScheme(50)
                .build();

        List<String> values = service.transliterateToStrings(
                request,
                StandardScheme.ALA_LC,
                TransliterationStringOutput.WITHOUT_DIACRITICS_ONLY
        );

        assertTrue(values.contains("Iarovoi Andrei"));
        assertFalse(values.contains("I͡arovoĭ Andreĭ"));
    }

    @Test
    void stringApiRespectsExtendedToggle() {
        TransliterationRequest strictOnlyRequest = TransliterationRequest.builder("Яровой Андрей")
                .includeExtended(false)
                .build();
        TransliterationRequest extendedRequest = TransliterationRequest.builder("Яровой Андрей")
                .includeExtended(true)
                .maxExtendedVariantsPerScheme(50)
                .build();

        List<String> strictOnlyValues = service.transliterateToStrings(
                strictOnlyRequest,
                StandardScheme.ALA_LC,
                TransliterationStringOutput.WITHOUT_DIACRITICS_ONLY
        );
        List<String> extendedValues = service.transliterateToStrings(
                extendedRequest,
                StandardScheme.ALA_LC,
                TransliterationStringOutput.WITHOUT_DIACRITICS_ONLY
        );
        List<String> strictOnlyFull = service.transliterateToStrings(strictOnlyRequest, StandardScheme.ALA_LC);
        List<String> extendedFull = service.transliterateToStrings(extendedRequest, StandardScheme.ALA_LC);

        assertTrue(strictOnlyValues.contains("Iarovoi Andrei"));
        assertTrue(extendedValues.contains("Iarovoi Andrei"));
        assertTrue(extendedFull.size() > strictOnlyFull.size());
    }

    @Test
    void generatesExtendedVariantsWithLimit() {
        TransliterationRequest request = TransliterationRequest.builder("Ель")
                .includeExtended(true)
                .maxExtendedVariantsPerScheme(5)
                .build();

        TransliterationResult result = service.transliterate(request, StandardScheme.ALA_LC);
        List<TransliterationVariant> extended = result.getExtendedResults();

        assertFalse(extended.isEmpty());
        assertTrue(extended.size() <= 5);
        assertTrue(
                extended.stream().anyMatch(v -> v.getTransliteratedText().startsWith("Ye"))
                        || extended.stream().anyMatch(v -> v.getTransliteratedText().startsWith("Je"))
        );
    }

    @Test
    void stringApiIncludesExtendedVariantsWhenEnabled() {
        TransliterationRequest request = TransliterationRequest.builder("Ель")
                .includeExtended(true)
                .maxExtendedVariantsPerScheme(10)
                .build();

        List<String> values = service.transliterateToStrings(request, StandardScheme.ALA_LC);

        assertTrue(values.contains("Elʹ"));
        assertTrue(values.contains("Yel"));
        assertTrue(values.size() > 1);
    }

    @Test
    void keepsPunctuationAndDigitsUntouched() {
        TransliterationRequest request = TransliterationRequest.builder("Тест-123!")
                .includeExtended(false)
                .build();

        TransliterationResult result = service.transliterate(request, StandardScheme.GOST_R_7034_2014);

        assertEquals("Test-123!", result.getStrictResults().get(0).getTransliteratedText());
    }

    @Test
    void matchesVerifiedNameExamples() {
        TransliterationRequest request = TransliterationRequest.builder("Яровой Андрей")
                .includeExtended(false)
                .build();

        assertEquals("Iarovoi Andrei", service.transliterate(request, StandardScheme.ICAO_DOC_9303)
                .getStrictResults().get(0).getTransliteratedText());
        assertEquals("Ârovoj Andrej", service.transliterate(request, StandardScheme.ISO_9_GOST_779)
                .getStrictResults().get(0).getTransliteratedText());
        assertEquals("I͡arovoĭ Andreĭ", service.transliterate(request, StandardScheme.ALA_LC)
                .getStrictResults().get(0).getTransliteratedText());
        assertEquals("Yarovoy Andrey", service.transliterate(request, StandardScheme.BGN_PCGN_1947)
                .getStrictResults().get(0).getTransliteratedText());
        assertEquals("Jarovoj Andrej", service.transliterate(request, StandardScheme.UNGEGN_1987)
                .getStrictResults().get(0).getTransliteratedText());
        assertEquals("Yarovoy Andrey", service.transliterate(request, StandardScheme.GOST_R_52290_2004)
                .getStrictResults().get(0).getTransliteratedText());
        assertEquals("Yarovoj Andrej", service.transliterate(request, StandardScheme.GOST_R_7034_2014)
                .getStrictResults().get(0).getTransliteratedText());
        assertEquals("Ârovoj Andrej", service.transliterate(request, StandardScheme.GOST_16876_71)
                .getStrictResults().get(0).getTransliteratedText());
        assertEquals("Jarovoj Andrej", service.transliterate(request, StandardScheme.SCHOLARLY)
                .getStrictResults().get(0).getTransliteratedText());
        assertEquals("Yarovoy Andrey", service.transliterate(request, StandardScheme.YANDEX_MAPS_STYLE)
                .getStrictResults().get(0).getTransliteratedText());
    }

    @Test
    void appliesYandexSpecificContextRules() {
        TransliterationRequest request = TransliterationRequest.builder("подъезд Щербаковский")
                .includeExtended(false)
                .build();

        String value = service.transliterate(request, StandardScheme.YANDEX_MAPS_STYLE)
                .getStrictResults().get(0).getTransliteratedText();

        assertEquals("podyezd Scherbakovskiy", value);
    }

    @Test
    void appliesBgnPcgnContextRules() {
        TransliterationRequest request = TransliterationRequest.builder("Щёлково Подъём Юрьевич Хрущёв")
                .includeExtended(false)
                .build();

        String value = service.transliterate(request, StandardScheme.BGN_PCGN_1947)
                .getStrictResults().get(0).getTransliteratedText();

        assertEquals("Shchëlkovo Pod”yëm Yur’yevich Khrushchëv", value);
    }

    @Test
    void appliesGost52290ContextRules() {
        TransliterationRequest request = TransliterationRequest.builder("Щёлково Подъём Юрьевич Татьяна Хрущёв")
                .includeExtended(false)
                .build();

        String value = service.transliterate(request, StandardScheme.GOST_R_52290_2004)
                .getStrictResults().get(0).getTransliteratedText();

        assertEquals("Shchelkovo Pod'yom Yur'yevich Tat'yana Khrushchev", value);
    }

    @Test
    void addsDiacriticFreeAlternativeForDiacriticSchemes() {
        TransliterationRequest request = TransliterationRequest.builder("Яровой Андрей")
                .includeExtended(true)
                .maxExtendedVariantsPerScheme(50)
                .build();

        List<TransliterationVariant> strict = service.transliterate(request, StandardScheme.ALA_LC)
                .getStrictResults();

        assertTrue(strict.stream().anyMatch(v -> "Iarovoi Andrei".equals(v.getTransliteratedText())));
        assertTrue(strict.stream().anyMatch(v -> !v.hasDiacritic()));
    }

    @Test
    void keepsExtendedVariantsCompleteWhenLimitIsReached() {
        TransliterationRequest request = TransliterationRequest.builder("улица Советская")
                .includeExtended(true)
                .maxExtendedVariantsPerScheme(12)
                .build();

        List<TransliterationVariant> extended = service.transliterate(request, StandardScheme.ALA_LC)
                .getExtendedResults();

        assertFalse(extended.stream().anyMatch(v -> "ulit͡sa Sove".equals(v.getTransliteratedText())));
        assertTrue(extended.stream().allMatch(v -> v.getTransliteratedText().contains(" ")));
        assertTrue(extended.stream().anyMatch(v -> "ulit͡sa Sovetskaya".equals(v.getTransliteratedText())));
    }

    @Test
    void exposesHasDiacriticFlag() {
        TransliterationRequest request = TransliterationRequest.builder("Яровой Андрей")
                .includeExtended(true)
                .maxExtendedVariantsPerScheme(50)
                .build();

        List<TransliterationVariant> strict = service.transliterate(request, StandardScheme.ALA_LC)
                .getStrictResults();
        TransliterationVariant strictWithDiacritic = strict.get(0);
        TransliterationVariant strictWithoutDiacritic = strict.stream()
                .filter(v -> "Iarovoi Andrei".equals(v.getTransliteratedText()))
                .findFirst()
                .orElseThrow(new java.util.function.Supplier<AssertionError>() {
                    @Override
                    public AssertionError get() {
                        return new AssertionError("ASCII fallback variant not found");
                    }
                });

        assertTrue(strictWithDiacritic.hasDiacritic());
        assertFalse(strictWithoutDiacritic.hasDiacritic());
    }

    @Test
    void validatesStrictRegressionDatasetForComplexWordsAcrossAllSchemes() {
        assertTrue(STRICT_REGRESSION_CASES.size() >= 260);

        for (StrictRegressionCase regressionCase : STRICT_REGRESSION_CASES) {
            TransliterationResult result = service.transliterate(
                    TransliterationRequest.builder(regressionCase.sourceText)
                            .includeExtended(false)
                            .build(),
                    regressionCase.scheme
            );

            String actual = result.getStrictResults().get(0).getTransliteratedText();
            assertEquals(
                    regressionCase.expectedText,
                    actual,
                    regressionCase.scheme + " -> " + regressionCase.sourceText
            );
        }
    }

    @ParameterizedTest
    @EnumSource(StandardScheme.class)
    void coversRussianAlphabet(StandardScheme scheme) {
        TransliterationRequest request = TransliterationRequest.builder("АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ")
                .includeExtended(false)
                .build();

        TransliterationResult result = service.transliterate(request, scheme);
        String output = result.getStrictResults().get(0).getTransliteratedText();

        assertNotNull(output);
        assertFalse(output.isEmpty());
        assertFalse(output.matches(".*[\\p{IsCyrillic}].*"));
    }

    private static List<StrictRegressionCase> loadStrictRegressionCases() {
        InputStream stream = TransliterationServiceTest.class.getResourceAsStream("/strict_regression_cases.tsv");
        if (stream == null) {
            throw new IllegalStateException("strict_regression_cases.tsv not found");
        }

        List<StrictRegressionCase> cases = new ArrayList<StrictRegressionCase>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\t", 3);
                if (parts.length != 3) {
                    throw new IllegalStateException("Invalid regression line: " + line);
                }
                cases.add(new StrictRegressionCase(
                        StandardScheme.valueOf(parts[0]),
                        parts[1],
                        parts[2]
                ));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read regression cases", e);
        }
        return Collections.unmodifiableList(cases);
    }

    private static final class StrictRegressionCase {
        private final StandardScheme scheme;
        private final String sourceText;
        private final String expectedText;

        private StrictRegressionCase(StandardScheme scheme, String sourceText, String expectedText) {
            this.scheme = scheme;
            this.sourceText = sourceText;
            this.expectedText = expectedText;
        }
    }
}
