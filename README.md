# Base Framework

基于 Spring Boot 3.2.5 + JDK 17 + MySQL 8 的后端基础框架，全站接口统一 POST。

## 模块结构

```
base-parent
├── base-common      统一返回 Result、ErrorCode、BusinessException、全局异常、BaseEntity、工具类（详细注释）
├── base-framework   多数据源、JWT 安全链、MyBatis-Plus、traceId、跨域、POST 约束、操作日志
├── base-system      认证 + 完整权限业务：用户/角色/菜单/部门 + 用户角色/角色菜单关联
├── base-job         定时任务：调度线程池配置 + cron/fixedRate/fixedDelay 示例 + 数据清理任务
└── base-admin       启动模块、多环境配置、Mapper XML、初始化 SQL
```

## 核心能力

- **统一 POST**：PostOnlyFilter 拦截非 POST 请求，返回 405 + 统一 Result（文档资源放行）。
- **统一返回**：所有接口返回 `Result<T>` = code / message / data / traceId / timestamp。
- **全局异常**：业务异常 BusinessException / ServiceException + GlobalExceptionHandler 统一兜底。
- **登录走数据库**：AuthenticationManager → DaoAuthenticationProvider → SysUserDetailsService 查库 + BCrypt 密码校验，登录后签发 JWT；权限标识通过三表联查加载。
- **权限业务**：用户、角色、菜单、部门、用户-角色、角色-菜单 6 张表；角色分配、菜单分配、权限查询接口；@PreAuthorize 接口鉴权。
- **多数据源**：master / slave_1，方法上用 `@DS("slave_1")` 切换。
- **Mapper XML 示例**：`base-system/src/main/resources/mapper/` 下 4 个 XML，演示联表查询、DISTINCT、<if>、<foreach> 批量插入/IN 查询、SQL 片段复用。
- **定时任务**：`@Scheduled` + 自定义 ThreadPoolTaskScheduler，任务开关由 application-job.yml 控制。
- **链路追踪**：请求头 X-Trace-Id 贯穿 MDC 日志与响应体。

## 数据库

执行 `base-admin/src/main/resources/db/init.sql` 自动建库建表并插入初始数据：

| 账号 | 密码 | 角色 | 权限 |
| --- | --- | --- | --- |
| admin | admin123 | 超级管理员 | 全部菜单与按钮权限 |

## 构建与运行

```bash
mvn clean package -DskipTests
java -jar base-admin/target/base-admin.jar
```

运行前修改 `base-admin/src/main/resources/application-dev.yml` 的 MySQL/Redis 地址。
注意：slave_1 指向 `base_framework_slave` 库，请按实际环境准备从库或改为同库。

## 主要接口

所有接口均为 POST：

| 接口 | 说明 | 是否需登录 |
| --- | --- | --- |
| /auth/login | 登录（查库 + BCrypt），返回 JWT | 否 |
| /api/v1/users/page | 用户分页（演示 @DS 从库） | 是 |
| /api/v1/roles/page | 角色分页 | 是 + system:role:list |
| /api/v1/permissions/roles | 查询用户角色 | 是 + system:user:list |
| /api/v1/permissions/user-permissions | 查询用户权限标识 | 是 |
| /api/v1/permissions/assign-roles | 为用户分配角色（事务） | 是 + system:user:edit |
| /api/v1/permissions/role-menus | 查询角色菜单ID | 是 |

请求头：`Authorization: Bearer <token>`
接口文档：<http://localhost:8080/doc.html>
