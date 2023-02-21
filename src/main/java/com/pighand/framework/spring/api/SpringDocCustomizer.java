package com.pighand.framework.spring.api;

import com.pighand.framework.spring.api.springdoc.SpringDocOpenAPI;
import com.pighand.framework.spring.api.springdoc.analysis.SpringDocRouterOperation;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.RouterOperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 重写swagger文档，使文档支持request bean、response bean的字段分组
 *
 * <p>需要再Application中增加以下内容，来获取schema对应的class信息
 *
 * <p><code>
 * @Bean
 * public PropertyCustomizer propertyCustomizer() {
 *  return (schema, annotatedType) -> SpringDocProperty.analysis(schema, annotatedType);
 * }
 *
 * @Bean
 * public ParameterCustomizer propertyCustomizers() {
 *  return (parameterModel, methodParameter) -> SpringDocParameter.analysis(parameterModel, methodParameter);
 * }
 * </code>
 *
 * @author wangshuli
 */
@Configuration
public class SpringDocCustomizer {

    /**
     * 重新生成文档
     *
     * @return
     */
    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> new SpringDocOpenAPI(openApi);
    }

    /**
     * 解析接口方法
     *
     * @return
     */
    @Bean
    public RouterOperationCustomizer routerOperationCustomizer() {
        return (routerOperation, handlerMethod) ->
                new SpringDocRouterOperation().analysis(routerOperation, handlerMethod);
    }
}
