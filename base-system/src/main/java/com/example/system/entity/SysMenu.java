package com.example.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统菜单与权限实体，对应表 sys_menu。
 *
 * <p>采用“菜单树 + 权限码”一体化设计：一条记录既可能是一个可见菜单，
 * 也可能只是一个按钮级权限或后端接口权限，由 {@code menuType} 区分：</p>
 * <ul>
 *   <li>M：目录（一级菜单）</li>
 *   <li>C：菜单（可点击的页面）</li>
 *   <li>F：按钮/接口权限（不可见，仅用于鉴权）</li>
 * </ul>
 * <p>parentId 指向父节点，根节点 parentId 为 0，据此构建树形结构。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    /** 菜单名称，展示在界面或权限列表中。 */
    private String menuName;

    /** 父菜单 ID，0 表示根节点。 */
    private Long parentId;

    /** 排序号，同级菜单按此值升序排列。 */
    private Integer sort;

    /** 路由地址，前端据此生成页面路由，如 /system/user。 */
    private String path;

    /** 前端组件路径，如 system/user/index。 */
    private String component;

    /** 菜单图标（前端图标库中的名称）。 */
    private String icon;

    /**
     * 权限标识，是后端鉴权的核心，如 system:user:list、system:role:add。
     * Controller 上的 @PreAuthorize 通过 hasAuthority('权限标识') 引用它。
     */
    private String permission;

    /** 菜单类型：M 目录 / C 菜单 / F 按钮或接口权限。 */
    private String menuType;

    /** 是否可见：1 显示，0 隐藏（隐藏的菜单有路由但不展示）。 */
    private Integer visible;

    /** 状态：1 启用，0 停用。 */
    private Integer status;
}
