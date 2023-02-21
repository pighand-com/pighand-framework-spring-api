package com.pighand.framework.spring.api.springdoc.dataType;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * api doc空对象
 *
 * <p>在属性中增加：@Schema(implementation = EmptyObject.class)
 *
 * @author wangshuli
 */
@Schema(format = "json")
public class EmptyObject {}
