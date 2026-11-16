# Spring Boot 项目框架设计文档

> 版本：v1.0　|　状态：草案　|　适用范围：基于 Java + Spring Boot 的后端服务新建项目
>
> 本文档用于统一项目的技术选型、模块划分、代码规范与工程化能力，作为团队搭建新项目时的基线参考。

---

## 目录

1. 建设目标与设计原则
2. 技术选型建议
3. 整体架构与分层
4. 模块划分（多模块）
5. 你已有设想的模块细化
6. 建议补充的模块与能力（重点）
7. 推荐目录结构
8. 依赖与版本管理
9. 开发规范约定
10. 数据库设计规范
11. 接口规范
12. 分阶段落地路线
13. 附录：快速开始 Checklist

---

## 1. 建设目标与设计原则

### 1.1 目标

- 快速启动：新业务模块能基于统一骨架快速搭建，减少重复造轮子。
- 统一规范：统一返回结构、异常体系、日志格式、代码风格、接口约定。
- 可维护 / 可扩展：模块边界清晰，业务代码与技术基础设施解耦。
- 安全可控：认证授权、数据安全、操作审计内建。
- 可观测：日志、指标、链路追踪、健康检查开箱即用。

### 1.2 原则

| 原则 | 说明 |
| --- | --- |
| 高内聚、低耦合 | 按业务边界拆分模块，模块间通过接口/API 交互 |
| 约定优于配置 | 统一默认配置，减少散落的个性化配置 |
| 分层清晰 | Controller / Service / Repository 各司其职，禁止跨层调用 |
| 面向接口编程 | 核心能力抽象成接口，便于替换实现与单元测试 |
| 防御性设计 | 参数校验、幂等、限流、超时、降级前置考虑 |
| 可观测优先 | 关键路径打点、统一 traceId、结构化日志 |

---

## 2. 技术选型建议

> 版本以“稳定 LTS / 团队熟悉度”为优先，下表为推荐基线，可按团队情况调整。

| 类别 | 推荐选型 | 说明 |
| --- | --- | --- |
| JDK | 17（LTS）或 21（LTS） | Spring Boot 3.x 要求 Java 17+ |
| 构建工具 | Maven（多模块） | 若团队更熟 Gradle 亦可，全团队统一 |
| 核心框架 | Spring Boot 3.x | 统一 parent / BOM 管理版本 |
| Web | Spring MVC | 默认即满足，WebFlux 按需 |
| 安全 | Spring Security 6 + JWT | 认证授权，支持无状态 Token |
| ORM | MyBatis-Plus（推荐）或 Spring Data JPA | MyBatis-Plus 更适合复杂 SQL 与国内团队 |
| 数据库 | MySQL 8.0 / PostgreSQL | 按场景选择，全团队统一 |
| 缓存 | Redis | 缓存、分布式锁、会话、限流 |
| 接口文档 | springdoc-openapi + Knife4j | Swagger UI 增强，支持调试 |
| 参数校验 | Hibernate Validator（Jakarta Validation） | 配合全局异常处理 |
| 对象转换 | MapStruct + Lombok | 编译期生成，性能优于反射 |
| 工具库 | Hutool / Apache Commons Lang3 | 减少自造工具类 |
| 日志 | Logback + MDC（traceId） | 可对接 ELK / Loki |
| 链路追踪 | SkyWalking / Micrometer Tracing | 微服务排障 |
| 数据库迁移 | Flyway（推荐）或 Liquibase | 版本化 Schema，告别手工改库 |
| 定时任务 | XXL-JOB（推荐）或 Quartz | 分布式调度、可视化 |
| 对象存储 | MinIO / 阿里云 OSS | 文件上传与静态资源 |
| 容器化 | Docker + docker-compose | 统一运行环境 |
| 代码质量 | Checkstyle / Alibaba P3C + SonarQube | 静态扫描 |

---

## 3. 整体架构与分层

### 3.1 分层模型

```
客户端 (Web / App / 第三方系统)
        |  HTTP/HTTPS (JWT)
        v
Controller 层   参数校验 / DTO 转换 / 接口路由
        |
        v
Service 层      业务逻辑 / 事务边界 / 编排
        |
        v
Manager / Domain 领域能力（可选，复杂业务拆分）
        |
        v
Repository/Mapper 数据访问 / 缓存 / 外部服务调用
        |
        v
DB / Redis / MQ / 第三方 API
```

### 3.2 调用约定

- 自上而下调用，禁止反向或跨层调用（如 Controller 直接调 Mapper）。
- Controller 只做参数校验 + 调用 Service + 返回结果，不写业务逻辑。
- 事务边界放在 Service 层，不在 Controller / Mapper。
- 层间数据用 DTO / VO 传递，Entity 不直接暴露到接口层。

---

## 4. 模块划分（多模块）

推荐采用 Maven 多模块，按“基础能力 / 系统能力 / 业务能力 / 启动聚合”拆分：

| 模块 | 职责 | 是否必建 |
| --- | --- | --- |
| xxx-common | 常量、枚举、统一响应、异常、工具类、通用基类、校验注解 | 必建 |
| xxx-framework | 技术基础设施配置：安全、Redis、MyBatis、跨域、切面、拦截器 | 必建 |
| xxx-system | 系统管理：用户、角色、菜单、部门、字典、参数、操作日志 | 必建（承载权限模块） |
| xxx-api | 对外接口定义、DTO、Feign Client（微服务时） | 按需 |
| xxx-module-order 等 | 各业务模块，按领域拆分 | 按需 |
| xxx-job | 定时任务、批处理 | 按需 |
| xxx-admin / xxx-app | 启动模块，聚合各模块、放启动类与全局配置 | 必建 |

依赖关系：启动模块 -> 业务模块 -> framework / system -> common；common 不依赖任何业务模块。

> 单体项目前期可先用“单模块 + 多 package”降低复杂度，但 package 结构要按上述模块边界预留，便于后续拆分。

---

## 5. 你已有设想的模块细化

### 5.1 权限认证模块（认证 + 授权）

核心概念：认证（你是谁）/ 授权（你能做什么）/ 数据权限（你能看哪些数据）。

技术路线：Spring Security 6 + JWT（无状态），权限信息从数据库加载到 UserDetails。

核心表设计（最小集）：

| 表 | 说明 |
| --- | --- |
| sys_user | 用户：用户名、密码（BCrypt 加密）、状态、部门 ID |
| sys_role | 角色 |
| sys_menu | 菜单 / 按钮 / 接口权限（type 区分） |
| sys_user_role | 用户-角色关联 |
| sys_role_menu | 角色-菜单关联 |
| sys_dept | 部门（可选，支撑数据权限） |

关键能力清单：

- [ ] 登录认证：JWT 签发、刷新、过期策略；密码 BCrypt 加密存储
- [ ] 验证码（图形/滑块）与登录失败锁定，防暴力破解
- [ ] 接口级鉴权：@PreAuthorize("hasAuthority('xxx')")
- [ ] 按钮 / 菜单级权限：前端按权限码渲染，后端接口强制校验
- [ ] 数据权限：基于部门 / 角色的行级过滤（MyBatis-Plus 数据权限插件或自定义注解 + SQL 拼装）
- [ ] 会话治理：Redis 黑名单实现登出 / 踢人下线 / 强制改密失效 Token
- [ ] 密码策略：复杂度校验、定期更换、首次登录改密
- [ ] 多租户（可选）：租户隔离字段 + 拦截器自动注入 tenantId
- [ ] 审计：记录登录日志（时间、IP、设备、结果）

### 5.2 公共类与工具类（common 模块）

基础返回与异常：

- Result<T>：统一响应结构 { code, message, data, traceId, timestamp }
- ErrorCode 枚举：全局 / 业务错误码分段（如 0 成功，1xxxx 参数，2xxxx 业务，5xxxx 系统）
- BusinessException：业务异常，携带错误码
- GlobalExceptionHandler：集中处理参数校验、业务、系统异常，避免 try-catch 散落

通用基类：

- BaseEntity：id, createBy, createTime, updateBy, updateTime, deleted（配合逻辑删除与自动填充）
- PageResult<T>：统一分页返回
- BaseQuery：统一分页查询入参（pageNum / pageSize）

工具类清单（优先复用 Hutool / Commons，必要时再封装）：

| 类别 | 工具 |
| --- | --- |
| 字符串 / 集合 | StrUtil、CollUtil（Hutool） |
| 日期时间 | LocalDateTime 工具类（统一时区与格式化） |
| JSON | Jackson / Fastjson2 封装 |
| 加解密 / 摘要 | MD5、AES、RSA、BCrypt、JWT 工具 |
| 文件 / Excel | 上传下载、EasyExcel |
| 树形结构 | 菜单 / 部门树构建 |
| ID 生成 | Snowflake / UUID |
| 网络 | IP 获取、请求头解析 |
| 校验 | 手机号、身份证、邮箱等自定义校验注解 |

自动填充：通过 MyBatis-Plus MetaObjectHandler 自动写入创建/更新人与时间。

---

## 6. 建议补充的模块与能力（重点）

> 下面按优先级分组。星号越多越建议在项目早期就纳入。

### A. 基础工程能力（强烈建议一开始就具备）

| # | 能力 | 优先级 | 说明 |
| --- | --- | --- | --- |
| 1 | 多环境配置 | 高 | application-{dev,test,prod}.yml，敏感信息走环境变量/配置中心（Nacos/Apollo） |
| 2 | 全局异常处理 + 参数校验 | 高 | 与统一返回配套，所有接口入参用 @Valid/@Validated |
| 3 | 统一日志体系 | 高 | 结构化日志、MDC 注入 traceId、访问日志、慢 SQL 日志、日志分级 |
| 4 | 接口文档 | 高 | springdoc-openapi + Knife4j，注解即文档，支持在线调试 |
| 5 | 数据访问规范 | 高 | 逻辑删除、乐观锁、事务、多数据源、审计字段自动填充 |
| 6 | 缓存与分布式锁 | 中 | Redis 缓存抽象、缓存穿透/击穿防护、Redisson 分布式锁 |
| 7 | 数据库版本管理 | 中 | Flyway 管理 DDL 变更，环境间 Schema 可复现 |
| 8 | 跨域（CORS） | 高 | 全局配置，明确允许的方法/头/源 |
| 9 | 接口幂等 | 中 | 重复提交/消息重试场景，Token 或幂等号 + Redis |
| 10 | 限流 | 中 | 接口级限流（Guava / Redis + 注解），保护核心接口 |

### B. 安全增强

| # | 能力 | 说明 |
| --- | --- | --- |
| 11 | XSS / SQL 注入防护 | 参数过滤、转义；MyBatis 使用 #{} |
| 12 | 敏感数据脱敏 | 手机号、身份证、邮箱日志与返回脱敏（注解 + 序列化） |
| 13 | 防重放 / 签名 | 重要接口加时间戳 + nonce + 签名校验 |
| 14 | 操作审计日志 | 谁、何时、对什么、做了什么（可 AOP + 注解实现） |
| 15 | 文件安全 | 文件类型白名单、大小限制、防路径穿越 |

### C. 可观测性与运维

| # | 能力 | 说明 |
| --- | --- | --- |
| 16 | 健康检查 | Spring Boot Actuator /health、/info |
| 17 | 指标监控 | Prometheus + Grafana，QPS/耗时/错误率/JVM |
| 18 | 链路追踪 | SkyWalking / Micrometer Tracing，贯穿 traceId |
| 19 | 告警 | 异常、慢接口、错误率告警对接钉钉/企业微信 |
| 20 | 容器化 + CI/CD | Dockerfile、docker-compose、Jenkins/GitHub Actions 流水线 |

### D. 业务通用能力（按需引入）

| # | 能力 | 说明 |
| --- | --- | --- |
| 21 | 文件上传 / 对象存储 | MinIO / OSS，统一文件服务 |
| 22 | 消息通知 | 邮件、短信、站内信；异步解耦 |
| 23 | 消息队列 | RabbitMQ / Kafka，削峰填谷、事件驱动 |
| 24 | 定时任务 | XXL-JOB / Quartz，避免 @Scheduled 单机局限 |
| 25 | 数据字典 / 参数配置 | 字典表 + 缓存，系统参数动态配置 |
| 26 | 代码生成 | 基于模板生成 CRUD 骨架（可选） |
| 27 | 国际化 i18n | 多语言资源文件（出海业务） |
| 28 | API 版本管理 | URL 前缀 /api/v1 或 Header 版本 |

### E. 质量保障

| # | 能力 | 说明 |
| --- | --- | --- |
| 29 | 单元测试 / 集成测试 | JUnit5 + Mockito + Testcontainers，核心逻辑必测 |
| 30 | 代码规范扫描 | Checkstyle / Alibaba P3C + SonarQube，CI 卡点 |
| 31 | Git 规范 | 分支策略（main/develop/feature）、Conventional Commits、CR 流程 |

> 一句话总结补充重点：在你已有的“权限 + 公共类”之上，最该优先补齐的是 统一异常/校验、日志与 traceId、接口文档、多环境配置、Flyway，以及缓存与幂等限流，还有监控告警与 CI/CD——这些决定了项目后期是“可控”还是“救火”。

---

## 7. 推荐目录结构

```
xxx-parent/                          # 根 POM，dependencyManagement 统一版本
|-- pom.xml
|-- xxx-common/                      # 公共模块（不依赖业务）
|   `-- src/main/java/com/company/xxx/common/
|       |-- core/                    # Result, PageResult, ErrorCode, BaseEntity
|       |-- exception/               # BusinessException, GlobalExceptionHandler
|       |-- constant/                # 常量
|       |-- enums/                   # 通用枚举
|       |-- util/                    # 工具类
|       |-- validation/              # 自定义校验注解
|       `-- config/                  # 通用配置（Jackson、WebMvc）
|-- xxx-framework/                   # 技术基础设施
|   `-- src/main/java/com/company/xxx/framework/
|       |-- security/                # Security 配置、JWT 过滤器、UserDetails
|       |-- redis/                   # Redis / Redisson 配置
|       |-- mybatis/                 # MyBatis-Plus 配置、自动填充、数据权限
|       |-- aspect/                  # 操作日志、限流、幂等切面
|       |-- interceptor/             # 拦截器（traceId、租户）
|       `-- config/                  # CORS、Swagger、线程池
|-- xxx-system/                      # 系统模块（权限体系在此）
|   `-- src/main/java/com/company/xxx/system/
|       |-- controller/              # 用户/角色/菜单/部门/字典
|       |-- service/ + service/impl/
|       |-- mapper/
|       |-- entity/ + dto/ + vo/ + query/
|       `-- convert/                 # MapStruct 转换器
|-- xxx-module-*/                    # 业务模块（结构同 system）
|-- xxx-job/                         # 定时任务
`-- xxx-admin/                       # 启动模块
    `-- src/main/
        |-- java/com/company/xxx/XxxApplication.java
        `-- resources/
            |-- application.yml
            |-- application-dev.yml
            |-- application-test.yml
            |-- application-prod.yml
            `-- db/migration/        # Flyway SQL
```

---

## 8. 依赖与版本管理

- 根 pom.xml 用 dependencyManagement 统一管理所有依赖版本（BOM），子模块只声明 groupId/artifactId。
- 使用 properties 集中定义版本号，升级只改一处。

| 依赖 | 用途 |
| --- | --- |
| spring-boot-starter-web | Web MVC |
| spring-boot-starter-validation | 参数校验 |
| spring-boot-starter-security | 安全框架 |
| jjwt / java-jwt | JWT |
| mybatis-plus-boot-starter | ORM |
| mysql-connector-j / postgresql | 数据库驱动 |
| spring-boot-starter-data-redis + redisson | 缓存 / 分布式锁 |
| springdoc-openapi-starter-webmvc-ui | 接口文档 |
| knife4j-openapi3-jakarta-spring-boot-starter | 文档 UI 增强 |
| lombok + mapstruct | 简化代码 / 对象转换 |
| hutool-all | 通用工具 |
| flyway-core + flyway-mysql | 数据库迁移 |
| spring-boot-starter-actuator + micrometer-registry-prometheus | 监控 |
| easyexcel | Excel 导入导出 |
| xxl-job-core | 定时任务（按需） |

---

## 9. 开发规范约定

1. 命名：包全小写；类名大驼峰；方法/变量小驼峰；常量全大写下划线；数据库表/字段小写下划线。
2. DTO 分层：Entity（数据库）、DTO（入参）、VO（出参）、Query（查询条件）严格区分；禁止 Entity 直接作为接口入参或出参。
3. Controller：只做路由、校验、调用、返回；方法返回统一 Result<T>。
4. Service：业务与事务边界；事务注解 @Transactional(rollbackFor = Exception.class)。
5. 异常：业务异常统一抛 BusinessException，由全局处理器转成友好响应；禁止吞异常。
6. 日志：使用 SLF4J，占位符 {}；关键操作打点，含 traceId；禁止 System.out.println。
7. 安全：禁止明文密码、禁止拼接 SQL、敏感信息不进日志。
8. 提交：遵循 Conventional Commits，feature 分支开发，MR/PR 合入。

---

## 10. 数据库设计规范

- 表名：t_ 或业务前缀 + 下划线小写（如 sys_user、biz_order）。
- 每张表必备公共字段：id、create_by、create_time、update_by、update_time、deleted（逻辑删除）。
- 主键：雪花 ID（BIGINT）或自增，全项目统一。
- 字符集：utf8mb4，排序规则 utf8mb4_general_ci（MySQL）。
- 索引：查询条件字段建索引，区分度低的字段不建；联合索引遵守最左前缀。
- 约束：业务唯一性用唯一索引；禁止物理删除，统一逻辑删除。
- 金额：用 DECIMAL，禁止 float/double。
- 变更：所有 DDL 通过 Flyway 脚本管理，禁止直接改库。

---

## 11. 接口规范

- 风格：RESTful；URL 名词复数小写，如 /api/v1/users。
- 方法：GET 查询、POST 新增、PUT 更新、DELETE 删除。
- 统一返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "traceId": "8f14e45fceea167a",
  "timestamp": 1700000000000
}
```

- 错误码：0 成功；1xxxx 参数错误；2xxxx 业务错误；4xxxx 认证授权；5xxxx 系统错误。
- 分页：入参 pageNum/pageSize，返回 total + list。
- 版本：URL 前缀 /api/v1，破坏性变更升版本。
- 文档：所有接口用 OpenAPI 注解标注，保持文档与代码同步。

---

## 12. 分阶段落地路线

| 阶段 | 内容 | 产出 |
| --- | --- | --- |
| 阶段一：骨架 | 多模块 POM、common（返回/异常/工具）、framework 基础配置、多环境配置、日志、Swagger | 可运行空壳 + 接口文档 |
| 阶段二：权限体系 | Spring Security + JWT、用户/角色/菜单/部门、数据权限、登录审计 | 完整认证授权 |
| 阶段三：基础设施 | Redis、Flyway、幂等/限流切面、操作日志、CORS、参数校验 | 稳定工程底座 |
| 阶段四：可观测运维 | Actuator、Prometheus、SkyWalking、告警、Docker、CI/CD | 可监控可交付 |
| 阶段五：业务通用 | 文件存储、消息通知、MQ、定时任务、字典、代码生成 | 业务快速开发能力 |
| 阶段六：质量保障 | 单测覆盖、代码扫描、Git 规范落地 | 可持续演进 |

---

## 13. 附录：快速开始 Checklist

搭建新项目时逐项勾选：

- [ ] 根 POM 建立，版本统一管理
- [ ] 多模块划分完成，依赖关系正确
- [ ] Result / ErrorCode / 全局异常 / 参数校验就位
- [ ] BaseEntity + MyBatis-Plus 自动填充 + 逻辑删除
- [ ] Spring Security + JWT 认证，密码加密
- [ ] 用户/角色/菜单表与接口
- [ ] @PreAuthorize 接口鉴权 + 数据权限
- [ ] 多环境配置 + 配置中心/环境变量
- [ ] 日志 + traceId（MDC）
- [ ] OpenAPI / Knife4j 文档
- [ ] Flyway 数据库迁移
- [ ] CORS、幂等、限流
- [ ] Redis 缓存 / 分布式锁
- [ ] Actuator + 监控告警
- [ ] Dockerfile + CI/CD 流水线
- [ ] Git 分支与提交规范、代码扫描

---

> 说明：本文档为框架基线，具体技术选型和模块取舍请结合团队规模、业务复杂度与运维能力调整。
