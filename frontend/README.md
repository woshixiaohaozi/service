# Base Framework Admin（Vue 3 前端）

基于 Vue 3 + Vite + Element Plus + Pinia + TypeScript 的后台管理前端，对接本项目 Spring Boot 后端。

## 技术栈

| 能力 | 选型 |
| --- | --- |
| 框架 | Vue 3（Composition API + script setup） |
| 构建 | Vite 6 |
| UI | Element Plus（中文语言包） |
| 状态 | Pinia |
| 路由 | Vue Router 4（含登录守卫） |
| HTTP | Axios（统一 POST + JWT 注入 + Result 解包） |
| 类型 | TypeScript |

## 目录结构

```
frontend/
├── src/
│   ├── api/          # 接口层：auth / user / role / menu / dept / permission
│   ├── layout/       # 后台布局 Layout.vue（侧边菜单 + 顶栏）
│   ├── router/       # 路由表 + 登录守卫
│   ├── stores/       # Pinia auth store（token / 用户信息 / 权限）
│   ├── utils/        # Axios 封装 request.ts
│   ├── types/        # API 类型定义
│   ├── views/        # 页面：登录 / 首页 / system/{用户,角色,菜单,部门}
│   └── styles/       # 全局样式
├── vite.config.mjs   # Vite 配置（端口 5173，代理 /api 与 /auth 到 8080）
└── package.json
```

## 运行

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:5173，开发服务器会把 `/api` 与 `/auth` 代理到后端 http://localhost:8080。

生产构建：`npm run build`，产物在 `frontend/dist`。

## 已实现功能

- **登录页**：账号密码登录，token 持久化，刷新不丢登录态；演示账号 admin/admin123
- **鉴权守卫**：未登录跳转登录页；登录页自动跳转首页
- **Layout**：动态侧边菜单（按路由表生成）、折叠、用户下拉退出登录
- **用户管理**：分页查询（用户名/状态过滤）、新增/编辑/删除、分配角色
- **角色管理**：分页查询、新增/编辑/删除、树形勾选分配菜单权限
- **菜单管理**：树形表格、新增下级/编辑/删除、类型区分（目录/菜单/权限）
- **部门管理**：树形表格、新增下级/编辑/删除

## 与后端的约定

1. 所有接口使用 **POST**：body 传 JSON，query 传 `@RequestParam` 参数。
2. Axios 拦截器自动解包后端 `Result<T>`，页面直接拿到 `data`。
3. 请求自动携带 `Authorization: Bearer <token>`；后端返回 40100 时自动登出跳转。
4. 权限标识（如 `system:user:edit`）由后端 `/api/v1/users/current-user` 返回，前端 store 提供 `hasPermission` 供按钮级控制。

## 说明

构建脚本中的 `vue-tsc --noEmit` 类型检查已通过。若在某些受限环境（如本会话沙箱）中 `vite build` 因 Node 派生 esbuild 子进程被拦截（EPERM）而失败，属环境限制：esbuild 二进制本身可正常运行，在普通本地终端执行 `npm run build` 即可正常产出。
