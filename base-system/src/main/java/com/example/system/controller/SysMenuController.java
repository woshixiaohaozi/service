package com.example.system.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.common.core.Result;
import com.example.system.entity.SysMenu;
import com.example.system.mapper.SysMenuMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 菜单管理接口：提供平铺列表与树形结构两种返回。 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuMapper menuMapper;

    /** 平铺列表（表格/编辑使用）。 */
    @PostMapping("/list")
    public Result<List<SysMenu>> list() {
        return Result.ok(menuMapper.selectList(Wrappers.<SysMenu>lambdaQuery()
                .orderByAsc(SysMenu::getSort)));
    }

    /** 树形结构（菜单管理页与角色授权树使用）。 */
    @Operation(summary = "菜单树")
    @PostMapping("/tree")
    public Result<List<SysMenu>> tree() {
        List<SysMenu> all = menuMapper.selectList(Wrappers.<SysMenu>lambdaQuery()
                .orderByAsc(SysMenu::getSort));
        Map<Long, List<SysMenu>> childrenMap = all.stream()
                .collect(Collectors.groupingBy(SysMenu::getParentId));
        // 从根节点开始递归组装：根节点 parentId 为 0
        List<SysMenu> roots = childrenMap.getOrDefault(0L, new ArrayList<>()).stream()
                .sorted(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        for (SysMenu root : roots) {
            fillChildren(root, childrenMap);
        }
        return Result.ok(roots);
    }

    /** 递归填充子节点。 */
    private void fillChildren(SysMenu parent, Map<Long, List<SysMenu>> childrenMap) {
        List<SysMenu> children = childrenMap.get(parent.getId());
        if (children != null && !children.isEmpty()) {
            children.sort(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
            parent.setChildren(children);
            for (SysMenu child : children) {
                fillChildren(child, childrenMap);
            }
        }
    }

    /** 新增或编辑菜单。 */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result<Void> save(@RequestBody SysMenu menu) {
        if (menu.getId() == null) {
            menuMapper.insert(menu);
        } else {
            menuMapper.updateById(menu);
        }
        return Result.ok();
    }

    /** 删除菜单。 */
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result<Void> delete(@RequestParam Long id) {
        menuMapper.deleteById(id);
        return Result.ok();
    }
}
