package com.pighand.framework.spring.api.springdoc.analysis;

import com.pighand.framework.spring.api.annotation.*;
import com.pighand.framework.spring.api.annotation.field.*;
import com.pighand.framework.spring.api.springdoc.analysis.info.FieldGroupType;
import com.pighand.framework.spring.api.springdoc.analysis.info.FieldInfo;
import com.pighand.framework.spring.api.springdoc.analysis.info.MethodInfo;
import com.pighand.framework.spring.api.springdoc.analysis.info.SpringDocInfo;
import com.pighand.framework.spring.api.springdoc.utils.DocFieldGroupUrl;
import org.springdoc.core.fn.RouterOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 解析method，转换成 { method.url : MethodInfo }
 *
 * @author wangshuli
 */
public class SpringDocRouterOperation {

    /**
     * 解析method
     *
     * @param routerOperation
     * @param handlerMethod
     * @return
     */
    public RouterOperation analysis(RouterOperation routerOperation, HandlerMethod handlerMethod) {
        // http url
        String path = routerOperation.getPath();
        // url对应的method
        RequestMethod[] requestMethods = routerOperation.getMethods();

        // 解析method，组装MethodInfo
        Method method = handlerMethod.getMethod();
        MethodInfo methodInfo = analysisMethod(method);

        for (RequestMethod requestMethod : requestMethods) {
            String url = DocFieldGroupUrl.url(requestMethod.name(), path);
            SpringDocInfo.docInfo.setUrl2MethodMapping(url, methodInfo);
        }

        return routerOperation;
    }

    /**
     * 解析method，组装MethodInfo
     *
     * @param method
     * @return MethodInfo
     */
    private MethodInfo analysisMethod(Method method) {
        String methodName = method.getName();
        Type returnType = method.getGenericReturnType();
        Parameter[] parameters = method.getParameters();

        // 解析response bean
        this.analysisBean(returnType.getTypeName());

        // 解析request bean
        for (Parameter parameter : parameters) {
            this.analysisBean(parameter.getType().getName());
        }

        Class cls = method.getDeclaringClass();
        String className = cls.getName();
        String packageName = cls.getPackage().getName();

        // 获取method的filedGroupName
        Set<String> methodFieldGroupNames =
                analysisMethodFieldGroupNames(
                        packageName, className, methodName, method.getAnnotations());

        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setCls(cls);
        methodInfo.setMethod(method);
        methodInfo.setReturnType(returnType);
        methodInfo.setParameters(new ArrayList<>(Arrays.asList(parameters)));
        methodInfo.setMethodFieldGroupNames(methodFieldGroupNames);

        return methodInfo;
    }

    /**
     * 获取method的filedGroupName
     *
     * @param packageName
     * @param className
     * @param methodName
     * @param annotations
     * @return [ 用户自定义的filedGroupName(注解中设置fieldGroup)， className+methodName，
     *     packageName+className+methodName ]
     */
    private Set<String> analysisMethodFieldGroupNames(
            String packageName, String className, String methodName, Annotation[] annotations) {
        Set<String> methodFieldGroupNames = new HashSet<>();

        // {className}.{methodName}
        methodFieldGroupNames.add(String.format("%s.%s", className, methodName));

        // {packageName}.{className}.{methodName}
        methodFieldGroupNames.add(
                String.format(
                        "%s.%s", className.replace(packageName, "").replace(".", ""), methodName));

        // 注解中fieldGroup
        for (Annotation annotation : annotations) {
            String annotationFieldGroupName = "";
            if (annotation instanceof Post) {
                annotationFieldGroupName = ((Post) annotation).fieldGroup();
            } else if (annotation instanceof Put) {
                annotationFieldGroupName = ((Put) annotation).fieldGroup();
            } else if (Delete.class.equals(annotation)) {
                annotationFieldGroupName = ((Delete) annotation).fieldGroup();
            } else if (annotation instanceof Get) {
                annotationFieldGroupName = ((Get) annotation).fieldGroup();
            } else if (annotation instanceof Patch) {
                annotationFieldGroupName = ((Patch) annotation).fieldGroup();
            } else if (annotation instanceof FieldGroup) {
                annotationFieldGroupName = ((FieldGroup) annotation).value();
            }

            if (StringUtils.hasText(annotationFieldGroupName)) {
                methodFieldGroupNames.add(annotationFieldGroupName);
            }
        }

        return methodFieldGroupNames;
    }

    /**
     * 解析 bean
     *
     * @param className
     */
    private void analysisBean(String className) {
        try {
            if (!className.startsWith("java.lang")
                    && !SpringDocInfo.analysisFinishedBeans.contains(className)) {

                SpringDocInfo.analysisFinishedBeans.add(className);

                Class cls = Class.forName(className);

                List<java.lang.reflect.Field> fields =
                        new ArrayList<>(Arrays.asList(cls.getDeclaredFields()));

                // 获取父类字段
                Class tmpSuperCls = cls.getSuperclass();
                while (tmpSuperCls != null) {
                    fields.addAll(Arrays.asList(tmpSuperCls.getDeclaredFields()));

                    tmpSuperCls = tmpSuperCls.getSuperclass();
                }

                // 组装分组信息
                fields.forEach(
                        field -> {
                            String fieldName = field.getName();

                            for (Annotation annotationObject : field.getAnnotations()) {
                                this.analysisFiledGroupNames(
                                        className, fieldName, annotationObject);
                            }
                        });
            }
        } catch (Exception e) {

        }
    }

    /**
     * 根据字段上的注解，组装分组信息
     *
     * @param className
     * @param fieldName
     * @param annotation
     */
    private void analysisFiledGroupNames(
            String className, String fieldName, Annotation annotation) {
        if (annotation instanceof Field) {
            // @Field
            String[] groupNames = ((Field) annotation).value();
            boolean required = ((Field) annotation).required();

            setClass2FieldMapping(
                    FieldGroupType.REQUEST, className, fieldName, groupNames, required);

            setClass2FieldMapping(
                    FieldGroupType.RESPONSE, className, fieldName, groupNames, required);
        } else if (annotation instanceof RequestField) {
            // @RequestField
            String[] groupNames = ((RequestField) annotation).value();
            boolean required = ((RequestField) annotation).required();

            setClass2FieldMapping(
                    FieldGroupType.REQUEST, className, fieldName, groupNames, required);
        } else if (annotation instanceof ResponseField) {
            // @ResponseField
            String[] groupNames = ((ResponseField) annotation).value();
            boolean required = ((ResponseField) annotation).required();

            setClass2FieldMapping(
                    FieldGroupType.RESPONSE, className, fieldName, groupNames, required);
        } else if (annotation instanceof FieldException) {
            // @FieldException
            String[] groupNames = ((FieldException) annotation).value();

            setClass2FieldMapping(
                    FieldGroupType.REQUEST_EXCEPTION, className, fieldName, groupNames, false);
            setClass2FieldMapping(
                    FieldGroupType.RESPONSE_EXCEPTION, className, fieldName, groupNames, false);
        } else if (annotation instanceof RequestFieldException) {
            // @RequestFieldException
            String[] groupNames = ((RequestFieldException) annotation).value();

            setClass2FieldMapping(
                    FieldGroupType.REQUEST_EXCEPTION, className, fieldName, groupNames, false);
        } else if (annotation instanceof ResponseFieldException) {
            // @ResponseFieldException
            String[] groupNames = ((ResponseFieldException) annotation).value();

            setClass2FieldMapping(
                    FieldGroupType.RESPONSE_EXCEPTION, className, fieldName, groupNames, false);
        } else if (annotation instanceof Fields) {
            // @Fields
            for (Field field : ((Fields) annotation).value()) {
                analysisFiledGroupNames(className, fieldName, field);
            }
        } else if (annotation instanceof RequestFields) {
            // @RequestFields
            for (RequestField field : ((RequestFields) annotation).value()) {
                analysisFiledGroupNames(className, fieldName, field);
            }
        } else if (annotation instanceof ResponseFields) {
            // @ResponseFields
            for (ResponseField field : ((ResponseFields) annotation).value()) {
                analysisFiledGroupNames(className, fieldName, field);
            }
        } else if (annotation instanceof FieldExceptions) {
            // @FieldExceptions
            for (FieldException field : ((FieldExceptions) annotation).value()) {
                analysisFiledGroupNames(className, fieldName, field);
            }
        } else if (annotation instanceof RequestFieldExceptions) {
            // @RequestFieldExceptions
            for (RequestFieldException field : ((RequestFieldExceptions) annotation).value()) {
                analysisFiledGroupNames(className, fieldName, field);
            }
        } else if (annotation instanceof ResponseFieldExceptions) {
            // @ResponseFieldExceptions
            for (ResponseFieldException field : ((ResponseFieldExceptions) annotation).value()) {
                analysisFiledGroupNames(className, fieldName, field);
            }
        }
    }

    /**
     * 组装分组信息
     *
     * <p>{className: {fileGroupName, FieldInfo}}
     *
     * @param type FieldGroupType
     * @param className
     * @param fieldName
     * @param groupNames
     * @param isRequired
     */
    private void setClass2FieldMapping(
            FieldGroupType type,
            String className,
            String fieldName,
            String[] groupNames,
            boolean isRequired) {

        // {fileGroupName, FieldInfo}
        Map<String, FieldInfo> groupMap =
                Optional.ofNullable(SpringDocInfo.docInfo.getClass2FieldMapping().get(className))
                        .orElse(new HashMap<>(0));

        if (groupNames == null || groupNames.length == 0) {
            // 未设置group name，添加至所有group
            groupMap.forEach(
                    (fileGroupName, fieldInfo) ->
                            this.addToFieldInfo(type, fieldInfo, fieldName, isRequired));
        } else {
            // 根据group name添加
            for (String groupName : groupNames) {
                FieldInfo fieldInfo =
                        Optional.ofNullable(groupMap.get(groupName)).orElse(new FieldInfo());
                fieldInfo.setFileGroupName(groupName);

                this.addToFieldInfo(type, fieldInfo, fieldName, isRequired);

                SpringDocInfo.docInfo.setClass2FieldMapping(className, groupName, fieldInfo);
            }
        }
    }

    /**
     * 添加字段信息
     *
     * @param type
     * @param fieldInfo
     * @param fieldName
     * @param isRequired
     */
    private void addToFieldInfo(
            FieldGroupType type, FieldInfo fieldInfo, String fieldName, boolean isRequired) {
        switch (type) {
            case REQUEST -> {
                fieldInfo.getRequestFields().add(fieldName);
                if (isRequired) {
                    fieldInfo.getRequestRequiredFields().add(fieldName);
                }
            }
            case RESPONSE -> {
                fieldInfo.getResponseFields().add(fieldName);
                if (isRequired) {
                    fieldInfo.getResponseRequiredFields().add(fieldName);
                }
            }
            case REQUEST_EXCEPTION -> fieldInfo.getRequestExceptionFields().add(fieldName);
            case RESPONSE_EXCEPTION -> fieldInfo.getResponseExceptionFields().add(fieldName);
        }
    }
}
