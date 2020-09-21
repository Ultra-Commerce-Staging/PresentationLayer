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
package com.ultracommerce.presentation.thymeleaf3.cache;

import com.ultracommerce.presentation.cache.UltraTemplateCacheInvalidationContext;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;

public class UltraThymeleaf3CacheInvalidationContext implements UltraTemplateCacheInvalidationContext {

    protected ITemplateEngine templateEngine;
    
    @Override
    public void clearTemplateCacheFor(String path) {
        if (TemplateEngine.class.isAssignableFrom(templateEngine.getClass())) {
            ((TemplateEngine) templateEngine).clearTemplateCacheFor(path);
        } else {
            throw new UnsupportedOperationException("Unable to invalidate cache for template engine because it's of type " + templateEngine.getClass().getName() 
                + " which doesn't extend " + TemplateEngine.class.getName() + ". To fix this extend " + this.getClass().getName() 
                + " and implement the clearTemplateCacheFor method for your template engine");
        }
    }

    public ITemplateEngine getTemplateEngine() {
        return templateEngine;
    }
    
    public void setTemplateEngine(ITemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

}
