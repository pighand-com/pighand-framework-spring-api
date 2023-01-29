package com.pighand.framework.spring.api.annotation.field;

import java.lang.annotation.*;

/**
 * 分组关键字
 *
 * <p>在controller的method中使用
 *
 * <p>实体使用RequestField、ResponseField，根据fieldGroup的值，判断是否显示在对应的接口文档中
 *
 * @author wangshuli
 */
@Target({
    ElementType.TYPE,
    ElementType.ANNOTATION_TYPE,
    ElementType.FIELD,
})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FieldGroup {
    String value() default "";
}
