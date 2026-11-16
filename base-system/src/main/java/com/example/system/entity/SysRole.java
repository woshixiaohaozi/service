package com.example.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色实体，对应表 sys_role。
 *
 * <p>角色是权限的载体：一个角色关联多个菜单权限，一个用户可拥有多个角色。
 * 角色通过 role_key（角色标识）参与鉴权，例如 admin、operator。
 * 继承 {@link BaseEntity} 后自动拥有主键、审计字段与逻辑删除能力。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /** 角色名称，用于界面展示，如“系统管理员”。 */
    private String roleName;

    /**
     * 角色标识（权限标识），全局唯一，如 admin / operator。
     * 代码中通过 hasRole('admin') 或 hasAuthority('admin') 进行判断。
     */
    private String roleKey;

    /** 角色排序号，数值越小越靠前。 */
    private Integer sort;

    /** 状态：1 启用，0 停用。停用的角色不参与权限计算。 */
    private Integer status;

    /** 角色描述，说明该角色的职责范围。 */
    private String remark;
}
