package com.example.system.controller;

import com.example.common.core.Result;
import com.example.system.dto.AssignRoleRequest;
import com.example.system.service.SysPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 权限管理接口：角色分配与权限查询，全站统一 POST。 */
@Tag(name = "权限管理")
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class SysPermissionController {

    private final SysPermissionService permissionService;

    /** 角色分配请求体（含菜单分配）。 */
    @Data
    @AllArgsConstructor
    public static class AssignMenusRequest {
        private Long roleId;
        private List<Long> menuIds;
    }

    @Operation(summary = "查询用户角色")
    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<?> roles(@RequestParam Long userId) {
        return Result.ok(permissionService.listRolesByUser(userId));
    }

    /** 查询用户角色 ID 列表（前端分配角色时回显勾选）。 */
    @PostMapping("/user-role-ids")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<List<Long>> userRoleIds(@RequestParam Long userId) {
        return Result.ok(permissionService.listRoleIdsByUser(userId));
    }

    @Operation(summary = "查询用户权限标识")
    @PostMapping("/user-permissions")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<List<String>> userPermissions(@RequestParam Long userId) {
        return Result.ok(permissionService.listPermissionsByUser(userId));
    }

    @Operation(summary = "为用户分配角色")
    @PostMapping("/assign-roles")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> assignRoles(@Valid @RequestBody AssignRoleRequest request) {
        permissionService.assignRoles(request.getUserId(), request.getRoleIds());
        return Result.ok();
    }

    @Operation(summary = "查询角色已分配的菜单ID")
    @PostMapping("/role-menus")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<List<Long>> roleMenus(@RequestParam Long roleId) {
        return Result.ok(permissionService.listMenuIdsByRole(roleId));
    }

    /** 为角色分配菜单权限（先删后插，事务保证）。 */
    @PostMapping("/assign-menus")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> assignMenus(@RequestBody AssignMenusRequest request) {
        permissionService.assignMenus(request.getRoleId(), request.getMenuIds());
        return Result.ok();
    }
}
