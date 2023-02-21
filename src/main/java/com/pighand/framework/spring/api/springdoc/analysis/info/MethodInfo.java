package com.pighand.framework.spring.api.springdoc.analysis.info;

import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * 文档方法信息
 *
 * @author wangshuli
 */
@Data
public class MethodInfo {

    private Class cls;
    private Method method;
    private Type returnType;
    private List<Parameter> parameters;
    private Set<String> methodFieldGroupNames;
}
