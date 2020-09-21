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

import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.model.IText;

import java.util.ArrayList;

/**
 * Class that's used for encapsulating a Thymeleaf 3 text node
 * 
 * Note that this is only for use inside of the Ultra common layer for Thymeleaf module
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public class UltraThymeleaf3TextElement implements UltraThymeleaf3TemplateEvent {

    protected IText text;

    public UltraThymeleaf3TextElement(IText text) {
        this.text = text;
    }

    @Override
    public ArrayList<ITemplateEvent> getAllTags() {
        ArrayList<ITemplateEvent> tags = new ArrayList<>();
        tags.add(text);
        return tags;
    }

}
