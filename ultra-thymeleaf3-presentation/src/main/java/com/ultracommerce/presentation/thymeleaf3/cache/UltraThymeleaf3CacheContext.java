/*
 * #%L
 * ultra-thymeleaf3-presentation
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
package com.ultracommerce.presentation.thymeleaf3.cache;

import com.ultracommerce.presentation.cache.UltraTemplateCacheContext;

public class UltraThymeleaf3CacheContext<K, V> implements UltraTemplateCacheContext<K, V> {

    protected UltraThymeleaf3ICache<K, V> ucCache;
    
    public UltraThymeleaf3CacheContext(UltraThymeleaf3ICache ucCache) {
        this.ucCache = ucCache;
    }
    
    @Override
    public V defaultGet(K key) {
        return ucCache.defaultGet(key);
    }

    @Override
    public void defaultPut(K key, V value) {
        ucCache.defaultPut(key, value);
    }

}
