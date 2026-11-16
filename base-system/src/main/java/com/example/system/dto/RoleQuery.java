package com.example.system.dto;

import com.example.common.core.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 角色分页查询条件，继承统一分页参数。 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQuery extends BaseQuery {

    /** 按角色名称模糊查询。 */
    private String roleName;

    /** 按状态精确查询：1 启用，0 停用。 */
    private Integer status;
}
