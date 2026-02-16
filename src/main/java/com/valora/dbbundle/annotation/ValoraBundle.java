package com.valora.dbbundle.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to inject database-backed ResourceBundle into fields.
 * 
 * Usage:
 * <pre>
 * @ValoraBundle
 * private ResourceBundle bundle;
 * </pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValoraBundle {
}
