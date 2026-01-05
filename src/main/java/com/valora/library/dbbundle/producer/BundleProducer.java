package com.valora.library.dbbundle.producer;

import com.valora.library.dbbundle.core.ValoraBundleControl;
import com.valora.library.dbbundle.context.LocaleContext;
import com.valora.library.dbbundle.spi.TranslationProvider;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * JSF ve CDI kullanan projeler için ResourceBundle üreticisi.
 * Spring projelerinde bu sınıf CDI taranmadığı sürece pasif kalır.
 */
@RequestScoped
public class BundleProducer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private TranslationProvider provider;

    /**
     * JSF projelerinde #{msg['key']} şeklinde kullanımı sağlar.
     * CDI üzerinden @Inject @ValoraBundle ResourceBundle rb; şeklinde de çağrılabilir.
     */
    @Produces
    @Named("msg")
    @ValoraBundle
    @RequestScoped
    public ResourceBundle produceBundle() {
        Locale currentLocale;

        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext != null && facesContext.getViewRoot() != null) {
                currentLocale = facesContext.getViewRoot().getLocale();
            } else {
                currentLocale = LocaleContext.getLocale();
            }
        } catch (NoClassDefFoundError | Exception e) {
            currentLocale = LocaleContext.getLocale();
        }

        return ResourceBundle.getBundle(
                "dbMessages",
                currentLocale,
                new ValoraBundleControl(provider, 60)
        );
    }
}