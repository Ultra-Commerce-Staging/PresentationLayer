/*
 * #%L
 * ultra-thymeleaf2-presentation
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
package com.ultracommerce.presentation.thymeleaf3.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.ultracommerce.common.web.resource.UltraContextUtil;
import com.ultracommerce.core.web.resolver.DatabaseResourceResolverExtensionManager;
import com.ultracommerce.presentation.dialect.UltraAttributeModifierProcessor;
import com.ultracommerce.presentation.dialect.UltraModelModifierProcessor;
import com.ultracommerce.presentation.dialect.UltraProcessor;
import com.ultracommerce.presentation.dialect.UltraTagReplacementProcessor;
import com.ultracommerce.presentation.dialect.UltraTagTextModifierProcessor;
import com.ultracommerce.presentation.dialect.UltraVariableModifierAttrProcessor;
import com.ultracommerce.presentation.dialect.UltraVariableModifierProcessor;
import com.ultracommerce.presentation.resolver.UltraTemplateMode;
import com.ultracommerce.presentation.resolver.UltraTemplateResolver;
import com.ultracommerce.presentation.resolver.UltraTemplateResolverType;
import com.ultracommerce.presentation.thymeleaf3.UltraThymeleaf3ThemeAwareTemplateResolver;
import com.ultracommerce.presentation.thymeleaf3.dialect.DelegatingThymeleaf3AttributeModelVariableModifierProcessor;
import com.ultracommerce.presentation.thymeleaf3.dialect.DelegatingThymeleaf3AttributeModifierProcessor;
import com.ultracommerce.presentation.thymeleaf3.dialect.DelegatingThymeleaf3ModelModifierProcessor;
import com.ultracommerce.presentation.thymeleaf3.dialect.DelegatingThymeleaf3TagReplacementProcessor;
import com.ultracommerce.presentation.thymeleaf3.dialect.DelegatingThymeleaf3TagTextModifierProcessor;
import com.ultracommerce.presentation.thymeleaf3.dialect.DelegatingThymeleaf3VariableModifierProcessor;
import com.ultracommerce.presentation.thymeleaf3.resolver.UltraThymeleaf3DatabaseTemplateResolver;
import com.ultracommerce.presentation.thymeleaf3.resolver.DelegatingThymeleaf3TemplateResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

@Component("ucThymeleaf3ConfigUtils")
public class Thymeleaf3ConfigUtils {
    
    protected static final Log LOG = LogFactory.getLog(Thymeleaf3ConfigUtils.class);
    
    @Resource
    protected ApplicationContext applicationContext;
            
    public Set<IProcessor> getDialectProcessors(Collection<UltraProcessor> ucProcessors) {
        Set<IProcessor> iProcessors = new HashSet<>();
        for (UltraProcessor proc : ucProcessors) {
            if (UltraVariableModifierProcessor.class.isAssignableFrom(proc.getClass())) {
                iProcessors.add(createDelegatingModelVariableModifierProcessor((UltraVariableModifierProcessor) proc));
            } else if (UltraTagTextModifierProcessor.class.isAssignableFrom(proc.getClass())) {
                iProcessors.add(createDelegatingTagTextModifierProcessor((UltraTagTextModifierProcessor) proc));
            } else if (UltraTagReplacementProcessor.class.isAssignableFrom(proc.getClass())) {
                iProcessors.add(createDelegatingTagReplacementProcessor((UltraTagReplacementProcessor) proc));
            } else if (UltraModelModifierProcessor.class.isAssignableFrom(proc.getClass())) {
                iProcessors.add(createDelegatingFormReplacementProcessor((UltraModelModifierProcessor) proc));
            } else if (UltraAttributeModifierProcessor.class.isAssignableFrom(proc.getClass())) {
                iProcessors.add(createDelegatingAttributeModifierProcessor((UltraAttributeModifierProcessor) proc));
            } else if (UltraVariableModifierAttrProcessor.class.isAssignableFrom(proc.getClass())) {
                iProcessors.add(createDelegatingAttributeModelVariableModifierProcessor((UltraVariableModifierAttrProcessor) proc));
            } else {
                LOG.warn("No known delegating processor to instantiate processor " + proc);
            }
        }
        return iProcessors;
    }
    
    public Set<ITemplateResolver> getWebResolvers(Collection<UltraTemplateResolver> resolvers) {
        Set<ITemplateResolver> webResolvers = new HashSet<>();
        for (UltraTemplateResolver resolver : resolvers) {
            if (!resolver.isEmailResolver()) {
                ITemplateResolver iResolver = createCorrectTemplateResolver(resolver);
                if (iResolver != null) {
                    webResolvers.add(iResolver);
                }
            }

        }
        return webResolvers;
    }
    
    public Set<ITemplateResolver> getEmailResolvers(Collection<UltraTemplateResolver> resolvers) {
        Set<ITemplateResolver> emailResovlers = new HashSet<>();
        for (UltraTemplateResolver resolver : resolvers) {
            if (resolver.isEmailResolver()) {
                ITemplateResolver iResolver = createCorrectTemplateResolver(resolver);
                if (iResolver != null) {
                    emailResovlers.add(iResolver);
                }
            }

        }
        return emailResovlers;
    }

    protected DelegatingThymeleaf3VariableModifierProcessor createDelegatingModelVariableModifierProcessor(UltraVariableModifierProcessor processor) {
        return new DelegatingThymeleaf3VariableModifierProcessor(processor.getName(), processor, processor.getPrecedence());
    }
    
    protected DelegatingThymeleaf3TagTextModifierProcessor createDelegatingTagTextModifierProcessor(UltraTagTextModifierProcessor processor) {
        return new DelegatingThymeleaf3TagTextModifierProcessor(processor.getName(), processor, processor.getPrecedence());
    }
    
    protected DelegatingThymeleaf3TagReplacementProcessor createDelegatingTagReplacementProcessor(UltraTagReplacementProcessor processor) {
        return new DelegatingThymeleaf3TagReplacementProcessor(processor.getName(), processor, processor.getPrecedence());
    }
    
    protected DelegatingThymeleaf3ModelModifierProcessor createDelegatingFormReplacementProcessor(UltraModelModifierProcessor processor) {
        return new DelegatingThymeleaf3ModelModifierProcessor(processor.getName(), processor, processor.getPrecedence());
    }
    
    protected DelegatingThymeleaf3AttributeModifierProcessor createDelegatingAttributeModifierProcessor(UltraAttributeModifierProcessor processor) {
        return new DelegatingThymeleaf3AttributeModifierProcessor(processor.getName(), processor, processor.getPrecedence());
    }

    protected DelegatingThymeleaf3AttributeModelVariableModifierProcessor createDelegatingAttributeModelVariableModifierProcessor(UltraVariableModifierAttrProcessor processor) {
        return new DelegatingThymeleaf3AttributeModelVariableModifierProcessor(processor.getName(), processor, processor.getPrecedence());
    }
    
    protected ITemplateResolver createCorrectTemplateResolver(UltraTemplateResolver resolver) {
        if (UltraTemplateResolverType.CLASSPATH.equals(resolver.getResolverType())) {
            return createClassLoaderTemplateResolver(resolver);
        } else if (UltraTemplateResolverType.DATABASE.equals(resolver.getResolverType())) {
            return createDatabaseTemplateResolver(resolver);
        } else if (UltraTemplateResolverType.THEME_AWARE.equals(resolver.getResolverType())) {
            return createServletTemplateResolver(resolver);
        } else if (UltraTemplateResolverType.CUSTOM.equals(resolver.getResolverType())) {
            return createDelegatingThymeleaf3TemplateResolver(resolver);
        } else {
            LOG.warn("No known Thmeleaf 3 template resolver can be mapped to UltraThymeleafTemplateResolverType " + resolver.getResolverType());
            return null;
        }
    }
    
    protected ClassLoaderTemplateResolver createClassLoaderTemplateResolver(UltraTemplateResolver resolver) {
        ClassLoaderTemplateResolver classpathResolver = new ClassLoaderTemplateResolver();
        commonTemplateResolver(resolver, classpathResolver);
        classpathResolver.setCheckExistence(true);
        classpathResolver.setPrefix(resolver.getPrefix() + resolver.getTemplateFolder());
        return classpathResolver;
    }
    
    protected UltraThymeleaf3DatabaseTemplateResolver createDatabaseTemplateResolver(UltraTemplateResolver resolver) {
        UltraThymeleaf3DatabaseTemplateResolver databaseResolver = new UltraThymeleaf3DatabaseTemplateResolver();
        commonTemplateResolver(resolver, databaseResolver);
        databaseResolver.setPrefix(resolver.getPrefix() + resolver.getTemplateFolder());
        databaseResolver.setResourceResolverExtensionManager(applicationContext.getBean("ucDatabaseResourceResolverExtensionManager", DatabaseResourceResolverExtensionManager.class));
        databaseResolver.setUltraContextUtil(applicationContext.getBean("ucUltraContextUtil", UltraContextUtil.class));
        return databaseResolver;
    }

    protected UltraThymeleaf3ThemeAwareTemplateResolver createServletTemplateResolver(UltraTemplateResolver resolver) {
        UltraThymeleaf3ThemeAwareTemplateResolver servletResolver = applicationContext.getAutowireCapableBeanFactory().createBean(UltraThymeleaf3ThemeAwareTemplateResolver.class);
        commonTemplateResolver(resolver, servletResolver);
        servletResolver.setCheckExistence(true);
        servletResolver.setPrefix(resolver.getPrefix());
        servletResolver.setTemplateFolder(resolver.getTemplateFolder());
        return servletResolver;
    }

    protected DelegatingThymeleaf3TemplateResolver createDelegatingThymeleaf3TemplateResolver(UltraTemplateResolver resolver) {
        DelegatingThymeleaf3TemplateResolver delegatingResolver = applicationContext.getAutowireCapableBeanFactory().createBean(DelegatingThymeleaf3TemplateResolver.class);
        commonTemplateResolver(resolver, delegatingResolver);
        delegatingResolver.setTemplateResolver(resolver);
        delegatingResolver.setCheckExistence(true);
        delegatingResolver.setPrefix(resolver.getPrefix());
        return delegatingResolver;
    }

    /**
     * Utility method to convert all HTML5 template modes to HTML since the HTML
     * option in Thymeleaf 3 is HTML5 and the HTML5 option is deprecated 
     */
    protected UltraTemplateMode translateTemplateModeForThymeleaf3(UltraTemplateMode mode) {
        if (UltraTemplateMode.HTML5.equals(mode) || UltraTemplateMode.LEGACYHTML5.equals(mode)) {
            return UltraTemplateMode.HTML;
        }
        return mode;
    }
    
    protected void commonTemplateResolver(UltraTemplateResolver ucResolver, AbstractConfigurableTemplateResolver tlResolver) {
        tlResolver.setCacheable(ucResolver.isCacheable());
        tlResolver.setCacheTTLMs(ucResolver.getCacheTTLMs());
        tlResolver.setCharacterEncoding(ucResolver.getCharacterEncoding());
        tlResolver.setTemplateMode(translateTemplateModeForThymeleaf3(ucResolver.getTemplateMode()).toString());
        tlResolver.setOrder(ucResolver.getOrder());
        tlResolver.setSuffix(ucResolver.getSuffix());
    }
}
