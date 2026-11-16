package com.example.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色-菜单权限关联实体，对应中间表 sys_role_menu。
 *
 * <p>多对多关系表：一个角色拥有多个菜单/权限，一条权限也可属于多个角色。
 * 鉴权时通过 roleId 查出所有 menuId，进而得到权限标识集合。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_menu")
public class SysRoleMenu extends BaseEntity {

    /** 角色 ID，关联 sys_role.id。 */
    private Long roleId;

    /** 菜单/权限 ID，关联 sys_menu.id。 */
    private Long menuId;
}
