/*
 * #%L
 * ultra-thymeleaf3-presentation
 * %%
 * Copyright (C) 2009 - 2017 Ultra Commerce
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
package com.ultracommerce.presentation.thymeleaf3.resolver;

import org.thymeleaf.templateresource.ITemplateResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author Jon Fleschler (jfleschler)
 */
public class UltraThymeleaf3ITemplateResource implements ITemplateResource {

    protected String resourceName;
    protected InputStream inputStream;

    public UltraThymeleaf3ITemplateResource(String resourceName, InputStream inputStream) {
        this.resourceName = resourceName;
        this.inputStream = inputStream;
    }

    @Override
    public String getDescription() {
        return "UC_CUSTOM";
    }

    @Override
    public String getBaseName() {
        return resourceName;
    }

    @Override
    public boolean exists() {
        return inputStream != null;
    }

    @Override
    public Reader reader() throws IOException {
        return inputStream == null ? null : new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public ITemplateResource relative(String s) {
        return null;
    }
}
