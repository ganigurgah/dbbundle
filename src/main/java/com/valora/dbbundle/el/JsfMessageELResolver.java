package com.valora.dbbundle.el;

import com.valora.dbbundle.service.DbBundleService;
import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.FeatureDescriptor;
import java.util.*;

/**
 * JSF-compatible EL Resolver using javax.el for legacy JSF applications.
 * For modern Jakarta EE applications, use MessageELResolver instead.
 * 
 * This resolver can be registered directly in faces-config.xml
 */
public class JsfMessageELResolver extends ELResolver {
    
    private static final Logger logger = LoggerFactory.getLogger(JsfMessageELResolver.class);
    private static final String MSG_VAR = "msg";
    
    // Static holder for DbBundleService - set by Spring configuration
    private static volatile DbBundleService staticDbBundleService;
    
    public JsfMessageELResolver() {
        // Default constructor for faces-config.xml instantiation
    }
    
    /**
     * Called by Spring to set the DbBundleService instance.
     * This allows the faces-config.xml registered resolver to access Spring beans.
     */
    public static void setStaticDbBundleService(DbBundleService dbBundleService) {
        staticDbBundleService = dbBundleService;
        logger.info("DbBundleService set for JsfMessageELResolver");
    }
    
    private DbBundleService getDbBundleService() {
        return staticDbBundleService;
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
            
            DbBundleService service = getDbBundleService();
            if (service != null) {
                return service.getMessage(key);
            } else {
                logger.warn("DbBundleService not initialized in JsfMessageELResolver");
                return "???" + key + "???";
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
