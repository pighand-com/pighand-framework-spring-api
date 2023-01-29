package com.pighand.framework.spring.api.annotation;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

/**
 * RESTFUL controller
 *
 * @author wangshuli
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Tag(name = "")
@RequestMapping
@org.springframework.web.bind.annotation.RestController
public @interface RestController {

    @AliasFor("path")
    String[] value() default {};

    @AliasFor("value")
    String[] path() default {};

    @AliasFor(annotation = Tag.class, attribute = "name")
    String docName() default "";

    @AliasFor(annotation = Tag.class, attribute = "description")
    String docDescription() default "";

    @AliasFor(annotation = Tag.class, attribute = "externalDocs")
    ExternalDocumentation docExternalDocs() default @ExternalDocumentation;

    @AliasFor(annotation = Tag.class, attribute = "extensions")
    Extension[] docExtensions() default {};
}
