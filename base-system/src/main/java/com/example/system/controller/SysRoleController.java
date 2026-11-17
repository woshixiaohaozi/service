package com.example.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.core.Result;
import com.example.system.dto.RoleQuery;
import com.example.system.entity.SysRole;
import com.example.system.mapper.SysRoleMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 角色管理接口。 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleMapper roleMapper;

    @Operation(summary = "分页查询角色")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<IPage<SysRole>> page(@Valid @RequestBody RoleQuery query) {
        Page<SysRole> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SysRole> result = roleMapper.selectPage(page, Wrappers.<SysRole>lambdaQuery()
                .like(query.getRoleName() != null && !query.getRoleName().isEmpty(),
                      SysRole::getRoleName, query.getRoleName())
                .eq(query.getStatus() != null, SysRole::getStatus, query.getStatus())
                .orderByAsc(SysRole::getSort));
        return Result.ok(result);
    }

    /** 角色下拉列表。 */
    @PostMapping("/list")
    public Result<List<SysRole>> list() {
        return Result.ok(roleMapper.selectList(Wrappers.<SysRole>lambdaQuery()
                .eq(SysRole::getStatus, 1)));
    }

    /** 新增或编辑角色。 */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> save(@Valid @RequestBody SysRole role) {
        if (role.getId() == null) {
            roleMapper.insert(role);
        } else {
            roleMapper.updateById(role);
        }
        return Result.ok();
    }

    /** 删除角色。 */
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> delete(@RequestParam Long id) {
        roleMapper.deleteById(id);
        return Result.ok();
    }
}
