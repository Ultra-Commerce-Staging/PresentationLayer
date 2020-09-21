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
package com.ultracommerce.presentation.thymeleaf3.expression;

import com.ultracommerce.common.web.expression.UltraVariableExpression;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

public class UltraVariableExpressionObjectFactory implements IExpressionObjectFactory {

    @Resource
    protected List<UltraVariableExpression> expressions = new ArrayList<>();

    @Override
    public Set<String> getAllExpressionObjectNames() {
        Set<String> expressionObjectNames = new HashSet<>();
        for (UltraVariableExpression expression : expressions) {
            expressionObjectNames.add(expression.getName());
        }
        return expressionObjectNames;
    }

    @Override
    public Object buildObject(IExpressionContext context, String expressionObjectName) {
        if (context instanceof IWebContext) {
            for (UltraVariableExpression expression : expressions) {
                if (expressionObjectName.equals(expression.getName())) {
                    return expression;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isCacheable(String expressionObjectName) {
        return true;
    }

}
