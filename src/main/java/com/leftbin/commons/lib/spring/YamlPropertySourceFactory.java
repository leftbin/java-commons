package com.leftbin.commons.lib.spring;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Objects;

@NoArgsConstructor
public class YamlPropertySourceFactory implements PropertySourceFactory {
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(encodedResource.getResource());
        return new PropertiesPropertySource(
            Objects.requireNonNull(encodedResource.getResource().getFilename()),
            Objects.requireNonNull(factory.getObject()));
    }
}
