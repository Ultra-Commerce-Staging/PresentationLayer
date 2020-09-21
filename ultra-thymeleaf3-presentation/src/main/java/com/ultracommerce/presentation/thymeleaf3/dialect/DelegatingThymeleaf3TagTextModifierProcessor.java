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
package com.ultracommerce.presentation.thymeleaf3.dialect;

import com.ultracommerce.presentation.dialect.UltraTagTextModifierProcessor;
import com.ultracommerce.presentation.model.UltraTemplateContext;
import com.ultracommerce.presentation.thymeleaf3.model.UltraThymeleaf3Context;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.Map;

public class DelegatingThymeleaf3TagTextModifierProcessor extends AbstractAttributeTagProcessor {

    protected UltraTagTextModifierProcessor processor;
    
    public DelegatingThymeleaf3TagTextModifierProcessor(String name, UltraTagTextModifierProcessor processor, int precedence) {
        super(TemplateMode.HTML, processor.getPrefix(), null, false, name, true, precedence, true);
        this.processor = processor;
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        UltraTemplateContext ucContext = new UltraThymeleaf3Context(context, structureHandler);
        String tagName = tag.getElementCompleteName();
        Map<String, String> tagAttributes = tag.getAttributeMap();
        String newText = processor.getTagText(tagName, tagAttributes, attributeName.getAttributeName(), attributeValue, ucContext);
        if (newText != null) {
            structureHandler.setBody(newText, processor.textShouldBeProcessed());
        }

    }
}
