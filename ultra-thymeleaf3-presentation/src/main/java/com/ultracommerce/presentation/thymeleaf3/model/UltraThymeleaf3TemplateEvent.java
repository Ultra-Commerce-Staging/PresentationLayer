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
import org.thymeleaf.model.ITemplateEvent;

import java.util.ArrayList;

/**
 * Interface that should be implemented for all {@code UltraThymeleafElement}s so that 
 * the module code can retrieve the underlying Thymeleaf 3 objects
 * 
 * Note that this is only for use inside of the Ultra common layer for Thymeleaf module
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public interface UltraThymeleaf3TemplateEvent extends UltraTemplateElement {

    public ArrayList<ITemplateEvent> getAllTags();
}
