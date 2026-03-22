# iqtranslit

Библиотека транслитерации русского текста (кириллица -> латиница) с двумя реализациями:

- Java 8 (основная библиотека);
- Python (алгоритмический паритет с Java).

Проект покрывает формальные стандарты и прикладные схемы, умеет возвращать как строгий детерминированный результат, так и набор реалистичных альтернатив.

Подробная спецификация алгоритма и таблиц соответствий: `STANDARDS.md`.

## Что умеет библиотека

- `strict`-транслитерация: один детерминированный результат на схему;
- `extended`-транслитерация: генерация альтернатив для неоднозначных букв (`е`, `ё`, `й`, `х`, `ц`, `щ`, `ю`, `я`, `ъ`, `ь` и др.);
- автоматический fallback без диакритики для результатов с диакритическими символами;
- работа по всем схемам сразу или по выбранной схеме;
- лимитирование расширенных вариантов `maxExtendedVariantsPerScheme` и дедупликация в стабильном порядке;
- сохранение нецелевых символов без изменений (цифры, пробелы, знаки препинания, латиница).

## Поддерживаемые схемы

Enum: `ru.iqtranslit.model.StandardScheme` (Java) и `iqtranslit_py.StandardScheme` (Python).

- `ICAO_DOC_9303`
- `ISO_9_GOST_779`
- `ALA_LC`
- `BGN_PCGN_1947`
- `UNGEGN_1987`
- `GOST_R_52290_2004`
- `GOST_R_7034_2014`
- `GOST_16876_71`
- `SCHOLARLY`
- `YANDEX_MAPS_STYLE`
- `WIKIPEDIA_STYLE`
- `TELEGRAM_STYLE`
- `MOSCOW_METRO_STYLE`

## Алгоритм в двух словах

- `strict`: посимвольная транслитерация слева направо по таблице схемы с сохранением регистра;
- `extended`: строится на основе `strict`, затем добавляет допустимые альтернативы и их комбинации с лимитом;
- для схем с диакритикой в выдачу автоматически добавляются варианты без диакритики (и в `strict`, и в `extended`, согласно `STANDARDS.md`).

Пример поведения:

- вход `Щука` -> `ICAO_DOC_9303`: `Shchuka`;
- вход `Ель` -> в `extended` появляются варианты вида `Yel`, `Jel`, `Iel`;
- вход `Тест-123!` -> `-123!` сохраняется без изменений.

## Java: как использовать

### Требования

- JDK 8+
- Gradle (или `./gradlew`)

### Быстрый старт

```java
import ru.iqtranslit.IqTranslit;
import ru.iqtranslit.api.TransliterationRequest;
import ru.iqtranslit.api.TransliterationResult;
import ru.iqtranslit.api.TransliterationService;
import ru.iqtranslit.model.StandardScheme;

import java.util.Collections;

TransliterationService service = IqTranslit.createDefaultService();

TransliterationRequest request = TransliterationRequest.builder("Ель")
        .includeExtended(true)
        .maxExtendedVariantsPerScheme(10)
        .schemes(Collections.singleton(StandardScheme.GOST_R_7034_2014))
        .build();

TransliterationResult result = service.transliterateAll(request);
```

### Основной API

- `transliterateAll(request)` - возвращает результат по всем схемам (или по списку из `request.schemes`);
- `transliterate(request, scheme)` - возвращает результат по одной схеме;
- `transliterateToStrings(request)` - возвращает уникальные строки по всем схемам;
- `transliterateToStrings(request, scheme)` - уникальные строки для одной схемы;
- `transliterateToStrings(request, output)` и `transliterateToStrings(request, scheme, output)`:
  - `FULL` - все строки;
  - `WITHOUT_DIACRITICS_ONLY` - только варианты без диакритики.

### Пример: получить только строки без диакритики

```java
import ru.iqtranslit.api.TransliterationStringOutput;

TransliterationRequest request = TransliterationRequest.builder("Яровой Андрей")
        .includeExtended(true)
        .maxExtendedVariantsPerScheme(50)
        .build();

List<String> values = service.transliterateToStrings(
        request,
        StandardScheme.ALA_LC,
        TransliterationStringOutput.WITHOUT_DIACRITICS_ONLY
);
```

### Сборка и тесты (Java)

```bash
./gradlew test
```

## Python: как использовать

Python-реализация находится в `python/src/iqtranslit_py` и повторяет поведение Java-версии:

- те же схемы `StandardScheme`;
- тот же контракт `strict/extended`;
- тот же принцип выдачи строк `FULL` / `WITHOUT_DIACRITICS_ONLY`.

### Требования

- Python 3.8+

### Установка для локальной разработки

```bash
cd python
python3 -m venv .venv
source .venv/bin/activate
pip install -U pip
pip install -e .
```

Установка с тестовыми зависимостями:

```bash
pip install -e .[test]
```

### Пример использования Python API

```python
from iqtranslit_py import (
    StandardScheme,
    TransliterationRequest,
    TransliterationStringOutput,
    create_default_service,
)

service = create_default_service()

request = (
    TransliterationRequest.builder("Яровой Андрей")
    .include_extended(True)
    .max_extended_variants_per_scheme(50)
    .build()
)

result = service.transliterate(request, StandardScheme.ALA_LC)
print(result.strict_results[0].transliterated_text)

values = service.transliterate_to_strings(
    request,
    StandardScheme.ALA_LC,
    TransliterationStringOutput.WITHOUT_DIACRITICS_ONLY,
)
print(values)
```

### Запуск тестов Python

```bash
cd python
python3 -m pytest
```

## Где смотреть детали правил

- полный алгоритм `strict` и `extended`: `STANDARDS.md`;
- исходная справка по стандартам и областям применения: `STANDARTS.md`;
- Java API: `src/main/java/ru/iqtranslit/api`;
- Python API: `python/src/iqtranslit_py`.
