package com.example.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 系统菜单与权限实体，对应表 sys_menu。
 * menuType：M 目录、C 菜单、F 按钮/接口权限。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    private String menuName;
    private Long parentId;
    private Integer sort;
    private String path;
    private String component;
    private String icon;
    private String permission;
    private String menuType;
    private Integer visible;
    private Integer status;

    /** 子节点集合，仅用于构建树形返回，不映射数据库字段。 */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private List<SysMenu> children;
}
