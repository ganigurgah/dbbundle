package com.valora.library.dbbundle.spi;

import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Map;

public interface TranslationProvider {
    /**
     * Belirli bir locale için tüm çevirileri yükler.
     */
    Map<String,Object> loadTranslations(Locale locale);

    /**
     * Veritabanına yeni bir anahtar ekler veya mevcut olanı günceller.
     */
    void saveTranslation(String key, String value, String locale);
}