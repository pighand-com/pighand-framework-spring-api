package com.pighand.framework.spring.api.springdoc.analysis.info;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author wangshuli
 */
@Data
public class DocInfo {

    /**
     * @NotNull不设置group，添加到所有接口文档中
     */
    public static final String NOT_NULL_GROUP_ALL = "ALL";

    /** {url: MethodInfo} */
    private Map<String, MethodInfo> url2MethodMapping = new HashMap<>();

    /** {className: {fileGroupName, FieldInfo}} */
    private Map<String, Map<String, FieldInfo>> class2FieldMapping = new HashMap<>();

    /** {className: {notNullGroupName, Set<fileName>}} */
    private Map<String, Map<String, Set<String>>> class2NotNullMapping = new HashMap<>();

    public void setUrl2MethodMapping(String url, MethodInfo methodInfo) {
        this.url2MethodMapping.put(url, methodInfo);
    }

    public void setClass2FieldMapping(String className, String groupName, FieldInfo fieldInfo) {
        Map<String, FieldInfo> groupMap =
                Optional.ofNullable(this.getClass2FieldMapping().get(className))
                        .orElse(new HashMap<>(0));

        groupMap.put(groupName, fieldInfo);
        this.class2FieldMapping.put(className, groupMap);
    }
}
