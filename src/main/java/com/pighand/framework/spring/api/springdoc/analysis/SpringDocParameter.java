package com.pighand.framework.spring.api.springdoc.analysis;

import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.core.MethodParameter;

/**
 * 获取springdoc - request的schema映射 {refName or beanName: className}
 *
 * @author wangshuli
 */
public class SpringDocParameter {
    /**
     * 获取springdoc的schema映射
     *
     * @param parameterModel
     * @param methodParameter
     * @return
     */
    public static Parameter analysis(Parameter parameterModel, MethodParameter methodParameter) {
        if (parameterModel == null) {
            return null;
        }

        String refName = parameterModel.getSchema().get$ref();

        AnalysisSchema.schema2Map(refName, methodParameter.getParameterType().getTypeName());

        return parameterModel;
    }
}
