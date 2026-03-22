package ru.iqtranslit;

import ru.iqtranslit.api.TransliterationService;
import ru.iqtranslit.engine.DefaultTransliterationService;

public final class IqTranslit {
    private IqTranslit() {
    }

    public static TransliterationService createDefaultService() {
        return new DefaultTransliterationService();
    }
}
