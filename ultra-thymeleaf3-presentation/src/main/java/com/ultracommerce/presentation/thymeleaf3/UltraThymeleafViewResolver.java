/*
 * #%L
 * UltraCommerce Common Libraries
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
package com.ultracommerce.presentation.thymeleaf3;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.ultracommerce.common.extension.ExtensionResultHolder;
import com.ultracommerce.common.util.UCSystemProperty;
import com.ultracommerce.common.web.UltraRequestContext;
import com.ultracommerce.common.web.UltraTemplateViewResolverExtensionManager;
import com.ultracommerce.common.web.controller.UltraControllerUtility;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.spring5.view.AbstractThymeleafView;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class extends the default ThymeleafViewResolver to facilitate rendering
 * template fragments (such as those used by AJAX modals or iFrames) within a
 * full page container should the request for that template have occurred in a
 * stand-alone context.
 *
 * @author Andre Azzolini (apazzolini)
 */
public class UltraThymeleafViewResolver extends ThymeleafViewResolver {
    private static final Log LOG = LogFactory.getLog(UltraThymeleafViewResolver.class);

    @Resource(name = "ucUltraTemplateViewResolverExtensionManager")
    protected UltraTemplateViewResolverExtensionManager extensionManager;

    public static final String EXTENSION_TEMPLATE_ATTR_NAME = "extensionTemplateAttr";

    /**
     * <p>
     *   Prefix to be used in view names (returned by controllers) for specifying an
     *   HTTP redirect with AJAX support. That is, if you want a redirect to be followed
     *   by the browser as the result of an AJAX call or within an iFrame at the parent
     *   window, you can utilize this prefix. Note that this requires a JavaScript component,
     *   which is provided as part of UC.js
     *
     *   If the request was not performed in an AJAX / iFrame context, this method will
     *   delegate to the normal "redirect:" prefix.
     * </p>
     * <p>
     *   Value: <tt>ajaxredirect:</tt>
     * </p>
     */
    public static final String AJAX_REDIRECT_URL_PREFIX = "ajaxredirect:";

    protected Map<String, String> layoutMap = new HashMap<>();
    protected String fullPageLayout = "layout/fullPageLayout";
    protected String iframeLayout = "layout/iframeLayout";

    protected boolean useThymeleafLayoutDialect() {
        return UCSystemProperty.resolveBooleanSystemProperty("thymeleaf.useLayoutDialect");
    }

    /*
     * This method is a copy of the same method in ThymeleafViewResolver, but since it is marked private,
     * we are unable to call it from the UltraThymeleafViewResolver
     */
    protected boolean canHandle(final String viewName) {
        final String[] viewNamesToBeProcessed = getViewNames();
        final String[] viewNamesNotToBeProcessed = getExcludedViewNames();
        return ((viewNamesToBeProcessed == null || PatternMatchUtils.simpleMatch(viewNamesToBeProcessed, viewName)) &&
                (viewNamesNotToBeProcessed == null || !PatternMatchUtils.simpleMatch(viewNamesNotToBeProcessed, viewName)));
    }

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        ExtensionResultHolder<String> erh = new ExtensionResultHolder<>();
        extensionManager.getProxy().overrideView(erh, viewName, isAjaxRequest());

        String viewOverride = erh.getResult();

        if (viewOverride != null) {
            viewName = viewOverride;
        }

        return super.resolveViewName(viewName, locale);
    }

    /**
     * Determines which internal method to call for creating the appropriate view. If no
     * Ultra specific methods match the viewName, it delegates to the parent
     * ThymeleafViewResolver createView method
     */
    @Override
    protected View createView(final String viewName, final Locale locale) throws Exception {
        if (!canHandle(viewName)) {
            LOG.trace("[THYMELEAF] View {" + viewName + "} cannot be handled by ThymeleafViewResolver. Passing on to the next resolver in the chain");
            return null;
        }

        if (viewName.startsWith(AJAX_REDIRECT_URL_PREFIX)) {
            LOG.trace("[THYMELEAF] View {" + viewName + "} is an ajax redirect, and will be handled directly by UltraThymeleafViewResolver");
            String redirectUrl = viewName.substring(AJAX_REDIRECT_URL_PREFIX.length());
            return loadAjaxRedirectView(redirectUrl, locale);
        }

        return super.createView(viewName, locale);
    }

    /**
     * Performs a Ultra AJAX redirect. This is used in conjunction with UC.js to support
     * doing a browser page change as as result of an AJAX call.
     *
     * @param redirectUrl
     * @param locale
     * @return
     * @throws Exception
     */
    protected View loadAjaxRedirectView(String redirectUrl, final Locale locale) throws Exception {
        if (isAjaxRequest()) {
            initializeAjaxRedirectFlashmap(redirectUrl);
            String viewName = "utility/ucRedirect";
            addStaticVariable(UltraControllerUtility.UC_REDIRECT_ATTRIBUTE, redirectUrl);
            return super.loadView(viewName, locale);
        } else {
            return new RedirectView(redirectUrl, false, isRedirectHttp10Compatible());
        }
    }

    /**
     * Sets up the Spring MVC FlashMap to replicate what happens when you return "redirect:"
     * from a Spring controller. This code comes directly from {@link RedirectView#renderMergedOutputModel}
     * @param redirectUrl URL to redirect to
     */
    protected void initializeAjaxRedirectFlashmap(String redirectUrl) {
        UltraRequestContext brc = UltraRequestContext.getUltraRequestContext();
        HttpServletRequest req = brc == null ? null : brc.getRequest();
        HttpServletResponse res = brc == null ? null : brc.getResponse();
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(req);
        if (!CollectionUtils.isEmpty(flashMap) && req != null && res != null) {
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(redirectUrl).build();
            flashMap.setTargetRequestPath(uriComponents.getPath());
            flashMap.addTargetRequestParams(uriComponents.getQueryParams());
            FlashMapManager flashMapManager = RequestContextUtils.getFlashMapManager(req);
            if (flashMapManager == null) {
                throw new IllegalStateException("FlashMapManager not found despite output FlashMap having been set");
            }
            flashMapManager.saveOutputFlashMap(flashMap, req, res);
        }
    }

    @Override
    protected View loadView(final String originalViewName, final Locale locale) throws Exception {
        String viewName = originalViewName;

        if (!isAjaxRequest() && !useThymeleafLayoutDialect()) {
            String longestPrefix = "";

            for (Entry<String, String> entry : layoutMap.entrySet()) {
                String viewPrefix = entry.getKey();
                String viewLayout = entry.getValue();

                if (viewPrefix.length() > longestPrefix.length()) {
                    if (originalViewName.startsWith(viewPrefix)) {
                        longestPrefix = viewPrefix;

                        if (!"NONE".equals(viewLayout)) {
                            viewName = viewLayout;
                        }
                    }
                }
            }

            if (longestPrefix.equals("")) {
                viewName = getFullPageLayout();
            }
        }

        AbstractThymeleafView view = null;
        boolean ajaxRequest = isAjaxRequest();

        ExtensionResultHolder<String> erh = new ExtensionResultHolder<>();
        extensionManager.getProxy().provideTemplateWrapper(erh, originalViewName, ajaxRequest);
        String templateWrapper = erh.getResult();

        if (templateWrapper != null && ajaxRequest) {
            view = (AbstractThymeleafView) super.loadView(templateWrapper, locale);
            view.addStaticVariable("wrappedTemplate", viewName);
        } else {
            view = (AbstractThymeleafView) super.loadView(viewName, locale);
        }

        if (!ajaxRequest) {
            view.addStaticVariable("templateName", originalViewName);
        }

        return view;
    }

    @Override
    protected Object getCacheKey(String viewName, Locale locale) {
        String cacheKey = viewName + "_" + locale + "_" + isAjaxRequest();

        ExtensionResultHolder<String> erh = new ExtensionResultHolder<>();
        extensionManager.getProxy().appendCacheKey(erh, viewName, isAjaxRequest());

        String addlCacheKey = erh.getResult();

        if (addlCacheKey != null) {
            cacheKey = cacheKey + "_" + addlCacheKey;
        }

        return cacheKey;
    }

    protected boolean isIFrameRequest() {
        WebRequest request = getCurrentRequest();

        String iFrameParameter = request.getParameter("ucIFrame");
        return (iFrameParameter != null && "true".equals(iFrameParameter));
    }

    protected boolean isAjaxRequest() {
        WebRequest request = getCurrentRequest();
        if (request == null) {
            return false;
        }
        return UltraControllerUtility.isAjaxRequest(request);
    }

    protected WebRequest getCurrentRequest() {
        // First, let's try to get it from the UltraRequestContext
        WebRequest request = null;
        if (UltraRequestContext.getUltraRequestContext() != null) {
            WebRequest brcRequest = UltraRequestContext.getUltraRequestContext().getWebRequest();
            if (brcRequest != null) {
                request = brcRequest;
            }
        }

        // If we didn't find it there, we might be outside of a security-configured uri. Let's see if the filter got it
        if (request == null) {
            try {
                HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                request = new ServletWebRequest(servletRequest);
            } catch (ClassCastException e) {
                // In portlet environments, we won't be able to cast to a ServletRequestAttributes. We don't want to
                // blow up in these scenarios.
                LOG.warn("Unable to cast to ServletRequestAttributes and the request in UltraRequestContext " +
                         "was not set. This may introduce incorrect AJAX behavior.");
            }
        }

        return request;
    }

    /**
     * Gets the map of prefix : layout for use in determining which layout
     * to dispatch the request to in non-AJAX calls
     *
     * @return the layout map
     */
    public Map<String, String> getLayoutMap() {
        return layoutMap;
    }

    /**
     * @see #getLayoutMap()
     * @param layoutMap
     */
    public void setLayoutMap(Map<String, String> layoutMap) {
        this.layoutMap = layoutMap;
    }

    /**
     * The default layout to use if there is no specifc entry in the layout map
     *
     * @return the full page layout
     */
    public String getFullPageLayout() {
        return fullPageLayout;
    }

    /**
     * @see #getFullPageLayout()
     * @param fullPageLayout
     */
    public void setFullPageLayout(String fullPageLayout) {
        this.fullPageLayout = fullPageLayout;
    }

    /**
     * The layout to use for iframe requests
     *
     * @return the iframe layout
     */
    public String getIframeLayout() {
        return iframeLayout;
    }

    /**
     * @see #getIframeLayout()
     * @param iframeLayout
     */
    public void setIframeLayout(String iframeLayout) {
        this.iframeLayout = iframeLayout;
    }

}
