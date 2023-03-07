package com.pighand.framework.spring.api;

import lombok.Data;
import lombok.Getter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

/**
 * 配置类
 *
 * @author wangshuli
 */
@Component
@ServletComponentScan
@ConfigurationProperties(prefix = PighandFrameworkConfig.PIGHAND_PREFIX)
public class PighandFrameworkConfig {
    public static final String PIGHAND_PREFIX = "pighand";

    /** api配置 */
    @Getter public static ApiConfig api = new ApiConfig();

    @Data
    public static class ApiConfig {
        /** 是否开启自动添加分页参数 */
        private boolean enableAddPageParams = true;

        /** 分页参数是否必填 */
        private boolean pageParamsRequired = true;

        /** 分页或下页token */
        private String pageOrNextRegex = ".*(?i)query$";

        /** 分页 */
        private String pageRegex = ".*(?i)page$";

        /** 下页token */
        private String pageNextRegex = ".*(?i)pageNext$";
    }

    public void setException(ApiConfig exception) {
        PighandFrameworkConfig.api = exception;
    }
}
