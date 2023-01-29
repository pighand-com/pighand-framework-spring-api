package com.pighand.framework.spring.api.springdoc;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.*;

/**
 * 处理springdoc文档，根据group生成新的schema，重写文档
 *
 * @author wangshuli
 */
public class SpringDocOpenAPI {

    private DocInfo docInfo = SpringDocInfo.docInfo;
    private Map<String, String> refMapping = SpringDocInfo.refMapping;

    private OpenAPI openApi;

    public SpringDocOpenAPI(OpenAPI openApi) {
        this.openApi = openApi;

        if (refMapping.size() == 0) {
            return;
        }

        openApi.getPaths()
                .forEach(
                        (url, pathItem) -> {
                            analysisController("POST", url, pathItem.getPost());
                            analysisController("PUT", url, pathItem.getPut());
                            analysisController("DELETE", url, pathItem.getDelete());
                            analysisController("GET", url, pathItem.getGet());
                            analysisController("PATCH", url, pathItem.getPatch());
                        });
    }

    /**
     * 重写文档
     *
     * <p>根据方法的fieldGroup，查找request、response中是否设置对应的group字段
     *
     * <p>如果没设置，返回原文档
     *
     * <p>如果设置了，生成新的schema，覆盖文档
     *
     * @param method
     * @param url
     * @param operation
     */
    private void analysisController(String method, String url, Operation operation) {
        String apiUrl = DocFieldGroupUrl.url(method, url);
        MethodInfo methodInfo = docInfo.getUrl2MethodMapping().get(apiUrl);

        if (null == methodInfo) {
            return;
        }

        // 重写request body
        Content requestBody =
                Optional.ofNullable(operation.getRequestBody())
                        .map(RequestBody::getContent)
                        .orElse(new Content());

        requestBody.forEach(
                (contentType, item) -> {
                    Schema oldSchema = item.getSchema();
                    String refName = oldSchema.get$ref();
                    Schema schema = requestSchema(methodInfo.getMethodFieldGroupNames(), refName);

                    if (schema != null) {
                        oldSchema.set$ref(schema.getName());
                        item.schema(oldSchema);
                    }
                });

        // 重写request params
        List<Parameter> parameters =
                Optional.ofNullable(operation.getParameters()).orElse(new ArrayList(0));
        parameters.forEach(
                parameter -> {
                    Schema oldSchema = parameter.getSchema();
                    String refName = oldSchema.get$ref();
                    Schema schema = requestSchema(methodInfo.getMethodFieldGroupNames(), refName);

                    if (schema != null) {
                        oldSchema.set$ref(schema.getName());
                        parameter.setSchema(oldSchema);
                    }
                });

        // 重写response
        ApiResponses apiResponses =
                Optional.ofNullable(operation.getResponses()).orElse(new ApiResponses());
        apiResponses.forEach(
                (key, value) -> {
                    Content responseContent =
                            Optional.ofNullable(value.getContent()).orElse(new Content());

                    responseContent.forEach(
                            (contentType, item) -> {
                                Schema schema =
                                        responseSchema(
                                                methodInfo.getMethodFieldGroupNames(),
                                                methodInfo.getReturnType());

                                if (schema != null) {
                                    Schema oldSchema = item.getSchema();
                                    oldSchema.set$ref(schema.getName());
                                    item.setSchema(oldSchema);
                                }
                            });
                });
    }

    /**
     * 根据bean class，生成新的schema
     *
     * @param type
     * @return
     */
    private ResolvedSchema createSchema(Type type) {
        return ModelConverters.getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(type).resolveAsRef(false));
    }

    /**
     * 处理request schema
     *
     * @param methodGroupNames
     * @param refName
     * @return 返回设置后的schema；没有对应的group，返回null
     */
    private Schema requestSchema(Set<String> methodGroupNames, String refName) {
        if (!StringUtils.hasText(refName)) {
            return null;
        }

        try {
            String refClassName = refMapping.get(refName);

            Class clz = Class.forName(refClassName);
            ResolvedSchema resolvedSchema = createSchema(clz);

            return this.formatSchema("request", methodGroupNames, resolvedSchema);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * 处理response schema
     *
     * @param methodGroupNames
     * @param returnType
     * @return 返回设置后的schema；没有对应的group，返回null
     */
    private Schema responseSchema(Set<String> methodGroupNames, Type returnType) {
        ResolvedSchema resolvedSchema = createSchema(returnType);

        return this.formatSchema("response", methodGroupNames, resolvedSchema);
    }

    /**
     * 根据group信息，格式化schema
     *
     * @param type request、response
     * @param methodGroupNames
     * @param resolvedSchema new schema
     * @return 返回设置后的schema；没有对应的group，返回null
     */
    private Schema formatSchema(
            String type, Set<String> methodGroupNames, ResolvedSchema resolvedSchema) {
        // 解析的bean对应的schema
        Schema mainSchema = resolvedSchema.schema;
        String mainSchemaName = mainSchema.getName();

        // bean中字段是其他bean，会生成字段bean对应的schema
        Map<String, Schema> newSchemas = resolvedSchema.referencedSchemas;

        if (newSchemas == null || newSchemas.size() == 0) {
            return mainSchema;
        }

        for (String schemaName : newSchemas.keySet()) {
            Schema schema = newSchemas.get(schemaName);
            String schemaClassName = refMapping.get(schemaName);

            // schema对应的group字段信息
            List<FieldInfo> groupFieldInfos =
                    this.getGroupFieldInfos(schemaClassName, methodGroupNames);

            if (groupFieldInfos.size() == 0) {
                continue;
            }

            Set<String> formatGroupNames = new HashSet<>();
            groupFieldInfos.forEach(
                    fieldInfo -> {
                        Set<String> fields =
                                type.equals("request")
                                        ? fieldInfo.getRequestFields()
                                        : fieldInfo.getResponseFields();
                        Set<String> requiredFields =
                                type.equals("request")
                                        ? fieldInfo.getRequestRequiredFields()
                                        : fieldInfo.getResponseRequiredFields();
                        Set<String> exceptionFields =
                                type.equals("request")
                                        ? fieldInfo.getRequestExceptionFields()
                                        : fieldInfo.getResponseExceptionFields();

                        // 根据group，设置schema
                        this.setSchemaFiled(schema, fields, requiredFields, exceptionFields);

                        formatGroupNames.add(fieldInfo.getFileGroupName());
                    });

            // 将新schema添加到文档中
            schema.setName(schema.getName() + " -> " + String.join(" ,", formatGroupNames));
            openApi.schema(schema.getName(), schema);

            if (mainSchemaName.equals(schemaClassName)) {
                mainSchema = schema;
            }
        }

        // 将字段schema合并至主schema
        return this.mergeMainSchema(mainSchema, newSchemas);
    }

    /**
     * bean中字段是其他bean，会生成字段bean对应的schema。将字段schema合并至主schema
     *
     * <p>从主schema开始读取properties，如果property是schema，从newSchemas取格式化后的schema，回写到properties中。
     *
     * @param mainSchema
     * @param newSchemas 格式化后的所有schema
     * @return
     */
    private Schema mergeMainSchema(Schema mainSchema, Map<String, Schema> newSchemas) {
        if (mainSchema == null) {
            return null;
        }

        Map<String, Schema> properties = mainSchema.getProperties();
        properties.forEach(
                (propertyName, propertySchema) -> {
                    String propertySchemaRef = propertySchema.get$ref();

                    // 解析list类型字段: List<bean>
                    Schema propertyItems = propertySchema.getItems();
                    if (!StringUtils.hasText(propertySchemaRef) && propertyItems != null) {
                        propertySchemaRef = propertyItems.get$ref();
                    }

                    if (StringUtils.hasText(propertySchemaRef)) {
                        String schemaBeanName =
                                SpringDocProperty.refName2BeanName(propertySchemaRef);
                        Schema propertyNewSchema = newSchemas.get(schemaBeanName);

                        if (propertyNewSchema != null) {
                            propertyNewSchema = this.mergeMainSchema(propertyNewSchema, newSchemas);

                            properties.put(propertyName, propertyNewSchema);
                        }
                    }
                });

        mainSchema.setProperties(properties);
        return mainSchema;
    }

    /**
     * 根据group对应的字段，设置schema字段信息
     *
     * @param schema
     * @param fields 需要显示的字段
     * @param requiredFields 必填的字段
     * @param exceptionFields 不显示的字段
     */
    private void setSchemaFiled(
            Schema schema,
            Set<String> fields,
            Set<String> requiredFields,
            Set<String> exceptionFields) {

        // 设置必填字段
        if (!requiredFields.isEmpty()) {
            schema.setRequired(requiredFields.stream().toList());
        }

        // 没有对应的group，使用原文档schema
        if (fields.isEmpty() && requiredFields.isEmpty() && exceptionFields.isEmpty()) {
            return;
        }

        Iterator<String> iterator = schema.getProperties().keySet().iterator();

        while (iterator.hasNext()) {
            String key = iterator.next();

            // 是否删除字段 = 非必填 && 不显示
            boolean isRemove =
                    (exceptionFields.contains(key) || !fields.contains(key))
                            && !requiredFields.contains(key);

            if (isRemove) {
                iterator.remove();
            }
        }
    }

    /**
     * 获取schema对应的group字段信息
     *
     * @param refClassName schema对应的bean class name
     * @param methodGroupNames 接口的groupNames
     * @return
     */
    private List<FieldInfo> getGroupFieldInfos(String refClassName, Set<String> methodGroupNames) {
        // {fileGroupName, FieldInfo}
        Map<String, FieldInfo> fileGroupNames =
                Optional.ofNullable(this.docInfo.getClass2FieldMapping().get(refClassName))
                        .orElse(new HashMap<>(0));

        // 使用交集，取出group对应的字段信息
        Set<String> resSet = new HashSet<>();
        resSet.addAll(methodGroupNames);
        resSet.retainAll(fileGroupNames.keySet());

        List<FieldInfo> FieldInfos = new ArrayList<>(resSet.size());
        resSet.forEach(fileGroupName -> FieldInfos.add(fileGroupNames.get(fileGroupName)));

        return FieldInfos;
    }
}
