package com.pighand.framework.spring.api.jacksonSerializer;

import com.fasterxml.jackson.databind.module.SimpleModule;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * custom jackson serializer
 *
 * @author wangshuli
 */
public class JacksonSerializer implements Jackson2ObjectMapperBuilderCustomizer {

    @Override
    public void customize(Jackson2ObjectMapperBuilder jacksonBuilder) {
        SimpleModule module = new SimpleModule();

        // 脱敏
        module.addSerializer(String.class, new ConcealSerializer());

        jacksonBuilder.modules(module);
    }
}
