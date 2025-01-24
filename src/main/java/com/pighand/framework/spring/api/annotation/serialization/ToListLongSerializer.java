package com.pighand.framework.spring.api.annotation.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * to long
 *
 * @author wangshuli
 */
public class ToListLongSerializer extends JsonDeserializer<List<Long>> {
    @Override
    public List<Long> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
        throws IOException {
        if (jsonParser == null) {
            return null;
        }

        String[] stringArray = jsonParser.readValueAs(String[].class);

        List<Long> result = new ArrayList<>();
        for (String s : stringArray) {
            try {
                result.add(Long.parseLong(s));
            } catch (NumberFormatException e) {
                throw new IOException("Invalid number format: " + s, e);
            }
        }
        return result;
    }
}
