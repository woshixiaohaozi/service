package com.example.system.controller;

import com.example.common.core.PageResult;
import com.example.common.core.Result;
import com.example.system.dto.UserQuery;
import com.example.system.entity.SysUser;
import com.example.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/v1/users")
public class SysUserController {

    private final SysUserService userService;

    public SysUserController(SysUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "分页查询用户（走从库）")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<PageResult<SysUser>> page(@Valid @RequestBody UserQuery query) {
        return Result.ok(userService.pageUsers(query));
    }
}
