# iqtranslit

Java 8 библиотека транслитерации с кириллицы на латиницу.

## Возможности

- строгая транслитерация (`strict`) по всем поддерживаемым схемам;
- расширенная генерация (`extended`) альтернативных вариантов;
- автоматическое добавление вариантов без диакритики для схем, где используются диакритические знаки;
- выбор конкретной схемы или получение результатов по всем схемам;
- лимитирование и дедупликация расширенных вариантов.

Подробные алгоритмы и таблицы подстановок: `STANDARDS.md`.

## Поддерживаемые схемы

См. `ru.iqtranslit.model.StandardScheme`.

Включены:

- формальные: `ICAO`, `ISO 9 / ГОСТ 7.79`, `ALA-LC`, `BGN/PCGN`, `UNGEGN`, `ГОСТ Р 52290`, `ГОСТ Р 7.0.34`, `ГОСТ 16876`;
- прикладные: `SCHOLARLY`, `YANDEX_MAPS_STYLE`, `WIKIPEDIA_STYLE`, `TELEGRAM_STYLE`, `MOSCOW_METRO_STYLE`.

## Использование

```java
import ru.iqtranslit.IqTranslit;
import ru.iqtranslit.api.TransliterationRequest;
import ru.iqtranslit.api.TransliterationResult;
import ru.iqtranslit.api.TransliterationService;

TransliterationService service = IqTranslit.createDefaultService();

TransliterationRequest request = TransliterationRequest.builder("Ель")
        .includeExtended(true)
        .maxExtendedVariantsPerScheme(10)
        .build();

TransliterationResult result = service.transliterateAll(request);
```

## API

- `TransliterationService#transliterateAll(request)`:
  - strict-результат по каждой схеме;
  - optional extended-результаты по каждой схеме, включая варианты без диакритики для диакритических схем.
- `TransliterationService#transliterate(request, scheme)`:
  - strict/extended только для выбранной схемы.
- `TransliterationService#transliterateToStrings(...)`:
  - возвращает уникальные строки транслитерации;
  - учитывает `includeExtended(...)` из `TransliterationRequest`;
  - поддерживает режимы `FULL` и `WITHOUT_DIACRITICS_ONLY`.

## Сборка и тесты

```bash
gradle test
```

## Python-реализация

В репозитории также есть Python-реализация с алгоритмическим паритетом к Java-версии:

- пакет: `python/src/iqtranslit_py`;
- тесты: `python/tests/test_transliteration_service.py`;
- регрессионный набор strict используется общий: `src/test/resources/strict_regression_cases.tsv`.

Запуск Python-тестов:

```bash
cd python
python3 -m pytest
```
