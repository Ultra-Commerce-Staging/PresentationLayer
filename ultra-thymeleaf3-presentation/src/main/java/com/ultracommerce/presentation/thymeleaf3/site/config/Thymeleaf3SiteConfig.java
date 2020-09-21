/*
 * #%L
 * ultra-thymeleaf3-presentation
 * %%
 * Copyright (C) 2009 - 2016 Ultra Commerce
 * %%
 * Licensed under the Ultra Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.ultracommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Ultra in which case
 * the Ultra End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.ultracommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Ultra Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package com.ultracommerce.presentation.thymeleaf3.site.config;

import com.ultracommerce.common.logging.LifeCycleEvent;
import com.ultracommerce.common.logging.ModuleLifecycleLoggingBean;
import com.ultracommerce.presentation.thymeleaf3.UltraThymeleaf3MessageResolver;
import com.ultracommerce.presentation.thymeleaf3.UltraThymeleaf3TemplateEngine;
import com.ultracommerce.presentation.thymeleaf3.UltraThymeleafViewResolver;
import com.ultracommerce.presentation.thymeleaf3.cache.UltraThymeleaf3CacheInvalidationContext;
import com.ultracommerce.presentation.thymeleaf3.cache.UltraThymeleaf3CacheManager;
import com.ultracommerce.presentation.thymeleaf3.config.AbstractThymeleaf3DialectConfig;
import com.ultracommerce.presentation.thymeleaf3.config.AbstractThymeleaf3EngineConfig;
import com.ultracommerce.presentation.thymeleaf3.config.Thymeleaf3CommonConfig;
import com.ultracommerce.presentation.thymeleaf3.config.Thymeleaf3ModuleRegistration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.LinkedHashSet;
import java.util.Set;

@Configuration
public class Thymeleaf3SiteConfig extends Thymeleaf3CommonConfig {
    
    @Bean
    public ModuleLifecycleLoggingBean ucThymeleaf3Lifecycle() {
        return new ModuleLifecycleLoggingBean(Thymeleaf3ModuleRegistration.MODULE_NAME, LifeCycleEvent.LOADING);
    }
    
    @Configuration
    static class Thymeleaf3SiteDialectConfig extends AbstractThymeleaf3DialectConfig {
        
        @Bean
        public Set<IDialect> ucWebDialects() {
            Set<IDialect> dialects = new LinkedHashSet<>();
            dialects.add(thymeleafSpringStandardDialect());
            dialects.add(ucDialect());
            return dialects;
        }
    }
    
    @Configuration
    static class Thymeleaf3SiteEngineConfig extends AbstractThymeleaf3EngineConfig {
        
        protected Set<IMessageResolver> messageResolvers;
        
        protected ICacheManager cacheManager;

        public Thymeleaf3SiteEngineConfig(Set<IMessageResolver> messageResolvers,
                                          ICacheManager cacheManager) {
            this.messageResolvers = messageResolvers;
            this.cacheManager = cacheManager;
        }
        
        @Bean
        @Primary
        public UltraThymeleaf3TemplateEngine ucWebTemplateEngine() {
            UltraThymeleaf3TemplateEngine engine = new UltraThymeleaf3TemplateEngine();
            engine.setMessageResolvers(messageResolvers);
            Set<ITemplateResolver> allResolvers = new LinkedHashSet<>();
            allResolvers.addAll(iTemplateResolvers);
            allResolvers.addAll(ucWebTemplateResolvers());
            engine.setTemplateResolvers(allResolvers);
            engine.setCacheManager(cacheManager);
            engine.setDialects(dialects);
            return engine;
        }
        
        @Configuration
        protected static class Thymeleaf3TemplateResolverConfig extends Thymeleaf3SiteTemplateConfig {}
    }
    
    @Configuration
    static class Thymeleaf3SiteViewConfig {
        
        protected ISpringTemplateEngine templateEngine;
        
        protected Environment environment;
        
        public Thymeleaf3SiteViewConfig(ISpringTemplateEngine templateEngine, Environment environment) {
            this.templateEngine = templateEngine;
            this.environment = environment;
        }
        
        @Bean(name = {"ucThymeleafViewResolver", "thymeleafViewResolver"})
        public UltraThymeleafViewResolver ucThymeleafViewResolver() {
            UltraThymeleafViewResolver view = new UltraThymeleafViewResolver();
            view.setTemplateEngine(templateEngine);
            view.setOrder(1);
            view.setCache(environment.getProperty("thymeleaf.view.resolver.cache", Boolean.class, true));
            view.setCharacterEncoding("UTF-8");
            return view;
        }
    }
    
    @Configuration
    static class Thymeleaf3CacheInvalidationConfig {
        
        protected ITemplateEngine templateEngine;
        
        public Thymeleaf3CacheInvalidationConfig(ITemplateEngine templateEngine) {
            this.templateEngine = templateEngine;
        }
        
        @Bean
        public UltraThymeleaf3CacheInvalidationContext ucTemplateCacheInvalidationContext() {
            UltraThymeleaf3CacheInvalidationContext context = new UltraThymeleaf3CacheInvalidationContext();
            context.setTemplateEngine(templateEngine);
            return context;
        }
        
    }
    
    @Bean
    public IMessageResolver ucMessageResolver() {
        UltraThymeleaf3MessageResolver resolver = new UltraThymeleaf3MessageResolver();
        resolver.setOrder(100);
        return resolver;
    }
    
    @Bean
    @ConditionalOnMissingBean(ICacheManager.class)
    public UltraThymeleaf3CacheManager ucICacheManager() {
        return new UltraThymeleaf3CacheManager();
    }
    
}
