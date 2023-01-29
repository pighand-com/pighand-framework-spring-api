package com.pighand.framework.spring.api.annotation;

import com.pighand.framework.spring.api.annotation.field.FieldGroup;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * RESTFUL delete
 *
 * @author wangshuli
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DocOperation
@FieldGroup
@RequestMapping(method = {RequestMethod.DELETE})
public @interface Delete {
    @AliasFor(annotation = RequestMapping.class)
    String name() default "";

    @AliasFor(annotation = RequestMapping.class)
    String[] value() default {};

    @AliasFor(annotation = RequestMapping.class)
    String[] path() default {};

    @AliasFor(annotation = RequestMapping.class)
    String[] params() default {};

    @AliasFor(annotation = RequestMapping.class)
    String[] headers() default {};

    @AliasFor(annotation = RequestMapping.class)
    String[] consumes() default {};

    @AliasFor(annotation = RequestMapping.class)
    String[] produces() default {};

    @AliasFor(annotation = DocOperation.class)
    String docMethod() default "";

    @AliasFor(annotation = DocOperation.class)
    String[] docTags() default {};

    @AliasFor(annotation = DocOperation.class)
    String docSummary() default "";

    @AliasFor(annotation = DocOperation.class)
    String docDescription() default "";

    @AliasFor(annotation = DocOperation.class)
    RequestBody docRequestBody() default @RequestBody;

    @AliasFor(annotation = DocOperation.class)
    ExternalDocumentation docExternalDocs() default @ExternalDocumentation;

    @AliasFor(annotation = DocOperation.class)
    String docOperationId() default "";

    @AliasFor(annotation = DocOperation.class)
    Parameter[] docParameters() default {};

    @AliasFor(annotation = DocOperation.class)
    ApiResponse[] docResponses() default {};

    @AliasFor(annotation = DocOperation.class)
    boolean docDeprecated() default false;

    @AliasFor(annotation = DocOperation.class)
    SecurityRequirement[] docSecurity() default {};

    @AliasFor(annotation = DocOperation.class)
    Server[] docServers() default {};

    @AliasFor(annotation = DocOperation.class)
    Extension[] docExtensions() default {};

    @AliasFor(annotation = DocOperation.class)
    boolean docHidden() default false;

    @AliasFor(annotation = DocOperation.class)
    boolean docIgnoreJsonView() default false;

    @AliasFor(annotation = FieldGroup.class, attribute = "value")
    String fieldGroup() default "";
}
