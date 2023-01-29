package com.pighand.framework.spring.api.springdoc;

/**
 * @author wangshuli
 */
public class DocFieldGroupUrl {

    /**
     * @param method http method
     * @param url api router
     * @return {method}.{url}
     */
    static String url(String method, String url) {
        return String.format("%s.%s", method.toUpperCase(), url.toLowerCase());
    }
}
