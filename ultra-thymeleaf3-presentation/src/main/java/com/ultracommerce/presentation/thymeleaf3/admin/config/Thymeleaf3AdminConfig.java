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
package com.ultracommerce.presentation.thymeleaf3.admin.config;

import com.ultracommerce.presentation.dialect.UltraDialectPrefix;
import com.ultracommerce.presentation.dialect.UltraProcessor;
import com.ultracommerce.presentation.thymeleaf3.UltraThymeleaf3MessageResolver;
import com.ultracommerce.presentation.thymeleaf3.UltraThymeleaf3TemplateEngine;
import com.ultracommerce.presentation.thymeleaf3.UltraThymeleafViewResolver;
import com.ultracommerce.presentation.thymeleaf3.config.AbstractThymeleaf3DialectConfig;
import com.ultracommerce.presentation.thymeleaf3.config.AbstractThymeleaf3EngineConfig;
import com.ultracommerce.presentation.thymeleaf3.config.Thymeleaf3CommonConfig;
import com.ultracommerce.presentation.thymeleaf3.dialect.UltraThymeleaf3AdminDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Configuration
public class Thymeleaf3AdminConfig extends Thymeleaf3CommonConfig {

    
    @Configuration
    static class Thymeleaf3AdminDialectConfig extends AbstractThymeleaf3DialectConfig {

        @Bean
        public UltraThymeleaf3AdminDialect ucAdminDialect() {
            UltraThymeleaf3AdminDialect dialect = new UltraThymeleaf3AdminDialect();
            Set<IProcessor> allProcessors = new LinkedHashSet<>();
            allProcessors.addAll(iProcessors);
            allProcessors.addAll(ucAdminDialectProcessors());
            dialect.setProcessors(allProcessors);
            return dialect;
        }
        
        @Bean
        @Override
        public Set<IProcessor> ucDialectProcessors() {
            Collection<UltraProcessor> commonProcessors = new ArrayList<>();
            for (UltraProcessor processor : ucProcessors) {
                if (UltraDialectPrefix.UC.equals(processor.getPrefix())) {
                    commonProcessors.add(processor);
                }
            }
            return configUtil.getDialectProcessors(commonProcessors);
        }
        
        @Bean
        public Set<IProcessor> ucAdminDialectProcessors() {
            Collection<UltraProcessor> adminProcessors = new ArrayList<>();
            for (UltraProcessor processor : ucProcessors) {
                if (UltraDialectPrefix.UC_ADMIN.equals(processor.getPrefix())) {
                    adminProcessors.add(processor);
                }
            }
            return configUtil.getDialectProcessors(adminProcessors);
        }
        
        @Bean
        public Set<IDialect> ucAdminDialects() {
            Set<IDialect> dialects = new LinkedHashSet<>();
            dialects.add(thymeleafSpringStandardDialect());
            dialects.add(ucAdminDialect());
            dialects.add(ucDialect());
            return dialects;
        }
        
    }
    
    @Configuration
    static class Thymeleaf3AdminEngineConfig extends AbstractThymeleaf3EngineConfig {

        protected Set<IMessageResolver> messageResolvers;
        
        public Thymeleaf3AdminEngineConfig(Set<IMessageResolver> messageResolvers) {
            this.messageResolvers = messageResolvers;
        }
        
        @Bean
        public Set<ITemplateResolver> ucAdminWebTemplateResolvers() {
            return configUtil.getWebResolvers(ucTemplateResolvers);
        }
        
        @Bean
        @Primary
        public UltraThymeleaf3TemplateEngine ucAdminWebTemplateEngine() {
            UltraThymeleaf3TemplateEngine engine = new UltraThymeleaf3TemplateEngine();
            engine.setMessageResolvers(messageResolvers);
            Set<ITemplateResolver> allResolvers = new LinkedHashSet<>();
            allResolvers.addAll(iTemplateResolvers);
            allResolvers.addAll(ucAdminWebTemplateResolvers());
            engine.setTemplateResolvers(allResolvers);
            engine.setDialects(dialects);
            return engine;
        }
        
        @Configuration
        protected static class Thymeleaf3AdminTemplateResolverConfig extends Thymeleaf3AdminTemplateConfig {}
    }
    
    @Configuration
    static class Thymeleaf3AdminViewConfig {
        
        protected ISpringTemplateEngine templateEngine;
        
        protected Environment environment;
        
        public Thymeleaf3AdminViewConfig(ISpringTemplateEngine templateEngine, Environment environment) {
            this.templateEngine = templateEngine;
            this.environment = environment;
        }
        
        @Bean(name = {"ucAdminThymeleafViewResolver", "thymeleafViewResolver"})
        public UltraThymeleafViewResolver ucAdminThymeleafViewResolver() {
            UltraThymeleafViewResolver view = new UltraThymeleafViewResolver();
            view.setTemplateEngine(templateEngine);
            view.setOrder(1);
            view.setCache(environment.getProperty("thymeleaf.view.resolver.cache", Boolean.class, true));
            view.setCharacterEncoding("UTF-8");
            view.setFullPageLayout("layout/fullPageLayout");
            Map<String, String> layoutMap = new HashMap<>();
            layoutMap.put("login/", "layout/loginLayout");
            layoutMap.put("views/", "NONE");
            layoutMap.put("modules/modalContainer", "NONE");
            view.setLayoutMap(layoutMap);
            return view;
        }
    }
    
    @Bean 
    public IMessageResolver ucAdminMessageResolver() {
        UltraThymeleaf3MessageResolver resolver = new UltraThymeleaf3MessageResolver();
        resolver.setOrder(100);
        return resolver;
    }
    
}
