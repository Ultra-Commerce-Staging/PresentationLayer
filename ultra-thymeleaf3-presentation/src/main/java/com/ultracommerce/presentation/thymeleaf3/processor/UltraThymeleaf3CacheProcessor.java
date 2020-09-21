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
package com.ultracommerce.presentation.thymeleaf3.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.ultracommerce.common.config.service.SystemPropertiesService;
import com.ultracommerce.common.extensibility.cache.JCacheUtil;
import com.ultracommerce.common.web.UltraRequestContext;
import com.ultracommerce.presentation.cache.service.TemplateCacheKeyResolverService;
import com.ultracommerce.presentation.dialect.UltraDialectPrefix;
import com.ultracommerce.presentation.model.UltraTemplateContext;
import com.ultracommerce.presentation.thymeleaf3.model.UltraThymeleaf3Context;
import org.springframework.web.context.request.WebRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.annotation.Resource;
import javax.cache.Cache;

public class UltraThymeleaf3CacheProcessor extends AbstractAttributeModelProcessor {

    public static final String ATTR_NAME = "cache";
    
    private static final Log LOG = LogFactory.getLog(UltraThymeleaf3CacheProcessor.class);

    protected Cache cache;

    @Resource(name = "ucSystemPropertiesService")
    protected SystemPropertiesService systemPropertiesService;

    @Resource(name = "ucTemplateCacheKeyResolver")
    protected TemplateCacheKeyResolverService cacheKeyResolver;
    
    @Resource(name = "ucJCacheUtil")
    protected JCacheUtil jcacheUtil;

    public UltraThymeleaf3CacheProcessor() {
        super(TemplateMode.HTML, UltraDialectPrefix.UC.toString(), null, false, ATTR_NAME, true, Integer.MIN_VALUE, false);
    }
    
    @Override
    protected void doProcess(ITemplateContext iContext, IModel model, AttributeName attributeName, String attributeValue, IElementModelStructureHandler structureHandler) {
        IProcessableElementTag rootTag = (IProcessableElementTag) model.get(0);
        String tagName = rootTag.getElementCompleteName();
        Map<String, String> tagAttributes = rootTag.getAttributeMap();
        String documentName = iContext.getTemplateData().getTemplate();
        Integer lineNumber = rootTag.getLine();
        UltraTemplateContext context = new UltraThymeleaf3Context(iContext, structureHandler);
        if (shouldCache(attributeValue, context)) {
            String cacheKey = cacheKeyResolver.resolveCacheKey(tagName, tagAttributes, documentName, lineNumber, context);
            Object cacheElement = checkCacheForElement(tagAttributes, cacheKey);
            if (cacheElement != null) {
                replaceTagWithCache(cacheElement.toString(), model, iContext);
            } else {
                tagAttributes.remove("blc:cache");
                addToCache(cacheKey, model, iContext, tagAttributes);
            }
        }
    }

    protected boolean shouldCache(String attributeValue, UltraTemplateContext context) {
        if (isCachingEnabled()) {
            String cacheAttrValue = attributeValue;
            if (StringUtils.isEmpty(attributeValue)) {
                return false;
            }
    
            cacheAttrValue = cacheAttrValue.toLowerCase();
            if (!isCachingEnabled() || "false".equals(cacheAttrValue)) {
                return false;
            } else if ("true".equals(cacheAttrValue)) {
                return true;
            }
    
            // Check for an expression
            Object o = context.parseExpression(cacheAttrValue);
            if (o instanceof Boolean) {
                return (Boolean) o;
            } else if (o instanceof String) {
                cacheAttrValue = (String) o;
                cacheAttrValue = cacheAttrValue.toLowerCase();
                return "true".equals(cacheAttrValue);
            }
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Template caching disabled - not retrieving template from cache");
            }
        }
        return false;
    }

    /**
     * If this template was found in cache, adds the response to the element and returns true.
     * 
     * If not found in cache, adds the cacheKey to the element so that the Writer can cache after the
     * first process.
     * 
     * @param arguments
     * @param element
     * @return
     */
    protected Object checkCacheForElement(Map<String, String> tagAttributes, String cacheKey) {
        
        if (!StringUtils.isEmpty(cacheKey)) {
            Object cacheElement = getCache().get(cacheKey);
            if (cacheElement != null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Template Cache Hit with cacheKey " + cacheKey + " found in cache.");
                }
                return cacheElement;
            } else {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Template Cache Miss with cacheKey " + cacheKey + " not found in cache.");
                }
            }
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Template not cached due to empty cacheKey");
            }
        }

        return null;
    }
    
    protected void replaceTagWithCache(String cachedObject, IModel model, ITemplateContext context) {
        model = context.getConfiguration().getTemplateManager().parseString(context.getTemplateData(), cachedObject, model.get(0).getLine(), model.get(0).getCol(), getTemplateMode(), false);
    }
    
    protected void addToCache(String cacheKey, IModel model, ITemplateContext context, Map<String, String> tagAttributes) {
        IProcessableElementTag firstEvent = (IProcessableElementTag) model.get(0);
        final IModelFactory modelFactory = context.getModelFactory();
        Map<String, String> originalAttributes = firstEvent.getAttributeMap();
        IProcessableElementTag newFirstEvent = firstEvent;
        for (String attrName : originalAttributes.keySet()) {
            if (!tagAttributes.containsKey(attrName)) {
                newFirstEvent = modelFactory.removeAttribute(newFirstEvent, attrName);
            }
        }
        if (newFirstEvent != firstEvent) {
            model.replace(0, newFirstEvent);
        }
        final StringWriter modelWriter = new StringWriter();
        try {
            model.write(modelWriter);
        } catch (IOException e) {
            throw new TemplateProcessingException("Error during creation of output", e);
        }
        final TemplateModel cacheModel = context.getConfiguration().getTemplateManager()
                .parseString(context.getTemplateData(), modelWriter.toString(), firstEvent.getLine(),
                        firstEvent.getCol(),
                        getTemplateMode(), false);

        final StringWriter templateWriter = new StringWriter();
        context.getConfiguration().getTemplateManager().process(cacheModel, context, templateWriter);
        if (StringUtils.isNotBlank(cacheKey) && templateWriter != null && StringUtils.isNotBlank(templateWriter.toString())) {
            getCache().put(cacheKey, templateWriter.toString());
        }
    }

    public Cache getCache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    cache = jcacheUtil.getCache("ucTemplateElements");
                }
            }
        }
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public boolean isCachingEnabled() {
        boolean enabled = !systemPropertiesService.resolveBooleanSystemProperty("disableThymeleafTemplateCaching");
        if (enabled) {
            // check for a URL param that overrides caching - useful for testing if this processor is incorrectly
            // caching a page (possibly due to an bad cacheKey).

            UltraRequestContext brc = UltraRequestContext.getUltraRequestContext();
            if (brc != null && brc.getWebRequest() != null) {
                WebRequest request = brc.getWebRequest();
                String disableCachingParam = request.getParameter("disableThymeleafTemplateCaching");
                if ("true".equals(disableCachingParam)) {
                    return false;
                }
            }
        }
        return enabled;
    }
    
}
