package com.pighand.framework.spring.api.springdoc.analysis;

import com.pighand.framework.spring.api.springdoc.analysis.info.SpringDocInfo;
import org.springframework.util.StringUtils;

/**
 * schema解析
 *
 * @author wangshuli
 */
public class AnalysisSchema {

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
     * 整理schema对应的class
     *
     * @param refName
     * @param typeName
     */
    public static void schema2Map(String refName, String typeName) {
        // full className
        String className = typeName.replaceAll("\\[.*class ", "").replaceAll("\\]", "");

        if (StringUtils.hasText(refName)) {
            // put refName
            SpringDocInfo.refMapping.put(refName, className);

            // put beanName
            SpringDocInfo.refMapping.put(AnalysisSchema.refName2BeanName(refName), className);
        }
    }
}
