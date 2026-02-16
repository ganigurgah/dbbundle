package com.valora.dbbundle.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.valora.dbbundle.model.MessageEntity;
import com.valora.dbbundle.repository.MessageRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * Service for managing database-backed resource bundles with Caffeine caching.
 */
public class DbBundleService {
    
    private static final Logger logger = LoggerFactory.getLogger(DbBundleService.class);
    
    private final MessageRepository messageRepository;
    private Cache<String, ResourceBundle> bundleCache;
    private Locale currentLocale;
    
    // Cache configuration
    private long cacheMaxSize = 100;
    private long cacheExpireMinutes = 60;
    
    public DbBundleService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.currentLocale = Locale.getDefault();
    }
    
    @PostConstruct
    public void init() {
        logger.info("Initializing DbBundleService with Caffeine cache");
        buildCache();
    }
    
    /**
     * Build Caffeine cache with configured parameters.
     */
    private void buildCache() {
        this.bundleCache = Caffeine.newBuilder()
                .maximumSize(cacheMaxSize)
                .expireAfterWrite(cacheExpireMinutes, TimeUnit.MINUTES)
                .recordStats()
                .build();
        
        logger.info("Cache initialized - maxSize: {}, expireMinutes: {}", 
                cacheMaxSize, cacheExpireMinutes);
    }
    
    /**
     * Get ResourceBundle for current locale.
     */
    public ResourceBundle getBundle() {
        return getBundle(currentLocale);
    }
    
    /**
     * Get ResourceBundle for specific locale.
     */
    public ResourceBundle getBundle(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        
        String localeCode = locale.getLanguage();

        Locale finalLocale = locale;
        return bundleCache.get(localeCode, key -> {
            logger.debug("Loading messages for locale: {}", key);
            List<? extends MessageEntity> messages = messageRepository.findByLocaleCode(key);
            return new DatabaseResourceBundle(messages, finalLocale);
        });
    }
    
    /**
     * Change the current locale and reload messages.
     */
    public void changeLocale(Locale newLocale) {
        if (newLocale == null) {
            throw new IllegalArgumentException("Locale cannot be null");
        }
        
        logger.info("Changing locale from {} to {}", currentLocale, newLocale);
        this.currentLocale = newLocale;
        
        // Invalidate cache for the new locale to force reload
        bundleCache.invalidate(newLocale.getLanguage());
    }
    
    /**
     * Change locale by language code (e.g., "tr", "en", "fr").
     */
    public void changeLocale(String localeCode) {
        if (localeCode == null || localeCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Locale code cannot be null or empty");
        }
        
        Locale newLocale = new Locale(localeCode.trim());
        changeLocale(newLocale);
    }
    
    /**
     * Get current locale.
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }
    
    /**
     * Clear all cached bundles.
     */
    public void clearCache() {
        logger.info("Clearing all cached bundles");
        bundleCache.invalidateAll();
    }
    
    /**
     * Clear cache for specific locale.
     */
    public void clearCache(String localeCode) {
        logger.info("Clearing cache for locale: {}", localeCode);
        bundleCache.invalidate(localeCode);
    }
    
    /**
     * Get a message value by key for current locale.
     */
    public String getMessage(String key) {
        return getMessage(key, currentLocale);
    }
    
    /**
     * Get a message value by key for specific locale.
     */
    public String getMessage(String key, Locale locale) {
        if (key == null) {
            return null;
        }
        
        ResourceBundle bundle = getBundle(locale);
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            logger.warn("Message key not found: {} for locale: {}", key, locale);
            return "???" + key + "???";
        }
    }
    
    /**
     * Configure cache settings.
     */
    public void configureCache(long maxSize, long expireMinutes) {
        this.cacheMaxSize = maxSize;
        this.cacheExpireMinutes = expireMinutes;
        buildCache();
        logger.info("Cache reconfigured - maxSize: {}, expireMinutes: {}", maxSize, expireMinutes);
    }
    
    /**
     * Get cache statistics.
     */
    public String getCacheStats() {
        return bundleCache.stats().toString();
    }
}
