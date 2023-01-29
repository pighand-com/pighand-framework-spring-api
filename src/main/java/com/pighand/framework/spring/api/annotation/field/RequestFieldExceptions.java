package com.pighand.framework.spring.api.annotation.field;

import java.lang.annotation.*;

/**
 * request field
 *
 * <p>被标注的实体，会在FieldGroup对应接口文档的request中显示
 *
 * @author wangshuli
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RequestFieldExceptions {

    RequestFieldException[] value() default {};
}
