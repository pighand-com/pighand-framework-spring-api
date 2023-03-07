package com.pighand.framework.spring.api.springdoc.pageParams;

import com.pighand.framework.spring.api.PighandFrameworkConfig;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.parameters.Parameter;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 根据方法名，自动增加分页参数
 *
 * @author wangshuli
 */
public class AddPageParams {

    private Map<ParamsType, String> addMapping =
            Map.of(
                    ParamsType.PAGE_OR_NEXT,
                    PighandFrameworkConfig.api.getPageOrNextRegex(),
                    ParamsType.PAGE,
                    PighandFrameworkConfig.api.getPageRegex(),
                    ParamsType.PAGE_NEXT,
                    PighandFrameworkConfig.api.getPageNextRegex());

    public AddPageParams(OpenAPI openApi) {
        if (!PighandFrameworkConfig.api.isEnableAddPageParams()) {
            return;
        }

        openApi.getPaths().values().stream()
                .flatMap(pathItem -> pathItem.readOperations().stream())
                .forEach(
                        operation -> {
                            List<Parameter> params = operation.getParameters();

                            // 相同的方法名，swagger会重命名为"方法名_数字"，替换"_数字"，以匹配正确的方法名
                            String methodName =
                                    operation.getOperationId().replaceAll("(.*)_\\d+", "$1");

                            addMapping.forEach(
                                    (key, value) -> this.addParams(methodName, params, key, value));
                        });
    }

    /**
     * 根据类型匹配对应方法名，添加分页参数
     *
     * <p>ParamsType.PAGE_OR_NEXT -> pageSize、pageCurrent、pageToken
     *
     * <p>ParamsType.PAGE -> pageSize、pageCurrent
     *
     * <p>ParamsType.PAGE_NEXT -> pageToken
     *
     * @param methodName
     * @param params
     * @param type
     * @param regex
     */
    private void addParams(
            String methodName, List<Parameter> params, ParamsType type, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(methodName);

        if (!matcher.find()) {
            return;
        }

        switch (type) {
            case PAGE:
                this.addPage(params);
                break;
            case PAGE_NEXT:
                this.addPageNext(params);
                break;
            default:
                this.addPage(params);
                this.addPageNext(params);
                break;
        }
    }

    private void addPage(List<Parameter> params) {
        params.add(
                new Parameter()
                        .in("query")
                        .name("pageSize")
                        .description("分页：每页数据量")
                        .required(PighandFrameworkConfig.api.isPageParamsRequired()));

        params.add(
                new Parameter()
                        .in("query")
                        .name("pageCurrent")
                        .description("分页：页数")
                        .required(PighandFrameworkConfig.api.isPageParamsRequired()));
    }

    private void addPageNext(List<Parameter> params) {
        params.add(
                new Parameter()
                        .in("query")
                        .name("pageToken")
                        .description("分页：下页数据token")
                        .required(PighandFrameworkConfig.api.isPageParamsRequired()));
    }
}
