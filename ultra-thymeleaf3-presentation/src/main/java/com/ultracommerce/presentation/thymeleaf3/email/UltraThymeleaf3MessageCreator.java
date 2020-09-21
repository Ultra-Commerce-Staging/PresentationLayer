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
package com.ultracommerce.presentation.thymeleaf3.email;

import com.ultracommerce.common.email.service.info.EmailInfo;
import com.ultracommerce.common.email.service.message.MessageCreator;
import com.ultracommerce.common.web.UltraRequestContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Iterator;
import java.util.Map;

public class UltraThymeleaf3MessageCreator extends MessageCreator {

    private TemplateEngine templateEngine;
    
    public UltraThymeleaf3MessageCreator(TemplateEngine templateEngine, JavaMailSender mailSender) {
        super(mailSender);
        this.templateEngine = templateEngine;        
    }

    @Override
    public String buildMessageBody(EmailInfo info, Map<String,Object> props) {
        UltraRequestContext ucContext = UltraRequestContext.getUltraRequestContext();

        final Context thymeleafContext = new Context();
        if (ucContext != null && ucContext.getJavaLocale() != null) {
            thymeleafContext.setLocale(ucContext.getJavaLocale());
        }

        if (props != null) {
            Iterator<String> propsIterator = props.keySet().iterator();
            while(propsIterator.hasNext()) {
                String key = propsIterator.next();
                thymeleafContext.setVariable(key, props.get(key));
            }
        }

        return this.templateEngine.process( info.getEmailTemplate(), thymeleafContext);
    }
}
