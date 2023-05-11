package com.pighand.framework.spring.api.jacksonSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对字段内容进行脱敏处理
 *
 * <p>不设置任何值，则全部替换
 *
 * <p>start: default 0; eg: (start=1) abc -> a**
 *
 * <p>end: default value.length; eg: (end=1) abc -> **c
 *
 * <p>prefix: (prefix=2) abc -> **c
 *
 * <p>suffix: (suffix=2) abc -> a**
 *
 * <p>markSize: 忽律字符数量，固定使用markSize的值
 *
 * <p>value: mark
 *
 * @author wangshuli
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Conceal {
    int start() default 0;

    int end() default 0;

    int prefix() default 0;

    int suffix() default 0;

    int markSize() default 0;

    String value() default "*";
}
