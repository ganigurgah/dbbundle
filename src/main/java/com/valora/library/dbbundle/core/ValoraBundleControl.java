package com.valora.library.dbbundle.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.valora.library.dbbundle.context.LocaleContext;
import com.valora.library.dbbundle.spi.TranslationProvider;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ValoraBundleControl extends ResourceBundle.Control {

    private final TranslationProvider provider;
    private final Cache<Locale, ResourceBundle> bundleCache;

    public ValoraBundleControl(TranslationProvider provider, int cacheDurationInMinutes) {
        this.provider = provider;
        this.bundleCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheDurationInMinutes, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) {
        /*
        if (locale == null || locale.getLanguage().trim().isEmpty())
            return null;
        Locale tmpLocale = Locale.of(locale.getLanguage());
        ResourceBundle cachedBundle = bundleCache.asMap().get(tmpLocale);
        if (cachedBundle != null) {
            return cachedBundle;
        }
        cachedBundle = bundleCache.get(tmpLocale, loc -> {
            Map<String, Object> data = provider.loadTranslations(loc);

            if (data == null || data.isEmpty()) {
                return new ValoraMapResourceBundle(new HashMap<>());
            }
            return new ValoraMapResourceBundle(data);
        });
        bundleCache.put(tmpLocale, cachedBundle);
        return cachedBundle;
        */
        if (locale == null || locale.getLanguage().isBlank()) {
            return null;
        }

        // Cache'den al veya oluştur
        return bundleCache.get(locale, loc -> {
            Map<String, Object> data = provider.loadTranslations(loc);

            // Eğer veri yoksa boş bir map ile oluştur ki tekrar tekrar DB'ye gitmesin
            if (data == null || data.isEmpty()) {
                data = new HashMap<>();
            }

            return new ValoraMapResourceBundle(data);
        });
    }

    public void reload() {
        ValoraCacheManager.clearLibCache();
        bundleCache.invalidateAll();
        LocaleContext.clear();
        //newBundle("", LocaleContext.getLocale(), "", null, true);
    }

    public void addNewKey(String key, String value, String locale) {
        provider.saveTranslation(key, value, locale);
        reload();
    }

    private static class ValoraMapResourceBundle extends ResourceBundle {
        private final Map<String, Object> data;

        public ValoraMapResourceBundle(Map<String, Object> data) {
            this.data = data;
            setParent(null);
        }

        @Override
        protected Object handleGetObject(String key) {
            if (data == null) return null;
            return data.get(key);
        }

        @Override
        public Enumeration<String> getKeys() {
            return Collections.enumeration(data.keySet());
        }

    }
}