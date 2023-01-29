package com.pighand.framework.spring.api.annotation;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * spring-doc operation
 *
 * @author wangshuli
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation
@Inherited
public @interface DocOperation {

    @AliasFor(annotation = Operation.class, attribute = "method")
    String docMethod() default "";

    @AliasFor(annotation = Operation.class, attribute = "tags")
    String[] docTags() default {};

    @AliasFor(annotation = Operation.class, attribute = "summary")
    String docSummary() default "";

    @AliasFor(annotation = Operation.class, attribute = "description")
    String docDescription() default "";

    @AliasFor(annotation = Operation.class, attribute = "requestBody")
    RequestBody docRequestBody() default @RequestBody;

    @AliasFor(annotation = Operation.class, attribute = "externalDocs")
    ExternalDocumentation docExternalDocs() default @ExternalDocumentation;

    @AliasFor(annotation = Operation.class, attribute = "operationId")
    String docOperationId() default "";

    @AliasFor(annotation = Operation.class, attribute = "parameters")
    Parameter[] docParameters() default {};

    @AliasFor(annotation = Operation.class, attribute = "responses")
    ApiResponse[] docResponses() default {};

    @AliasFor(annotation = Operation.class, attribute = "deprecated")
    boolean docDeprecated() default false;

    @AliasFor(annotation = Operation.class, attribute = "security")
    SecurityRequirement[] docSecurity() default {};

    @AliasFor(annotation = Operation.class, attribute = "servers")
    Server[] docServers() default {};

    @AliasFor(annotation = Operation.class, attribute = "extensions")
    Extension[] docExtensions() default {};

    @AliasFor(annotation = Operation.class, attribute = "hidden")
    boolean docHidden() default false;

    @AliasFor(annotation = Operation.class, attribute = "ignoreJsonView")
    boolean docIgnoreJsonView() default false;
}
