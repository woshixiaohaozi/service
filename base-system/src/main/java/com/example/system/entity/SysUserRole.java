package com.example.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户-角色关联实体，对应中间表 sys_user_role。
 *
 * <p>多对多关系表：一个用户可拥有多个角色，一个角色可分配给多个用户。
 * 登录鉴权时通过 userId 查出所有 roleId，再据此加载角色与权限。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_role")
public class SysUserRole extends BaseEntity {

    /** 用户 ID，关联 sys_user.id。 */
    private Long userId;

    /** 角色 ID，关联 sys_role.id。 */
    private Long roleId;
}
