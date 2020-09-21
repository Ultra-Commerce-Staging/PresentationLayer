/*
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
package com.ultracommerce.presentation.thymeleaf3.resolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.ultracommerce.common.web.resource.UltraContextUtil;
import com.ultracommerce.presentation.resolver.UltraTemplateResolver;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import java.io.InputStream;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Jon Fleschler (jfleschler)
 */
public class DelegatingThymeleaf3TemplateResolver extends SpringResourceTemplateResolver {

    private static final Log LOG = LogFactory.getLog(DelegatingThymeleaf3TemplateResolver.class);

    @Resource(name = "ucUltraContextUtil")
    protected UltraContextUtil ucContextUtil;

    protected UltraTemplateResolver templateResolver;

    public DelegatingThymeleaf3TemplateResolver() {
        super();
        setCheckExistence(true);
    }

    @Override
    protected ITemplateResource computeTemplateResource(final IEngineConfiguration configuration, final String ownerTemplate,
                                                        final String template, final String resourceName, final String characterEncoding,
                                                        final Map<String, Object> templateResolutionAttributes) {
        ucContextUtil.establishThinRequestContextWithoutSandBox();
        InputStream resolvedResource = templateResolver.resolveResource(template, resourceName);
        return new UltraThymeleaf3ITemplateResource(resourceName, resolvedResource);
    }

    public UltraTemplateResolver getTemplateResolver() {
        return templateResolver;
    }

    public void setTemplateResolver(UltraTemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }
}
