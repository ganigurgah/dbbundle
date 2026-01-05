package com.valora.library.dbbundle.core;

import java.util.ResourceBundle;

public class ValoraCacheManager {
    /**
     * Sadece bu kütüphaneyi yükleyen ClassLoader'a ait
     * ResourceBundle önbelleğini temizler.
     */
    public static void clearLibCache() {
        ClassLoader loader = ValoraCacheManager.class.getClassLoader();
        ResourceBundle.clearCache(loader);
    }
}