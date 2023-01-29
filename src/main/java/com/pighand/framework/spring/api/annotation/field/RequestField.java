package com.pighand.framework.spring.api.annotation.field;

import java.lang.annotation.*;

/**
 * request field
 *
 * <p>被标注的实体，会在FieldGroup对应接口文档的request中显示
 *
 * @author wangshuli
 */
@Repeatable(RequestFields.class)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RequestField {

    String[] value() default {};

    boolean required() default false;
}
