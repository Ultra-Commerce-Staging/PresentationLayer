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

import com.ultracommerce.presentation.model.UltraTemplateElement;
import com.ultracommerce.presentation.model.UltraTemplateModel;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.ITemplateEvent;

/**
 * A class used to encapsulate the underlying IModel for Thymeleaf 3. 
 * The model is modified using {@code UltraThymeleafTemplateEvent}s and then used to
 * modify the original model sent to the processor
 * 
 * Note that this is only for use inside of the Ultra common layer for Thymeleaf module
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public class UltraThymeleaf3Model implements UltraTemplateModel {

    protected IModel model;

    public UltraThymeleaf3Model(IModel model) {
        this.model = model;
    }
    @Override
    public void addElement(UltraTemplateElement elem) {
        for (ITemplateEvent tag : ((UltraThymeleaf3TemplateEvent) elem).getAllTags()) {
            model.add(tag);
        }
    }

    public IModel getModel() {
        return model;
    }

}
