package com.valora.dbbundle.service;

import com.valora.dbbundle.model.MessageEntity;
import java.util.*;

/**
 * ResourceBundle implementation backed by database.
 */
public class DatabaseResourceBundle extends ResourceBundle {
    
    private final Map<String, String> messages;
    private final Locale locale;
    
    public DatabaseResourceBundle(List<? extends MessageEntity> messageEntities, Locale locale) {
        this.locale = locale;
        this.messages = new HashMap<>();
        
        if (messageEntities != null) {
            for (MessageEntity entity : messageEntities) {
                messages.put(entity.getMsgKey(), entity.getMsgValue());
            }
        }
    }
    
    @Override
    protected Object handleGetObject(String key) {
        if (key == null) {
            throw new NullPointerException("key cannot be null");
        }
        return messages.get(key);
    }
    
    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(messages.keySet());
    }
    
    @Override
    public Locale getLocale() {
        return locale;
    }
    
    @Override
    public boolean containsKey(String key) {
        return messages.containsKey(key);
    }
    
    @Override
    public Set<String> keySet() {
        return messages.keySet();
    }
}
