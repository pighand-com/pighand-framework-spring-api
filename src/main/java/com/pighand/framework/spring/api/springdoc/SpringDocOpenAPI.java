package com.pighand.framework.spring.api.springdoc;

import com.pighand.framework.spring.api.springdoc.analysis.AnalysisSchema;
import com.pighand.framework.spring.api.springdoc.analysis.info.DocInfo;
import com.pighand.framework.spring.api.springdoc.analysis.info.FieldInfo;
import com.pighand.framework.spring.api.springdoc.analysis.info.MethodInfo;
import com.pighand.framework.spring.api.springdoc.analysis.info.SpringDocInfo;
import com.pighand.framework.spring.api.springdoc.pageParams.AddPageParams;
import com.pighand.framework.spring.api.springdoc.utils.DocFieldGroupUrl;

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

        // 添加分页查询参数
        new AddPageParams(openApi);

        // 根据group处理文档
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
        this.rewriteRequestBody(operation, methodInfo);

        // 重写request params
        this.rewriteRequestParams(operation, methodInfo);

        // 重写response
        this.rewriteResponses(operation, methodInfo);
    }

    /**
     * 重写request body
     *
     * @param operation
     * @param methodInfo
     */
    private void rewriteRequestBody(Operation operation, MethodInfo methodInfo) {
        Content requestBody =
                Optional.ofNullable(operation.getRequestBody())
                        .map(RequestBody::getContent)
                        .orElse(new Content());

        requestBody.forEach(
                (contentType, item) -> {
                    Schema oldSchema = item.getSchema();

                    if (null == oldSchema) {
                        return;
                    }

                    String refName = oldSchema.get$ref();
                    Schema schema =
                            requestSchema(
                                    methodInfo.getMethodFieldGroupNames(),
                                    methodInfo.getValidationGroupNames(),
                                    refName);

                    if (schema != null) {
                        oldSchema.set$ref(schema.getName());
                        item.schema(oldSchema);
                    }
                });
    }

    /**
     * 重写request params
     *
     * @param operation
     * @param methodInfo
     */
    private void rewriteRequestParams(Operation operation, MethodInfo methodInfo) {
        List<Parameter> parameters =
                Optional.ofNullable(operation.getParameters()).orElse(new ArrayList(0));

        // request params
        List<Parameter> requestParameters = new ArrayList<>(parameters.size());

        // request schema to params
        List<Parameter> schemaParameters = new ArrayList<>(parameters.size());

        parameters.forEach(
                parameter -> {
                    Schema oldSchema = parameter.getSchema();

                    if (null == oldSchema) {
                        requestParameters.add(parameter);
                        return;
                    }

                    String refName = oldSchema.get$ref();
                    Schema schema =
                            requestSchema(
                                    methodInfo.getMethodFieldGroupNames(),
                                    methodInfo.getValidationGroupNames(),
                                    refName);

                    if (schema != null) {
                        // 更新新的schema
                        oldSchema.set$ref(schema.getName());
                        parameter.setSchema(oldSchema);

                        // 根据schema生成params
                        schemaParameters.addAll(schema2Parameters(schema));
                    }
                });

        // 如果存在schema。将schema转为params
        if (schemaParameters.size() > 0) {
            schemaParameters.addAll(requestParameters);
            operation.setParameters(schemaParameters);
        }
    }

    /**
     * 根据schema生成parameters
     *
     * @param schema
     * @return
     */
    private List<Parameter> schema2Parameters(Schema schema) {
        Map<String, Schema> properties = schema.getProperties();

        List<Parameter> parameter = new ArrayList<>(properties.size());

        properties
                .keySet()
                .forEach(
                        key -> {
                            Schema temSchema = properties.get(key);

                            Parameter newParameter = new Parameter();
                            newParameter.in("query");
                            newParameter.name(key);
                            newParameter.description(temSchema.getDescription());
                            newParameter.required(
                                    schema.getRequired() != null
                                            && schema.getRequired().contains(key));
                            newParameter.example(temSchema.getExample());
                            newParameter.schema(temSchema);

                            parameter.add(newParameter);
                        });

        return parameter;
    }

    /**
     * 重写response
     *
     * @param operation
     * @param methodInfo
     */
    private void rewriteResponses(Operation operation, MethodInfo methodInfo) {
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
     * @param validationGroupNames
     * @param refName
     * @return 返回设置后的schema；没有对应的group，返回null
     */
    private Schema requestSchema(
            Set<String> methodGroupNames, Set<String> validationGroupNames, String refName) {
        if (!StringUtils.hasText(refName)) {
            return null;
        }

        try {
            String refClassName = refMapping.get(refName);

            Class clz = Class.forName(refClassName);
            ResolvedSchema resolvedSchema = createSchema(clz);

            return this.formatSchema(
                    "request", methodGroupNames, validationGroupNames, resolvedSchema);
        } catch (Exception e) {
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

        return this.formatSchema("response", methodGroupNames, null, resolvedSchema);
    }

    /**
     * 根据group信息，格式化schema
     *
     * @param type request、response
     * @param methodGroupNames
     * @param validationGroupNames
     * @param resolvedSchema new schema
     * @return 返回设置后的schema；没有对应的group，返回null
     */
    private Schema formatSchema(
            String type,
            Set<String> methodGroupNames,
            Set<String> validationGroupNames,
            ResolvedSchema resolvedSchema) {
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

            // 当前bean适配@NotNull的字段
            Set<String> notNullFields =
                    this.setSchemaNotNullRequired(schema, schemaClassName, validationGroupNames);

            // 格式化schema字段信息
            Set<String> formatGroupNames =
                    this.formatSchemaField(
                            schema, schemaClassName, type, methodGroupNames, notNullFields);

            // 跳过：无任何格式化
            if (notNullFields == null && formatGroupNames.size() == 0) {
                continue;
            }

            if (formatGroupNames.size() == 0) {
                formatGroupNames.add("@NotNull");
            }

            // 将新schema添加到文档中，并重命名
            schema.setName(
                    type + " ->" + schema.getName() + " -> " + String.join(" ,", formatGroupNames));
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
                        String schemaBeanName = AnalysisSchema.refName2BeanName(propertySchemaRef);
                        Schema propertyNewSchema = newSchemas.get(schemaBeanName);

                        if (propertyNewSchema != null
                                && !"json".equals(propertyNewSchema.getFormat())) {
                            propertyNewSchema = this.mergeMainSchema(propertyNewSchema, newSchemas);

                            properties.put(propertyName, propertyNewSchema);
                        }
                    }
                });

        mainSchema.setProperties(properties);
        return mainSchema;
    }

    /**
     * 设置schema对类中，带有@NotNull注解的字段（包含没有group和group与当前方法一致）
     *
     * @param schema
     * @param schemaClassName
     * @param validationGroupNames
     * @return null：类中不带任何@NotNull注解
     */
    private Set<String> setSchemaNotNullRequired(
            Schema schema, String schemaClassName, Set<String> validationGroupNames) {
        Map<String, Set<String>> notNullGroups =
                this.docInfo.getClass2NotNullMapping().get(schemaClassName);

        if (notNullGroups != null && notNullGroups.size() > 0) {
            Set<String> requiredFieldNames = new HashSet<>();

            // 不带group的@NotNull
            Set<String> allFields = notNullGroups.get(DocInfo.NOT_NULL_GROUP_ALL);
            if (allFields != null) {
                requiredFieldNames.addAll(allFields);
            }

            // 当前方法group对应@NotNull的group
            if (validationGroupNames != null) {
                validationGroupNames.stream()
                        .forEach(
                                validationGroupName -> {
                                    Set<String> groupRequiredFieldNames =
                                            notNullGroups.get(validationGroupName);
                                    if (groupRequiredFieldNames != null) {
                                        requiredFieldNames.addAll(groupRequiredFieldNames);
                                    }
                                });
            }

            // @NotNull与schema中自带required一致，则不处理
            List<String> schemaRequired = schema.getRequired();
            if (schemaRequired != null && schemaRequired.equals(requiredFieldNames)) {
                return null;
            }

            schema.setRequired(requiredFieldNames.stream().toList());
            return requiredFieldNames;
        }

        return null;
    }

    /**
     * 格式化schema字段
     *
     * @param schema
     * @param schemaClassName
     * @param type
     * @param methodGroupNames
     * @param notNullFields
     * @return 格式化的field group name。null：未格式化任何信息
     */
    private Set<String> formatSchemaField(
            Schema schema,
            String schemaClassName,
            String type,
            Set<String> methodGroupNames,
            Set<String> notNullFields) {
        Set<String> formatGroupNames = new HashSet<>();

        // schema对应的group字段信息
        List<FieldInfo> groupFieldInfos =
                this.getGroupFieldInfos(schemaClassName, methodGroupNames);

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
                    this.setSchemaFiled(
                            schema, fields, requiredFields, exceptionFields, notNullFields);

                    formatGroupNames.add(fieldInfo.getFileGroupName());
                });
        return formatGroupNames;
    }

    /**
     * 根据group对应的字段，设置schema字段信息
     *
     * @param schema
     * @param fields 需要显示的字段
     * @param requiredFields 必填的字段
     * @param exceptionFields 不显示的字段
     * @param notNullFields 不显示的字段
     */
    private void setSchemaFiled(
            Schema schema,
            Set<String> fields,
            Set<String> requiredFields,
            Set<String> exceptionFields,
            Set<String> notNullFields) {

        // 没有对应的group，使用原文档schema
        if (fields.isEmpty() && requiredFields.isEmpty() && exceptionFields.isEmpty()) {
            return;
        }

        Iterator<String> iterator = schema.getProperties().keySet().iterator();

        // 移除字段
        while (iterator.hasNext()) {
            String key = iterator.next();

            // @notNull。如果设置了@notNull或@notNull(group=当前方法的group)，设置成必填项
            if (notNullFields != null && notNullFields.size() > 0) {
                boolean isNotNull = notNullFields.contains(key);

                if (isNotNull) {
                    requiredFields.add(key);
                }
            }

            // 是否删除字段 = 非必填 && 不显示
            boolean isRemove =
                    (exceptionFields.contains(key) || (fields.size() > 0 && fields.contains(key)));

            if (isRemove) {
                iterator.remove();
                requiredFields.remove(key);
            }
        }

        // 设置必填字段
        schema.setRequired(requiredFields.stream().toList());
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
