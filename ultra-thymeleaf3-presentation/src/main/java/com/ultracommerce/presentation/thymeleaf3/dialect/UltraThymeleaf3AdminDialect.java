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
package com.ultracommerce.presentation.thymeleaf3.dialect;

import com.ultracommerce.presentation.dialect.UltraDialectPrefix;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

public class UltraThymeleaf3AdminDialect extends AbstractProcessorDialect implements IExpressionObjectDialect {

    @Resource(name = "ucVariableExpressionObjectFactory")
    protected IExpressionObjectFactory expressionObjectFactory;

    private Set<IProcessor> processors = new HashSet<>();

    public UltraThymeleaf3AdminDialect() {
        super("Ultra Admin Dialect", UltraDialectPrefix.UC_ADMIN.toString(), SpringStandardDialect.PROCESSOR_PRECEDENCE);
    }

    public void setProcessors(Set<IProcessor> processors) {
        this.processors = processors;
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        return processors;
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return expressionObjectFactory;
    }


}
