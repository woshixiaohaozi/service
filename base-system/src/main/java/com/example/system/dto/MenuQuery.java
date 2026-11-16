package com.example.system.dto;

import com.example.common.core.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 菜单/权限查询条件。 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuQuery extends BaseQuery {

    /** 按菜单名称模糊查询。 */
    private String menuName;

    /** 按菜单类型过滤：M 目录 / C 菜单 / F 权限。 */
    private String menuType;

    /** 按状态过滤。 */
    private Integer status;
}
