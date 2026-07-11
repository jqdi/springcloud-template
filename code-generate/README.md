# 代码生成模块 (code-generate)

## 简介

code-generate 是一个基于 **MyBatis Plus Generator** + **Velocity 模板引擎**的企业级代码生成模块，能够一键生成符合项目规范的微服务全栈代码，大幅提高开发效率，减少重复劳动。

本模块深度集成了项目的微服务架构规范，不仅可以生成内部微服务（service/api）的代码，还可以同时生成管理后台（adminapi）的控制层和 Excel 导入导出类。

## 功能特点

1. **全链路代码生成**：一键生成 Entity、Mapper、Service、Controller、Req、Resp、Feign、AdminApi Controller、Excel 等 9 类文件
2. **深度集成项目架构**：生成的代码完全遵循项目的微服务分层规范（service/api/adminapi）
3. **双模式操作**：支持命令行交互式生成和 REST API 调用两种方式
4. **自定义 Velocity 模板**：所有代码模板位于 `templates/` 目录，可按需定制
5. **批量生成支持**：支持多表名逗号分隔批量生成，或通过 API 批量提交
6. **默认配置**：通过 `application.yml` 配置默认模块名、表名，减少重复输入
7. **构建隔离**：`mvn install/deploy` 自动跳过本模块，不影响项目构建产物

## 生成的代码清单

生成目标模块的 `service` 子模块：
- **Entity**：实体类，基于数据库表结构自动生成
- **Mapper.java**：Mapper 接口，继承 BaseMapper
- **Mapper.xml**：Mapper XML 映射文件，输出到 `resources/mapper/`
- **Service.java**：Service 接口层（本项目约定不生成 ServiceImpl）
- **Controller.java**：内部微服务 Controller 层

生成目标模块的 `api` 子模块：
- **{Entity}Req.java**：请求参数 DTO，输出到 `api/.../request/`
- **{Entity}Resp.java**：响应结果 DTO，输出到 `api/.../response/`
- **{Entity}Feign.java**：Feign 客户端接口，输出到 `api/.../feign/`

生成 `adminapi` 管理后台模块：
- **{Entity}Controller.java**：管理后台 Controller，输出到 `adminapi/.../controller/`
- **{Entity}Excel.java**：Excel 导入导出实体类，输出到 `adminapi/.../excel/`

## 目录结构

```
code-generate/
├── src/main/java/com/company/codegenerate/
│   ├── CodeGenerateApplication.java       # 启动类
│   ├── CommandRunner.java                 # 命令行交互式 Runner
│   ├── config/
│   │   └── CodeGeneratorConfig.java       # 生成参数配置类
│   ├── controller/
│   │   └── CodeGeneratorController.java   # REST API 控制器
│   └── generator/
│       └── CodeGeneratorService.java      # 代码生成核心服务
├── src/main/resources/
│   ├── templates/                         # Velocity 代码模板（可自定义）
│   │   ├── entity.java.vm
│   │   ├── mapper.java.vm
│   │   ├── mapper.xml.vm
│   │   ├── service.java.vm
│   │   ├── controller.java.vm
│   │   ├── entityReq.java.vm
│   │   ├── entityResp.java.vm
│   │   ├── feign.java.vm
│   │   ├── adminapi-controller.java.vm
│   │   └── adminapi-entityExcel.java.vm
│   ├── application.yml                    # 主配置（含代码生成默认参数）
│   ├── application-dev.yml                # 开发环境配置
│   ├── bootstrap.yml                      # 引导配置
│   └── logback-conf.xml                   # 日志配置
├── pom.xml
└── README.md
```

## 快速开始

### 1. 配置数据库连接

编辑 `src/main/resources/application.yml` 或对应环境的配置文件，确保数据源正确指向目标数据库：

数据库连接配置位于 `boot-starter-datasource` 的 `application-datasource.yml` 中，通常无需修改，如需覆盖请在本模块的配置文件中添加：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 2. 配置代码生成默认参数

在 `application.yml` 中调整默认参数（可选，运行时也可交互输入）：

```yaml
code:
  generate:
    # 默认目标模块，支持: adminapi,admin,system,order,user,tool
    moduleName: order
    # 默认表名，多个表名用英文逗号分隔
    tableNames: sms_task,sms_task_detail
    # 生成代码中 @author 的值
    author: CodeGenerate
    # 父包名，默认与项目一致
    parentPackage: com.company
```

### 3. 运行代码生成

#### 方式一：命令行交互式（推荐）

直接运行启动类 [CodeGenerateApplication.java](file:///D:/code/me/springcloud-template/code-generate/src/main/java/com/company/codegenerate/CodeGenerateApplication.java) 的 `main` 方法：

```bash
# 进入 code-generate 目录后
mvn spring-boot:run
```

启动后控制台会进入交互式循环：

```
=================================
====欢迎使用代码生成器====
=================================
请输入模块名称 (当前值:order): user
请输入要生成代码的表名(多个表名用英文逗号分隔,当前值:sms_task): user_info,user_wallet
代码生成成功！
请输入模块名称 (当前值:user):
```

- 直接按回车可使用括号中的默认值
- 生成成功后可继续输入下一组，不会退出

#### 方式二：REST API 调用

启动应用后（默认端口由配置决定），通过 HTTP 接口生成代码：

**① 生成单表代码**

```http
POST /code/generate/{moduleName}?tableName={tableName}
```

示例：
```bash
curl -X POST "http://localhost:8080/code/generate/user?tableName=user_info"
```

**② 批量生成多表代码**

```http
POST /code/generate/{moduleName}/batch
Content-Type: application/json

["table1", "table2", "table3"]
```

示例：
```bash
curl -X POST "http://localhost:8080/code/generate/order/batch" \
  -H "Content-Type: application/json" \
  -d '["order_info","order_item","pay_record"]'
```

**③ 获取支持的模块列表**

```http
GET /code/generate/modules
```

返回：
```json
["adminapi","admin","system","order","user","tool"]
```

## 生成的代码输出路径示例（以 moduleName=order、表名=order_info 为例）

```
springcloud-template/
├── order/
│   ├── service/src/main/java/com/company/order/
│   │   ├── entity/OrderInfo.java
│   │   ├── mapper/OrderInfoMapper.java
│   │   ├── service/OrderInfoService.java
│   │   └── controller/OrderInfoController.java
│   ├── service/src/main/resources/mapper/
│   │   └── OrderInfoMapper.xml
│   └── api/src/main/java/com/company/order/api/
│       ├── request/OrderInfoReq.java
│       ├── response/OrderInfoResp.java
│       └── feign/OrderInfoFeign.java
└── adminapi/src/main/java/com/company/adminapi/
    ├── controller/OrderInfoController.java   # 管理后台专用 Controller
    └── excel/OrderInfoExcel.java             # Excel 导入导出类
```

## 支持的目标模块

| 模块名     | 代码输出位置                     | 适用场景               |
|------------|----------------------------------|------------------------|
| `order`    | `order/service` + `order/api`    | 订单中心相关表         |
| `user`     | `user/service` + `user/api`      | 用户中心相关表         |
| `system`   | `system/service` + `system/api`  | 系统基础配置相关表     |
| `tool`     | `tool/service` + `tool/api`      | 工具服务相关表         |
| `admin`    | `admin/...`                      | 后台管理相关（较少用） |
| `adminapi` | `adminapi/...`                   | 后台 API 专用          |

> 注意：无论选择哪个模块，`adminapi` 下的 Controller 和 Excel 类**始终会生成**，这是为了方便管理后台直接对接 CRUD。

## 自定义代码模板

所有模板使用 **Apache Velocity** 语法编写，位于 [templates](file:///D:/code/me/springcloud-template/code-generate/src/main/resources/templates/) 目录。

### 模板内置变量

在模板中可以使用以下变量（由 MyBatis Plus Generator 自动注入 + 自定义注入）：

| 变量名              | 含义说明                                         |
|--------------------|------------------------------------------------|
| `table`            | 表信息对象（`table.name`, `table.comment` 等）  |
| `entity`           | 实体名（首字母大写，如 `OrderInfo`）            |
| `_entity`          | 实体名（首字母小写，如 `orderInfo`）**自定义**  |
| `service`          | Service 接口名                                  |
| `_serviceName`     | Service 变量名（首字母小写）**自定义**          |
| `mapper`           | Mapper 接口名                                   |
| `controller`       | Controller 类名                                 |
| `package`          | 包名配置对象（`package.Entity`, `package.Mapper` 等） |
| `author`           | @author 值，来自配置                            |
| `apiPackage`       | API 子模块包名，如 `com.company.order.api` **自定义** |
| `adminapiPackage`  | adminapi 包名，如 `com.company.adminapi` **自定义** |
| `fields`           | 字段列表（`field.name`, `field.propertyName`, `field.comment` 等） |

### 修改模板示例

如果你想让生成的 Entity 默认加上 `@Data` 注解，编辑 [entity.java.vm](file:///D:/code/me/springcloud-template/code-generate/src/main/resources/templates/entity.java.vm)，在类定义上方添加：

```velocity
import lombok.Data;

@Data
${table.annotation}
public class ${entity} {
    ...
}
```

## 核心类说明

| 类名                                                         | 职责                                                                 |
|--------------------------------------------------------------|----------------------------------------------------------------------|
| [CodeGeneratorService.java](file:///D:/code/me/springcloud-template/code-generate/src/main/java/com/company/codegenerate/generator/CodeGeneratorService.java) | 代码生成核心服务：封装 FastAutoGenerator，配置策略、包路径、自定义文件输出 |
| [CodeGeneratorConfig.java](file:///D:/code/me/springcloud-template/code-generate/src/main/java/com/company/codegenerate/config/CodeGeneratorConfig.java) | `@ConfigurationProperties` 配置类，绑定 `code.generate.*` 前缀       |
| [CommandRunner.java](file:///D:/code/me/springcloud-template/code-generate/src/main/java/com/company/codegenerate/CommandRunner.java) | Spring Boot 启动后自动执行，提供命令行交互 Scanner 循环              |
| [CodeGeneratorController.java](file:///D:/code/me/springcloud-template/code-generate/src/main/java/com/company/codegenerate/controller/CodeGeneratorController.java) | REST API 暴露层，提供单表、批量、模块列表三个接口                    |

## 技术依赖

| 依赖                   | 版本    | 用途                 |
|------------------------|---------|----------------------|
| mybatis-plus-generator | 3.5.7   | 代码生成引擎核心     |
| velocity-engine-core   | 2.3     | Velocity 模板引擎    |
| boot-starter-datasource| 项目内  | 提供数据源自动配置   |
| template-framework     | 项目内  | 项目基础框架依赖     |

## 注意事项

1. **文件覆盖**：默认开启 `enableFileOverride()`，生成的代码会**覆盖**已有同名文件，首次使用前请务必 Git 提交或备份
2. **数据库权限**：确保连接数据库的账号拥有目标表的 `SELECT`、`SHOW COLUMNS` 等元数据读取权限
3. **ServiceImpl 约定**：本项目约定「Service 接口即 Mapper 代理」，故默认 `disableServiceImpl()`，不生成 ServiceImpl
4. **Swagger**：全局配置中默认关闭了 `enableSwagger()`，如需生成 `@ApiModel` 注解请在 CodeGeneratorService 中开启
5. **仅开发环境使用**：本模块是**开发工具**，严禁部署到生产环境；pom.xml 中已配置 `maven-install-plugin` 和 `maven-deploy-plugin` 的 `<skip>true</skip>`
6. **生成后检查**：自动生成的代码仅提供基础骨架，生成后请根据业务逻辑补充校验、注释、业务方法等
7. **表名规范**：建议使用下划线命名（如 `order_info`），工具会自动转换为驼峰 `OrderInfo`

## 常见问题

**Q1: 生成后找不到生成的文件？**
> 检查控制台日志中打印的 `service路径`、`api路径`、`adminapi路径`，确认是否输出到了预期的模块目录。路径基于 `System.getProperty("user.dir")`（即 code-generate 目录）拼接 `../` 得到项目根，再拼接各模块。

**Q2: 报错 `DataSource not available` 或连接失败？**
> 检查 `application-datasource.yml` 或当前模块的数据源配置，确认 URL/用户名/密码正确，以及数据库服务可达。

**Q3: 想新增支持的模块（如 `im`）怎么办？**
> 1. 在 `CodeGeneratorController` 的 `supportedModules` 列表中添加模块名
> 2. 确认项目根目录下存在 `im/service` 和 `im/api` 子模块结构
> 3. 如需特殊路径处理，修改 `CodeGeneratorService.generateCode()` 中的路径拼接逻辑

**Q4: 生成的 Req/Resp 只有空壳，想自动带字段？**
> 编辑 `entityReq.java.vm` / `entityResp.java.vm` 模板，遍历 `fields` 变量，按需生成字段（可参考 Entity 模板的写法）。
