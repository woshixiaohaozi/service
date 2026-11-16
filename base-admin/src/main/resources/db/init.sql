-- ============================================================
-- 基础框架初始化 SQL（MySQL 8.0）
-- 包含：用户、角色、菜单、部门及两张关联表 + 初始数据
-- ============================================================
CREATE DATABASE IF NOT EXISTS base_framework DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE base_framework;

-- ----------------------------
-- 1. 系统用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_user (
  id           BIGINT       NOT NULL COMMENT '主键(雪花ID)',
  username     VARCHAR(64)  NOT NULL COMMENT '用户名',
  password     VARCHAR(128) NOT NULL COMMENT 'BCrypt 加密密码',
  real_name    VARCHAR(64)  DEFAULT NULL COMMENT '姓名',
  mobile       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  status       TINYINT      DEFAULT 1 COMMENT '状态:1启用 0停用',
  dept_id      BIGINT       DEFAULT NULL COMMENT '所属部门ID',
  create_by    VARCHAR(64)  DEFAULT NULL,
  create_time  DATETIME     DEFAULT NULL,
  update_by    VARCHAR(64)  DEFAULT NULL,
  update_time  DATETIME     DEFAULT NULL,
  deleted      TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ----------------------------
-- 2. 系统部门表（树形结构，支撑数据权限）
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_dept (
  id         BIGINT      NOT NULL COMMENT '主键',
  dept_name  VARCHAR(64) NOT NULL COMMENT '部门名称',
  parent_id  BIGINT      DEFAULT 0 COMMENT '父部门ID,0为根',
  leader_id  BIGINT      DEFAULT NULL COMMENT '负责人用户ID',
  sort       INT         DEFAULT 0 COMMENT '排序',
  status     TINYINT     DEFAULT 1 COMMENT '状态:1启用 0停用',
  create_by  VARCHAR(64)  DEFAULT NULL,
  create_time DATETIME    DEFAULT NULL,
  update_by  VARCHAR(64)  DEFAULT NULL,
  update_time DATETIME    DEFAULT NULL,
  deleted    TINYINT      DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统部门表';

-- ----------------------------
-- 3. 系统角色表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_role (
  id         BIGINT      NOT NULL COMMENT '主键',
  role_name  VARCHAR(64) NOT NULL COMMENT '角色名称',
  role_key   VARCHAR(64) NOT NULL COMMENT '角色标识(如 admin)',
  sort       INT         DEFAULT 0 COMMENT '排序',
  status     TINYINT     DEFAULT 1 COMMENT '状态:1启用 0停用',
  remark     VARCHAR(255) DEFAULT NULL COMMENT '备注',
  create_by  VARCHAR(64)  DEFAULT NULL,
  create_time DATETIME    DEFAULT NULL,
  update_by  VARCHAR(64)  DEFAULT NULL,
  update_time DATETIME    DEFAULT NULL,
  deleted    TINYINT      DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- ----------------------------
-- 4. 系统菜单/权限表（M目录 C菜单 F权限）
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_menu (
  id          BIGINT      NOT NULL COMMENT '主键',
  menu_name   VARCHAR(64) NOT NULL COMMENT '菜单名称',
  parent_id   BIGINT      DEFAULT 0 COMMENT '父菜单ID,0为根',
  sort        INT         DEFAULT 0 COMMENT '排序',
  path        VARCHAR(128) DEFAULT NULL COMMENT '路由地址',
  component   VARCHAR(128) DEFAULT NULL COMMENT '组件路径',
  icon        VARCHAR(64)  DEFAULT NULL COMMENT '图标',
  permission  VARCHAR(128) DEFAULT NULL COMMENT '权限标识(如 system:user:list)',
  menu_type   CHAR(1)     NOT NULL COMMENT '类型:M目录 C菜单 F权限',
  visible     TINYINT     DEFAULT 1 COMMENT '是否可见:1是 0否',
  status      TINYINT     DEFAULT 1 COMMENT '状态:1启用 0停用',
  create_by   VARCHAR(64)  DEFAULT NULL,
  create_time DATETIME     DEFAULT NULL,
  update_by   VARCHAR(64)  DEFAULT NULL,
  update_time DATETIME     DEFAULT NULL,
  deleted     TINYINT      DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单权限表';

-- ----------------------------
-- 5. 用户-角色关联表（id自增，无逻辑删除）
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_user_role (
  id          BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键(自增)',
  user_id     BIGINT      NOT NULL COMMENT '用户ID',
  role_id     BIGINT      NOT NULL COMMENT '角色ID',
  create_by   VARCHAR(64) DEFAULT NULL,
  create_time DATETIME    DEFAULT NULL,
  update_by   VARCHAR(64) DEFAULT NULL,
  update_time DATETIME    DEFAULT NULL,
  deleted     TINYINT     DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------
-- 6. 角色-菜单关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_role_menu (
  id          BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键(自增)',
  role_id     BIGINT      NOT NULL COMMENT '角色ID',
  menu_id     BIGINT      NOT NULL COMMENT '菜单权限ID',
  create_by   VARCHAR(64) DEFAULT NULL,
  create_time DATETIME    DEFAULT NULL,
  update_by   VARCHAR(64) DEFAULT NULL,
  update_time DATETIME    DEFAULT NULL,
  deleted     TINYINT     DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_menu (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ============================================================
-- 初始数据
-- ============================================================
-- 部门
INSERT INTO sys_dept (id, dept_name, parent_id, sort, status, deleted) VALUES
(1, '总公司', 0, 1, 1, 0),
(2, '研发部', 1, 1, 1, 0),
(3, '市场部', 1, 2, 1, 0);

-- 用户（密码明文 admin123，BCrypt 加密串）
INSERT INTO sys_user (id, username, password, real_name, mobile, status, dept_id, deleted) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOVTHYKI33B8TFJmRMKQYQ0HkoiJ7.', '系统管理员', '13800000000', 1, 1, 0);

-- 角色
INSERT INTO sys_role (id, role_name, role_key, sort, status, remark, deleted) VALUES
(1, '超级管理员', 'admin', 1, 1, '拥有全部权限', 0),
(2, '普通用户', 'user', 2, 1, '基础查看权限', 0);

-- 菜单/权限：目录 -> 菜单 -> 按钮权限
INSERT INTO sys_menu (id, menu_name, parent_id, sort, path, component, icon, permission, menu_type, visible, status, deleted) VALUES
(1, '系统管理', 0, 1, '/system', NULL, 'setting', NULL, 'M', 1, 1, 0),
(2, '用户管理', 1, 1, '/system/user', 'system/user/index', 'user', 'system:user:list', 'C', 1, 1, 0),
(3, '角色管理', 1, 2, '/system/role', 'system/role/index', 'peoples', 'system:role:list', 'C', 1, 1, 0),
(4, '菜单管理', 1, 3, '/system/menu', 'system/menu/index', 'tree', 'system:menu:list', 'C', 1, 1, 0),
(10, '用户查询', 2, 1, NULL, NULL, NULL, 'system:user:list', 'F', 0, 1, 0),
(11, '用户编辑', 2, 2, NULL, NULL, NULL, 'system:user:edit', 'F', 0, 1, 0);

-- 用户-角色：admin 拥有超级管理员角色
INSERT INTO sys_user_role (user_id, role_id, create_by, create_time, deleted) VALUES
(1, 1, 'system', NOW(), 0);

-- 角色-菜单：超级管理员拥有全部菜单权限
INSERT INTO sys_role_menu (role_id, menu_id, create_by, create_time, deleted) VALUES
(1, 1, 'system', NOW(), 0), (1, 2, 'system', NOW(), 0), (1, 3, 'system', NOW(), 0),
(1, 4, 'system', NOW(), 0), (1, 10, 'system', NOW(), 0), (1, 11, 'system', NOW(), 0);
