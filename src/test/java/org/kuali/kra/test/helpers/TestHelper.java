package org.kuali.kra.test.helpers;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kra.infrastructure.KraServiceLocator;

/**
 * A class that contains methods other TestHelper common to more
 * than one Helper.
 * 
 */
public class TestHelper {
	
	private Map<Class<?>, Object> serviceCache;


	public TestHelper() {
		serviceCache = new HashMap<Class<?>, Object>();
	}

    @SuppressWarnings("unchecked")
	protected final <T> T getService(Class<T> serviceClass) {
    	T service = (T) serviceCache.get(serviceClass);
    	if(service == null) {
    		service = KraServiceLocator.getService(serviceClass);
    		serviceCache.put(serviceClass, service);
    	}
        return service;
    }
	
}
