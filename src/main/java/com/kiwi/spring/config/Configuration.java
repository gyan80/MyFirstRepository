package com.kiwi.spring.config;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.InitializingBean;

public class Configuration implements InitializingBean {
    public PropertiesConfiguration properties;
    
    public Configuration(){
    }

    public void setProperties(PropertiesConfiguration properties) {
        this.properties = properties;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
    }

    public String get(String key) {
        return properties.getString(key);
    }
}