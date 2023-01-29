package com.pighand.framework.spring.api.annotation.field;

import java.lang.annotation.*;

/**
 * request exception field
 *
 * <p>被标注的实体，不会在FieldGroup对应接口文档的request、response中显示
 *
 * @author wangshuli
 */
@Repeatable(FieldExceptions.class)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FieldException {

    String[] value() default {};
}
