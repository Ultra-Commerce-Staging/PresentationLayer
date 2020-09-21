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
package com.ultracommerce.presentation.thymeleaf3.admin.config;

import com.ultracommerce.presentation.resolver.UltraClasspathTemplateResolver;
import com.ultracommerce.presentation.resolver.UltraTemplateMode;
import com.ultracommerce.presentation.resolver.UltraTemplateResolver;
import com.ultracommerce.presentation.resolver.UltraThemeAwareTemplateResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

public class Thymeleaf3AdminTemplateConfig {

    @Autowired
    protected Environment environment;
    
    protected final String isCacheableProperty = "cache.page.templates";
    protected final String cacheableTTLProperty = "cache.page.templates.ttl";
    
    @Bean
    public UltraTemplateResolver ucAdminWebTemplateResolver() {
        UltraThemeAwareTemplateResolver resolver = new UltraThemeAwareTemplateResolver();
        resolver.setPrefix("/WEB-INF/templates/admin/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(UltraTemplateMode.HTML);
        resolver.setCacheable(environment.getProperty(isCacheableProperty, Boolean.class, false));
        resolver.setCacheTTLMs(environment.getProperty(cacheableTTLProperty, Long.class, 0L));
        resolver.setCharacterEncoding("UTF-8");
        resolver.setOrder(200);
        return resolver;
    }
    
    @Bean
    public UltraTemplateResolver ucAdminWebClasspathTemplateResolver() {
        UltraClasspathTemplateResolver resolver = new UltraClasspathTemplateResolver();
        resolver.setPrefix("open_admin_style/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(UltraTemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(environment.getProperty(isCacheableProperty, Boolean.class, false));
        resolver.setCacheTTLMs(environment.getProperty(cacheableTTLProperty, Long.class, 0L));
        resolver.setOrder(300);
        return resolver;
    }
}
