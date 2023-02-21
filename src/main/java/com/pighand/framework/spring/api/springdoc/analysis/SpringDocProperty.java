package com.pighand.framework.spring.api.springdoc.analysis;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;

/**
 * 获取springdoc - response的schema映射 {refName or beanName: className}
 *
 * @author wangshuli
 */
public class SpringDocProperty {

    /**
     * 获取springdoc的schema映射
     *
     * @param schema
     * @param annotatedType
     * @return
     */
    public static Schema analysis(Schema schema, AnnotatedType annotatedType) {
        String refName = schema.get$ref();

        AnalysisScheam.schema2Map(refName, annotatedType.getType().getTypeName());

        return schema;
    }
}
