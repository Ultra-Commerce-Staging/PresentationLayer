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
import com.ultracommerce.presentation.model.UltraTemplateContext;
import org.thymeleaf.standard.expression.Assignation;

public class UltraThymeleaf3Assignation implements UltraAssignation {

    protected Assignation assignation;

    public UltraThymeleaf3Assignation(Assignation assignation) {
        this.assignation = assignation;
    }

    @Override
    public Object parseLeft(UltraTemplateContext context) {
        return assignation.getLeft().execute(((UltraThymeleaf3Context) context).getThymeleafContext());
    }

    @Override
    public Object parseRight(UltraTemplateContext context) {
        return assignation.getRight().execute(((UltraThymeleaf3Context) context).getThymeleafContext());
    }

    @Override
    public String getLeftStringRepresentation(UltraTemplateContext context) {
        return assignation.getLeft().getStringRepresentation();
    }

    @Override
    public String getRightStringRepresentation(UltraTemplateContext context) {
        return assignation.getRight().getStringRepresentation();
    }

}
