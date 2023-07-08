package com.pighand.framework.spring.api.annotation.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * to long
 *
 * @author wangshuli
 */
public class ToLongSerializer extends JsonDeserializer<Long> {
    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        if (jsonParser == null) {
            return null;
        }
        return Long.valueOf(jsonParser.getText());
    }
}
