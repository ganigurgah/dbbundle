package com.valora.library.dbbundle.spring;

import com.valora.library.dbbundle.core.ValoraBundleControl;
import com.valora.library.dbbundle.context.LocaleContext;
import com.valora.library.dbbundle.spi.TranslationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ResourceBundle;

@Configuration
public class ValoraSpringConfig implements WebMvcConfigurer {

    @Bean
    public ValoraLocaleInterceptor valoraLocaleInterceptor() {
        return new ValoraLocaleInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(valoraLocaleInterceptor());
    }

    @Bean
    @Scope("prototype")
    public ResourceBundle msg(TranslationProvider provider) {
        return ResourceBundle.getBundle(
                "dbMessages",
                LocaleContext.getLocale(),
                new ValoraBundleControl(provider, 60)
        );
    }
}