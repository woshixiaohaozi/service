package com.example.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** 为用户分配角色的请求体。 */
@Data
public class AssignRoleRequest {

    /** 目标用户 ID。 */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 角色 ID 列表，可为空表示清空该用户所有角色。 */
    private java.util.List<Long> roleIds;
}
