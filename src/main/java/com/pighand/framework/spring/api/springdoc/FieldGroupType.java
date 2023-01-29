package com.pighand.framework.spring.api.springdoc;

/**
 * 字段分组的类型
 *
 * @author wangshuli
 */
public enum FieldGroupType {
    // request字段
    REQUEST,
    // request排除字段
    REQUEST_EXCEPTION,

    // response字段
    RESPONSE,
    // response排除字段
    RESPONSE_EXCEPTION,
}
