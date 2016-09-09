/*
 * #%L
 * broadleaf-thymeleaf3-presentation
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.admin.config;

import org.broadleafcommerce.common.config.Thymeleaf3ConfigUtils;
import org.broadleafcommerce.common.web.dialect.BroadleafDialectPrefix;
import org.broadleafcommerce.common.web.dialect.BroadleafProcessor;
import org.broadleafcommerce.common.web.dialect.BroadleafThymeleaf3AdminDialect;
import org.broadleafcommerce.common.web.dialect.BroadleafThymeleaf3Dialect;
import org.broadleafcommerce.common.web.resolver.BroadleafTemplateResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.annotation.Resource;

@Configuration
public class Thymeleaf3AdminConfig {

    @Resource
    protected ApplicationContext applicationContext;
    
    @Bean
    public BroadleafThymeleaf3AdminDialect blAdminDialect() {
        BroadleafThymeleaf3AdminDialect dialect = new BroadleafThymeleaf3AdminDialect();
        dialect.setProcessors(blAdminDialectProcessors());
        return dialect;
    }
    @Bean
    public BroadleafThymeleaf3Dialect blDialect() {
        BroadleafThymeleaf3Dialect dialect = new BroadleafThymeleaf3Dialect();
        dialect.setProcessors(blDialectProcessors());
        return dialect;
    }
    
    @Bean
    public Set<ITemplateResolver> blAdminWebTemplateResolvers() {
        Collection<BroadleafTemplateResolver> resolvers = applicationContext.getBeansOfType(BroadleafTemplateResolver.class).values();
        return Thymeleaf3ConfigUtils.getWebResovlers(resolvers, applicationContext);
    }
    
    @Bean 
    public Set<ITemplateResolver> blEmailTemplateResolvers() {
        Collection<BroadleafTemplateResolver> resolvers = applicationContext.getBeansOfType(BroadleafTemplateResolver.class).values();
        return Thymeleaf3ConfigUtils.getEmailResolvers(resolvers, applicationContext);
    }
    
    @Bean
    public Set<IProcessor> blAdminDialectProcessors() {
        Collection<BroadleafProcessor> blcProcessors = applicationContext.getBeansOfType(BroadleafProcessor.class).values();
        Collection<BroadleafProcessor> adminProcessors = new ArrayList<>();
        for (BroadleafProcessor processor : blcProcessors) {
            if (BroadleafDialectPrefix.BLC_ADMIN.equals(processor.getPrefix())) {
                adminProcessors.add(processor);
            }
        }
        return Thymeleaf3ConfigUtils.getDialectProcessors(adminProcessors);
    }
    
    @Bean
    public Set<IProcessor> blDialectProcessors() {
        Collection<BroadleafProcessor> blcProcessors = applicationContext.getBeansOfType(BroadleafProcessor.class).values();
        Collection<BroadleafProcessor> commonProcessors = new ArrayList<>();
        for (BroadleafProcessor processor : blcProcessors) {
            if (BroadleafDialectPrefix.BLC.equals(processor.getPrefix())) {
                commonProcessors.add(processor);
            }
        }
        return Thymeleaf3ConfigUtils.getDialectProcessors(commonProcessors);
    }
}
