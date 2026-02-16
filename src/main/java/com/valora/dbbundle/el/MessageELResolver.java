package com.valora.dbbundle.el;

import com.valora.dbbundle.service.DbBundleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import java.beans.FeatureDescriptor;
import java.util.*;

/**
 * EL Resolver for Jakarta EL to resolve #{msg['key']} expressions.
 * This resolver works with both JSF and Spring applications using Jakarta EL.
 */
public class MessageELResolver extends ELResolver {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageELResolver.class);
    private static final String MSG_VAR = "msg";
    
    private DbBundleService dbBundleService;
    
    public void setDbBundleService(DbBundleService dbBundleService) {
        this.dbBundleService = dbBundleService;
    }
    
    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (base == null && MSG_VAR.equals(property)) {
            context.setPropertyResolved(true);
            return new MessageMap();
        }
        
        if (base instanceof MessageMap && property != null) {
            context.setPropertyResolved(true);
            String key = property.toString();
            
            if (dbBundleService != null) {
                return dbBundleService.getMessage(key);
            } else {
                logger.warn("DbBundleService not initialized in MessageELResolver");
                return "???<tst>" + key + "<tst>???";
            }
        }
        
        return null;
    }
    
    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        if (base == null && MSG_VAR.equals(property)) {
            context.setPropertyResolved(true);
            return MessageMap.class;
        }
        
        if (base instanceof MessageMap) {
            context.setPropertyResolved(true);
            return String.class;
        }
        
        return null;
    }
    
    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        // Read-only
    }
    
    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (base == null && MSG_VAR.equals(property)) {
            context.setPropertyResolved(true);
            return true;
        }
        
        if (base instanceof MessageMap) {
            context.setPropertyResolved(true);
            return true;
        }
        
        return false;
    }
    /*
    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }
    */
    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base instanceof MessageMap) {
            return String.class;
        }
        return null;
    }
    
    /**
     * Internal class to represent the message map in EL expressions.
     */
    private static class MessageMap extends HashMap<String, String> {
        private static final long serialVersionUID = 1L;
    }
}
