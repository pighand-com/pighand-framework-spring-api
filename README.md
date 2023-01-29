# pighand-framework-spring-api

restful api相关功能支持。

更多功能查看[pighand-framework-spring](https://github.com/pighand-com/pighand-framework-spring)

### 快速开始

#### 安装

##### MAVEN

```xml

<dependencies>
    <dependency>
        <groupId>com.pighand</groupId>
        <artifactId>pighand-framework-spring-api</artifactId>
        <version>1.1.0</version>
    </dependency>
</dependencies>
```

##### GRADLE

```
dependencies {
        compile 'com.pighand:pighand-framework-spring-api:1.1.0'
}
```

##### parent

使用[pighand-framework-spring-parent](https://github.com/pighand-com/pighand-framework-spring-parent)，不需要引用spring
boot、pighand-framework相关包。

```xml

<parent>
    <groupId>com.pighand</groupId>
    <artifactId>pighand-framework-spring-parent</artifactId>
    <version>1.1.0</version>
</parent>
```

### 注解

#### @RestController

合并@RequestMapping、@RestController、@Tag(springdoc)

| 属性              | 类型                    | 描述                   |
|-----------------|-----------------------|----------------------|
| path            | String[]              | RestController.path  |
| value           | String[]              | RestController.value |
| docName         | String                | Tag.name             |
| docDescription  | String                | Tag.description      |
| docExternalDocs | ExternalDocumentation | Tag.externalDocs     |
| docExtensions   | Extension[]           | Tag.extensions       |

#### @Get、@Put、@Post、@Delete、@Patch

合并@DocOperation、@RequestMapping

| 属性                | 类型                    | 描述                          |
|-------------------|-----------------------|-----------------------------|
| name              | String                | RequestMapping.name         |
| value             | String[]              | RequestMapping.value        |
| path              | String[]              | RequestMapping.path         |
| params            | String[]              | RequestMapping.params       |
| headers           | String[]              | RequestMapping.headers      |
| consumes          | String[]              | RequestMapping.consumes     |
| produces          | String[]              | RequestMapping.produces     |
| docMethod         | String                | DocOperation.method         |
| docTags           | String[]              | DocOperation.tags           |
| docSummary        | String                | DocOperation.summary        |
| docDescription    | String                | DocOperation.description    |
| docRequestBody    | RequestBody           | DocOperation.requestBody    |
| docExternalDocs   | ExternalDocumentation | DocOperation.externalDocs   |
| docOperationId    | String                | DocOperation.operationId    |
| docParameters     | Parameter[]           | DocOperation.parameters     |
| docResponses      | ApiResponse[]         | DocOperation.responses      |
| docDeprecated     | boolean               | DocOperation.deprecated     |
| docSecurity       | SecurityRequirement[] | DocOperation.security       |
| docServers        | Server[]              | DocOperation.servers        |
| docExtensions     | Extension[]           | DocOperation.extensions     |
| docHidden         | boolean               | DocOperation.hidden         |
| docIgnoreJsonView | boolean               | DocOperation.ignoreJsonView |

### springdoc bean中字段支持分组

1. 在Application中启用：

```
 @Bean
 public PropertyCustomizer propertyCustomizer() {
     return (schema, annotatedType) -> SpringDocProperty.analysis(schema, annotatedType);
 }
```

2. bean中字段上使用分组注解：

| 注解                      | 作用域                         | 描述    |
|-------------------------|-----------------------------|-------|
| @Field                  | request bean, response bean | 显示此字段 |
| @FieldException         | request bean, response bean | 排除此字段 |
| @RequestField           | request bean                | 显示此字段 |
| @RequestFieldException  | request bean                | 排除此字段 | 
| @ResponseField          | response bean               | 显示此字段 | 
| @ResponseFieldException | response bean               | 排除此字段 | 

* 支持多注解
* @Field, @RequestField 支持required参数，标记是否必填

3. 在controller的注解中，增加分组属性：
   fieldGroup="XXX"

支持@Get、@Put、@Post、@Delete、@Patch注解

举例：

```
@RestController(path = "user") public class ConfigEmailController {

    @Post(docDescription = "创建", fieldGroup = "userCreate")
    public Result<UserVO> create(@RequestBody UserVO userVO) {
        UserVO result = service.create(userVO);
        return new Result(result);
    }

}

@Data public class userVO {

    @Schema(description = "昵称") 
    @RequestField("userCreate", required = true) 
    @RequestField("userUpdate") 
    private String name;

    @Schema(description = "账号") 
    @ResponseField("userCreate") 
    private String account;

}
```
