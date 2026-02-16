package com.valora.dbbundle.config;

import com.valora.dbbundle.el.MessageELResolver;
import com.valora.dbbundle.repository.MessageRepository;
import com.valora.dbbundle.service.DbBundleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for DbBundle library.
 */
@Configuration
@EnableConfigurationProperties(DbBundleProperties.class)
public class DbBundleAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DbBundleAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public DbBundleService dbBundleService(MessageRepository messageRepository,
                                           DbBundleProperties properties) {
        logger.info("Creating DbBundleService bean");
        DbBundleService service = new DbBundleService(messageRepository);

        // Configure cache from properties
        if (properties.getCache() != null) {
            service.configureCache(
                    properties.getCache().getMaxSize(),
                    properties.getCache().getExpireMinutes()
            );
        }

        // Set default locale if configured
        if (properties.getDefaultLocale() != null && !properties.getDefaultLocale().isEmpty()) {
            service.changeLocale(properties.getDefaultLocale());
        }

        service.init();

        return service;
    }

    @Bean
    @ConditionalOnMissingBean
    public ValoraBundleAnnotationProcessor valoraBundleAnnotationProcessor(DbBundleService dbBundleService) {
        logger.info("Creating ValoraBundleAnnotationProcessor bean");
        return new ValoraBundleAnnotationProcessor(dbBundleService);
    }

    @Bean
    @ConditionalOnClass(name = "jakarta.el.ELResolver")
    @ConditionalOnMissingBean
    public MessageELResolver messageELResolver(DbBundleService dbBundleService) {
        logger.info("Creating MessageELResolver bean for Jakarta EL support");
        MessageELResolver resolver = new MessageELResolver();
        resolver.setDbBundleService(dbBundleService);
        return resolver;
    }
}
