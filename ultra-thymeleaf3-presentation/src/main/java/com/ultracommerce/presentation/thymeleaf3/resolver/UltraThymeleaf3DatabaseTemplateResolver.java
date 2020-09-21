/*
 * #%L
 * ultra-theme
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
package com.ultracommerce.presentation.thymeleaf3.resolver;

import com.ultracommerce.common.web.resource.UltraContextUtil;
import com.ultracommerce.core.web.resolver.DatabaseResourceResolverExtensionManager;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import java.util.Map;

/**
 * The injection happens in XML configuration.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class UltraThymeleaf3DatabaseTemplateResolver extends AbstractConfigurableTemplateResolver {
    
    protected DatabaseResourceResolverExtensionManager resourceResolverExtensionManager;

    protected UltraContextUtil ucContextUtil;
    
    public UltraThymeleaf3DatabaseTemplateResolver() {
        setCheckExistence(true);
    }

    @Override
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
        return new UltraThymeleaf3DatabaseResourceResolver(resourceResolverExtensionManager, ucContextUtil, template);
    }

    public DatabaseResourceResolverExtensionManager getResourceResolverExtensionManager() {
        return resourceResolverExtensionManager;
    }
    
    public void setResourceResolverExtensionManager(DatabaseResourceResolverExtensionManager resourceResolverExtensionManager) {
        this.resourceResolverExtensionManager = resourceResolverExtensionManager;
    }

    public void setUltraContextUtil(UltraContextUtil ucContextUtil) {
        this.ucContextUtil = ucContextUtil;
    }

}
