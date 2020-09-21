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

import com.ultracommerce.presentation.cache.service.SimpleCacheKeyResolver;
import com.ultracommerce.presentation.cache.service.TemplateCacheKeyResolverService;
import com.ultracommerce.presentation.thymeleaf3.expression.UltraVariableExpressionObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.spring5.messageresolver.SpringMessageResolver;

@Configuration
public class Thymeleaf3CommonConfig {

    @Bean
    public SpringMessageResolver springMessageResolver() {
        SpringMessageResolver resolver = new SpringMessageResolver();
        resolver.setOrder(200);
        return resolver;
    }
    
    @Bean
    @ConditionalOnMissingBean(TemplateCacheKeyResolverService.class)
    public SimpleCacheKeyResolver ucTemplateCacheKeyResolver() {
        return new SimpleCacheKeyResolver();
    }
    
    @Bean
    @ConditionalOnMissingBean(IExpressionObjectFactory.class)
    public UltraVariableExpressionObjectFactory ucVariableExpressionObjectFactory() {
        return new UltraVariableExpressionObjectFactory();
    }
    
}
