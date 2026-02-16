package com.valora.dbbundle.repository;

import com.valora.dbbundle.model.MessageEntity;
import java.util.List;

/**
 * Repository interface for accessing message entities.
 * Applications should provide an implementation of this interface.
 */
public interface MessageRepository {
    
    /**
     * Find all messages for a specific locale code.
     * 
     * @param localeCode The locale code (e.g., "en", "tr", "fr")
     * @return List of message entities
     */
    List<? extends MessageEntity> findByLocaleCode(String localeCode);
    
    /**
     * Find a specific message by key and locale code.
     * 
     * @param msgKey The message key
     * @param localeCode The locale code
     * @return The message entity or null if not found
     */
    MessageEntity findByMsgKeyAndLocaleCode(String msgKey, String localeCode);
}
