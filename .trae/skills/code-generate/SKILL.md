---
name: "code-generate"
description: "根据用户需求生成Spring Cloud微服务各层代码（Controller/Service/DAO/Entity/Req/Resp）。当用户请求创建新的微服务组件、增删改查接口或需要生成完整业务模块代码时使用。"
---

# Code Generate - 微服务代码生成器

本技能用于为Spring Cloud微服务项目生成完整的各层代码（Controller、Service、DAO、Entity、Req、Resp）。

## 适用场景

- 用户请求创建新的微服务组件
- 需要生成增删改查（CRUD）接口
- 需要创建完整的业务模块代码
- 需要生成API请求/响应对象

## 使用方法

### 第一步：收集需求信息

需要向用户确认以下信息：

1. **模块名称**：需要创建的实体/业务模块名称（如：User、Order、Product等）
2. **模块类型**：确定所属的微服务模块（如：user、order、tool、system等）
3. **需要生成的组件**（可多选）：
   - Controller（控制器）
   - Service（服务层）
   - DAO/Mapper（数据访问层）
   - Entity（实体类）
   - Req（请求对象）
   - Resp（响应对象）
4. **是否需要登录认证**：接口是否需要 @RequireLogin 注解

### 第二步：分析现有代码结构

根据用户选择的模块类型，参考现有代码的风格：

- **Entity**：使用Lombok @Data @Accessors(chain = true)，配合MyBatis-Plus @TableName
- **Mapper**：继承 BaseMapper<T> 接口
- **Service**：使用 @Component 注解，包含@Autowired注入Mapper
- **Controller**：使用 @Validated @RestController @RequestMapping，方法参数使用@Valid @RequestBody
- **Req/Resp**：使用Lombok，Req包含@NotBlank等校验注解

### 第三步：生成代码

根据以下模板生成各层代码：

#### Entity模板

```java
package com.company.{module}.entity;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("{table_name}")
public class {EntityName} {
    private Long id;
    private Integer deleted;
    private String creator;
    private LocalDateTime createTime;
    private String updater;
    private LocalDateTime updateTime;
    // 添加业务字段...
}
```

#### Mapper模板

```java
package com.company.{module}.mapper.{module};

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.{module}.entity.{EntityName};

public interface {EntityName}Mapper extends BaseMapper<{EntityName}> {
}
```

#### Service模板

```java
package com.company.{module}.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.company.{module}.mapper.{module}.{EntityName}Mapper;
import com.company.{module}.entity.{EntityName};

@Component
public class {EntityName}Service {

    @Autowired
    private {EntityName}Mapper {entityName}Mapper;

    public {EntityName} getById(Long id) {
        return {entityName}Mapper.selectById(id);
    }

    public void save({EntityName} {entityName}) {
        {entityName}Mapper.insert({entityName});
    }

    public void update({EntityName} {entityName}) {
        {entityName}Mapper.updateById({entityName});
    }

    public void deleteById(Long id) {
        {entityName}Mapper.deleteById(id);
    }
}
```

#### Controller模板

```java
package com.company.{module}.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.company.{module}.entity.{EntityName};
import com.company.{module}.service.{EntityName}Service;
import com.company.{module}.req.{EntityName}Req;
import com.company.{module}.resp.{EntityName}Resp;
import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/{module}/{entityNameLowerCase}")
public class {EntityName}Controller {

    @Autowired
    private {EntityName}Service {entityName}Service;

    @GetMapping("/{id}")
    public {EntityName}Resp getById(@PathVariable Long id) {
        {EntityName} {entityName} = {entityName}Service.getById(id);
        // 转换为Resp...
        return null;
    }

    @PostMapping
    public void save(@Valid @RequestBody {EntityName}Req req) {
        {EntityName} {entityName} = new {EntityName}();
        // req转换为entity...
        {entityName}Service.save({entityName});
    }
}
```

#### Req模板

```java
package com.company.{module}.req;

import lombok.Data;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class {EntityName}Req {
    // 添加请求字段和校验注解...
}
```

#### Resp模板

```java
package com.company.{module}.resp;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class {EntityName}Resp {
    // 添加响应字段...
}
```

### 第四步：创建文件

1. 在对应的模块目录下创建各层代码文件
2. 确保包路径正确（参考现有代码）
3. 遵循项目代码规范

## 示例对话

用户：请帮我创建一个商品模块的代码

1. 询问：需要创建哪些组件？（Controller、Service、Mapper、Entity、Req、Resp）
2. 询问：模块名称是什么？（Product）
3. 询问：属于哪个微服务？（product）
4. 询问：需要哪些字段？（name、price、description等）

生成代码后，向用户展示创建的文件列表。

## 注意事项

- 严格遵循项目现有的代码风格
- Entity使用MyBatis-Plus注解
- Controller方法参数使用@Valid校验
- 需要登录的接口添加@RequireLogin注解
- 使用com.company.{module}.entity格式的包名
- Req使用请求参数校验注解（@NotBlank、@NotNull等）
