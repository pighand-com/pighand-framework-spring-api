package com.pighand.framework.spring.api.springdoc;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.util.StringUtils;

/**
 * 获取springdoc的schema映射 {refName or beanName: className}
 *
 * @author wangshuli
 */
public class SpringDocProperty {

    /**
     * schema refName to beanName
     *
     * @param refName
     * @return beanName
     */
    public static String refName2BeanName(String refName) {
        return refName.replace("#/components/schemas/", "");
    }

    /**
     * 获取springdoc的schema映射
     *
     * @param schema
     * @param annotatedType
     * @return
     */
    public static Schema analysis(Schema schema, AnnotatedType annotatedType) {
        String refName = schema.get$ref();

        // full className
        String className =
                annotatedType
                        .getType()
                        .getTypeName()
                        .replaceAll("\\[.*class ", "")
                        .replaceAll("\\]", "");

        if (StringUtils.hasText(refName)) {
            // put refName
            SpringDocInfo.refMapping.put(refName, className);

            // put beanName
            SpringDocInfo.refMapping.put(refName2BeanName(refName), className);
        }

        return schema;
    }
}
