package com.pighand.framework.spring.api.annotation.field;

import java.lang.annotation.*;

/**
 * request field exception
 *
 * <p>被标注的实体，不会在FieldGroup对应接口文档的request中显示
 *
 * @author wangshuli
 */
@Repeatable(RequestFieldExceptions.class)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RequestFieldException {

    String[] value() default {};
}
