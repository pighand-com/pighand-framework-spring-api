package com.pighand.framework.spring.api.springdoc.analysis.info;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author wangshuli
 */
@Data
public class DocInfo {

    /** {url: MethodInfo} */
    private Map<String, MethodInfo> url2MethodMapping = new HashMap<>();

    /** {className: {fileGroupName, FieldInfo}} */
    private Map<String, Map<String, FieldInfo>> class2FieldMapping = new HashMap<>();

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
