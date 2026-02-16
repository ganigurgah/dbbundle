package com.valora.dbbundle.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for DbBundle.
 */
@ConfigurationProperties(prefix = "dbbundle")
public class DbBundleProperties {
    
    private String defaultLocale = "en";
    private Cache cache = new Cache();
    
    public String getDefaultLocale() {
        return defaultLocale;
    }
    
    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }
    
    public Cache getCache() {
        return cache;
    }
    
    public void setCache(Cache cache) {
        this.cache = cache;
    }
    
    public static class Cache {
        private long maxSize = 100;
        private long expireMinutes = 60;
        
        public long getMaxSize() {
            return maxSize;
        }
        
        public void setMaxSize(long maxSize) {
            this.maxSize = maxSize;
        }
        
        public long getExpireMinutes() {
            return expireMinutes;
        }
        
        public void setExpireMinutes(long expireMinutes) {
            this.expireMinutes = expireMinutes;
        }
    }
}
