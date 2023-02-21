package com.pighand.framework.spring.api.springdoc.analysis.info;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * SpringDocProperty、SpringDocRouterOperation组装doc信息，提供给SpringDocOpenAPI使用
 *
 * @author wangshuli
 */
public class SpringDocInfo {
    public static DocInfo docInfo = new DocInfo();

    public static Map<String, String> refMapping = new HashMap<>();

    /** 记录已处理schema的bean，后续不在处理 */
    public static Set<String> analysisFinishedBeans = new HashSet<>();
}
