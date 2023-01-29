package com.pighand.framework.spring.api.annotation.field;

import java.lang.annotation.*;

/**
 * request field
 *
 * <p>被标注的实体，会在FieldGroup对应接口文档的request、response中显示
 *
 * @author wangshuli
 */
@Repeatable(Fields.class)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Field {
    /**
     * 分组关键字
     *
     * <p>支持fieldGroup > docDescription
     *
     * @return
     */
    String[] value() default {};

    /**
     * 当前分组中是否必填
     *
     * @return
     */
    boolean required() default false;
}
