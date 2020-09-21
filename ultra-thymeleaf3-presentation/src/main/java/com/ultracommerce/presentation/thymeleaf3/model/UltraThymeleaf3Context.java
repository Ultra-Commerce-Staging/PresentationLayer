/*
 * #%L
 * ultra-common-thymeleaf
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
package com.ultracommerce.presentation.thymeleaf3.model;

import com.ultracommerce.presentation.model.UltraAssignation;
import com.ultracommerce.presentation.model.UltraBindStatus;
import com.ultracommerce.presentation.model.UltraTemplateContext;
import com.ultracommerce.presentation.model.UltraTemplateElement;
import com.ultracommerce.presentation.model.UltraTemplateModel;
import com.ultracommerce.presentation.model.UltraTemplateNonVoidElement;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.IText;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.util.FieldUtils;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationUtils;
import org.thymeleaf.standard.expression.StandardExpressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Concrete implementation of utilities that can be done during execution of a processor.
 * The underlying encapsulated object is an {@code ITemplateContext}
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public class UltraThymeleaf3Context implements UltraTemplateContext {

    protected ITemplateContext context;
    protected IElementModelStructureHandler modelHandler;
    protected IElementTagStructureHandler tagHandler;

    public UltraThymeleaf3Context(ITemplateContext context, IElementModelStructureHandler modelHandler) {
        this.context = context;
        this.modelHandler = modelHandler;
        this.tagHandler = null;
    }
    
    public UltraThymeleaf3Context(ITemplateContext context, IElementTagStructureHandler tagHandler) {
        this.context = context;
        this.tagHandler = tagHandler;
        this.modelHandler = null;
    }

    @Override
    public <T> T parseExpression(String value) {
        return (T) StandardExpressions.getExpressionParser(context.getConfiguration())
            .parseExpression(context, value)
            .execute(context);
    }
    
    @Override
    public List<UltraAssignation> getAssignationSequence(String value, boolean allowParametersWithoutValue) {
        List<UltraAssignation> assignations = new ArrayList<>();
        for (Assignation assignation : AssignationUtils.parseAssignationSequence(context, value, allowParametersWithoutValue)) {
            assignations.add(new UltraThymeleaf3Assignation(assignation));
        }
        return assignations;
    }

    public ITemplateContext getThymeleafContext() {
        return this.context;
    }

    @Override
    public UltraTemplateNonVoidElement createNonVoidElement(String tagName, Map<String, String> attributes, boolean useDoubleQuotes) {
        IOpenElementTag open = context.getModelFactory().createOpenElementTag(tagName, attributes, useDoubleQuotes ? AttributeValueQuotes.DOUBLE : AttributeValueQuotes.SINGLE, false);
        ICloseElementTag close = context.getModelFactory().createCloseElementTag(tagName, false, false);
        return new UltraThymeleaf3NonVoidElement(open, close);
    }

    @Override
    public UltraTemplateNonVoidElement createNonVoidElement(String tagName) {
        IOpenElementTag open = context.getModelFactory().createOpenElementTag(tagName);
        ICloseElementTag close = context.getModelFactory().createCloseElementTag(tagName, false, false);
        return new UltraThymeleaf3NonVoidElement(open, close);
    }

    @Override
    public UltraTemplateElement createStandaloneElement(String tagName, Map<String, String> attributes, boolean useDoubleQuotes) {
        IStandaloneElementTag standaloneTag = context.getModelFactory().createStandaloneElementTag(tagName, attributes, useDoubleQuotes ? AttributeValueQuotes.DOUBLE : AttributeValueQuotes.SINGLE, false, true);
        return new UltraThymeleaf3StandaloneElement(standaloneTag);
    }

    @Override
    public UltraTemplateElement createStandaloneElement(String tagName) {
        IStandaloneElementTag standaloneTag = context.getModelFactory().createStandaloneElementTag(tagName);
        return new UltraThymeleaf3StandaloneElement(standaloneTag);
    }

    @Override
    public UltraTemplateElement createTextElement(String text) {
        IText textNode = context.getModelFactory().createText(text);
        return new UltraThymeleaf3TextElement(textNode);
    }

    @Override
    public UltraTemplateModel createModel() {
        return new UltraThymeleaf3Model(context.getModelFactory().createModel());
    }

    @Override
    public void setNodeLocalVariable(UltraTemplateElement element, String key, Object value) {
        if (modelHandler != null) {
            modelHandler.setLocalVariable(key, value);
        } else if (tagHandler != null) {
            tagHandler.setLocalVariable(key, value);
        }
    }

    @Override
    public void setNodeLocalVariables(UltraTemplateElement element, Map<String, Object> variableMap) {
        if (modelHandler != null) {
            for (String key : variableMap.keySet()) {
                modelHandler.setLocalVariable(key, variableMap.get(key));
            }
        } else if (tagHandler != null) {
            for (String key : variableMap.keySet()) {
                tagHandler.setLocalVariable(key, variableMap.get(key));
            }
        }
    }

    @Override
    public Object getVariable(String name) {
        return context.getVariable(name);
    }

    @Override
    public UltraBindStatus getBindStatus(String attributeValue) {
        return new UltraThymeleaf3BindStatus(FieldUtils.getBindStatus(context, attributeValue));
    }

    @Override
    public HttpServletRequest getRequest() {
        if (context instanceof WebEngineContext) {
            return ((WebEngineContext) context).getRequest();
        }
        return null;
    }

}
