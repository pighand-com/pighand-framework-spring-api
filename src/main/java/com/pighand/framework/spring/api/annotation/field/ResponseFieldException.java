package com.pighand.framework.spring.api.annotation.field;

import java.lang.annotation.*;

/**
 * response field exception
 *
 * <p>被标注的实体，不会在FieldGroup对应接口文档的response中显示
 *
 * @author wangshuli
 */
@Repeatable(ResponseFieldExceptions.class)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ResponseFieldException {

    String[] value() default {};
}
