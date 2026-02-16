package com.valora.dbbundle.config;

import com.valora.dbbundle.annotation.ValoraBundle;
import com.valora.dbbundle.service.DbBundleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ResourceBundle;

/**
 * BeanPostProcessor to inject ResourceBundle into fields annotated with @ValoraBundle.
 */
public class ValoraBundleAnnotationProcessor implements BeanPostProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(ValoraBundleAnnotationProcessor.class);
    
    private final DbBundleService dbBundleService;
    
    public ValoraBundleAnnotationProcessor(DbBundleService dbBundleService) {
        this.dbBundleService = dbBundleService;
    }
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        
        ReflectionUtils.doWithFields(clazz, field -> {
            if (field.isAnnotationPresent(ValoraBundle.class)) {
                logger.debug("Injecting ResourceBundle into field {} of bean {}", 
                        field.getName(), beanName);
                
                if (!ResourceBundle.class.isAssignableFrom(field.getType())) {
                    throw new IllegalArgumentException(
                            String.format("Field %s in %s must be of type ResourceBundle", 
                                    field.getName(), clazz.getName()));
                }
                
                ReflectionUtils.makeAccessible(field);
                ResourceBundle bundle = dbBundleService.getBundle();
                ReflectionUtils.setField(field, bean, bundle);
            }
        });
        
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
