/*-
 * #%L
 * ultra-thymeleaf3-presentation
 * %%
 * Copyright (C) 2009 - 2017 Ultra Commerce
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
package com.ultracommerce.presentation.thymeleaf3.config;

import com.ultracommerce.presentation.resolver.UltraTemplateResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractThymeleaf3EngineConfig {

    @Autowired(required = false)
    protected Set<ITemplateResolver> iTemplateResolvers = new LinkedHashSet<>();
    
    @Autowired(required = false)
    protected Set<UltraTemplateResolver> ucTemplateResolvers = new LinkedHashSet<>();
    
    @Autowired
    protected Set<IDialect> dialects;
    
    @Autowired
    protected Thymeleaf3ConfigUtils configUtil;
    
    @Bean
    public SpringTemplateEngine ucEmailTemplateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        Set<ITemplateResolver> allResolvers = new LinkedHashSet<>();
        allResolvers.addAll(iTemplateResolvers);
        allResolvers.addAll(ucEmailTemplateResolvers());
        engine.setTemplateResolvers(allResolvers);
        engine.setDialects(dialects);
        return engine;
    }
    
    @Bean
    public Set<ITemplateResolver> ucEmailTemplateResolvers() {
        return configUtil.getEmailResolvers(ucTemplateResolvers);
    }
    
    @Bean
    @Primary
    public Set<ITemplateResolver> ucWebTemplateResolvers() {
        return configUtil.getWebResolvers(ucTemplateResolvers);
    }
    
    @Configuration
    protected static class Thymeleaf3CommonTemplateResolverConfig extends Thymeleaf3CommonTemplateConfig {}
}
