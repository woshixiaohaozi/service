package com.example.system.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.common.core.PageResult;
import com.example.common.core.Result;
import com.example.system.dto.CurrentUser;
import com.example.system.dto.UserQuery;
import com.example.system.entity.SysUser;
import com.example.system.mapper.SysMenuMapper;
import com.example.system.mapper.SysRoleMapper;
import com.example.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 用户管理接口：全站统一 POST。 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/v1/users")
public class SysUserController {

    private final SysUserService userService;
    private final SysMenuMapper menuMapper;
    private final SysRoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    public SysUserController(SysUserService userService, SysMenuMapper menuMapper,
                             SysRoleMapper roleMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.menuMapper = menuMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "分页查询用户")
    @PostMapping("/page")
    public Result<PageResult<SysUser>> page(@Valid @RequestBody UserQuery query) {
        return Result.ok(userService.pageUsers(query));
    }

    @PostMapping("/list")
    public Result<List<SysUser>> list() {
        return Result.ok(userService.list(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getStatus, 1)
                .select(SysUser::getId, SysUser::getUsername, SysUser::getRealName)));
    }

    @PostMapping("/save")
    public Result<Void> save(@Valid @RequestBody SysUser user) {
        if (user.getId() == null && user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else if (user.getId() != null) {
            user.setPassword(null);
        }
        userService.saveOrUpdate(user);
        return Result.ok();
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestParam Long id) {
        userService.removeById(id);
        return Result.ok();
    }

    /** 启用 / 禁用用户。 */
    @PostMapping("/change-status")
    public Result<Void> changeStatus(@RequestParam Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        userService.updateById(user);
        return Result.ok();
    }

    @PostMapping("/current-user")
    public Result<CurrentUser> currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth == null ? "anonymous" : auth.getName();
        SysUser user = userService.getOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, username));
        List<String> permissions = user == null ? List.of() : menuMapper.selectPermissionsByUserId(user.getId());
        List<String> roles = user == null ? List.of() : roleMapper.selectRolesByUserId(user.getId())
                .stream().map(r -> r.getRoleKey()).toList();
        String realName = user == null ? username : user.getRealName();
        return Result.ok(new CurrentUser(username, realName, permissions, roles));
    }
}
