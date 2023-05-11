package com.pighand.framework.spring.api.jacksonSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * 内容脱敏
 *
 * <p>使用@Conceal注解，可对字段内容进行脱敏处理
 *
 * @author wangshuli
 */
public class ConcealSerializer extends JsonSerializer<String> implements ContextualSerializer {
    private Integer start;
    private Integer end;
    private Integer prefix;
    private Integer suffix;
    private Integer markSize;
    private String mark;

    /** 是否需要脱敏 */
    private boolean shouldMark = false;

    public ConcealSerializer() {}

    public ConcealSerializer(
            int start, int end, int prefix, int suffix, int markSize, String mark) {
        this.start = start;
        this.end = end;
        this.prefix = prefix;
        this.suffix = suffix;
        this.markSize = markSize;
        this.mark = mark;
        this.shouldMark = true;
    }

    /**
     * 字符串脱敏
     *
     * @param value
     * @return
     */
    private String mark(String value) {
        Integer calPrefix = Math.min(this.prefix, value.length());
        Integer calSuffix = Math.min(this.suffix, value.length());

        Integer calStart = Math.max(this.start, 0);

        Integer calEnd = this.end;
        if (this.end > value.length() || this.end == 0) {
            calEnd = value.length();
        } else if (this.end < 0) {
            calEnd = this.end + value.length();
        }

        boolean isByIndex =
                (this.start == 0 && this.end == 0 && (this.prefix > 0 || this.suffix > 0))
                        || (calEnd - calStart) <= 0;
        if (isByIndex) {
            calStart = null;
            calEnd = null;
        }

        if (calPrefix <= 0) {
            calPrefix = null;
        }

        if (calSuffix <= 0) {
            calSuffix = null;
        }

        int replaceSize = this.markSize;
        if (calStart != null && calEnd != null) {
            replaceSize =
                    replaceSize > 0
                            ? this.markSize
                            : ((calEnd - calStart) == 0 ? value.length() : (calEnd - calStart));

            String masked = StringUtils.repeat(this.mark, replaceSize);
            value = value.substring(0, calStart) + masked + value.substring(calEnd);
        }

        if (calPrefix != null) {
            value =
                    StringUtils.repeat(this.mark, replaceSize > 0 ? replaceSize : calPrefix)
                            + value.substring(calPrefix);
        }

        if (calSuffix != null) {
            value =
                    value.substring(0, value.length() - calSuffix)
                            + StringUtils.repeat(
                                    this.mark, replaceSize > 0 ? replaceSize : calSuffix);
        }

        return value;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (this.shouldMark) {
            value = this.mark(value);
        }

        gen.writeString(value);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {

        Conceal annotation = property.getAnnotation(Conceal.class);

        if (annotation != null) {
            return new ConcealSerializer(
                    annotation.start(),
                    annotation.end(),
                    annotation.prefix(),
                    annotation.suffix(),
                    annotation.markSize(),
                    annotation.value());
        }

        return new ConcealSerializer();
    }
}
