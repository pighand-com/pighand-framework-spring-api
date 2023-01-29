package com.pighand.framework.spring.api.annotation.field;

import java.lang.annotation.*;

/**
 * response field
 *
 * <p>被标注的实体，会在FieldGroup对应接口文档的response中显示
 *
 * @author wangshuli
 */
@Repeatable(ResponseFields.class)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ResponseField {

    String[] value() default {};

    boolean required() default false;
}
