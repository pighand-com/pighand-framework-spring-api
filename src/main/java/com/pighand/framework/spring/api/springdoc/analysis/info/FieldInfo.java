package com.pighand.framework.spring.api.springdoc.analysis.info;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 字段信息，保存每个group下的字段信息
 *
 * @author wangshuli
 */
@Data
public class FieldInfo {

    private String fileGroupName;
    private Set<String> requestFields = new HashSet<>();
    private Set<String> requestRequiredFields = new HashSet<>();
    private Set<String> requestExceptionFields = new HashSet<>();
    private Set<String> responseFields = new HashSet<>();
    private Set<String> responseRequiredFields = new HashSet<>();
    private Set<String> responseExceptionFields = new HashSet<>();

    /** {fieldName: notNull group name} */
    private Map<String, Set<String>> requestNotNullGroupNames = new HashMap<>();
}
