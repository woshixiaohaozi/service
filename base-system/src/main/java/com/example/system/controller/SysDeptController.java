package com.example.system.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.common.core.Result;
import com.example.system.entity.SysDept;
import com.example.system.mapper.SysDeptMapper;
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

/** 部门管理接口。 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/api/v1/depts")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptMapper deptMapper;

    @PostMapping("/list")
    public Result<List<SysDept>> list() {
        return Result.ok(deptMapper.selectList(Wrappers.<SysDept>lambdaQuery()
                .orderByAsc(SysDept::getSort)));
    }

    /** 部门树。 */
    @PostMapping("/tree")
    public Result<List<SysDept>> tree() {
        List<SysDept> all = deptMapper.selectList(Wrappers.<SysDept>lambdaQuery()
                .orderByAsc(SysDept::getSort));
        Map<Long, List<SysDept>> childrenMap = all.stream()
                .collect(Collectors.groupingBy(SysDept::getParentId));
        List<SysDept> roots = childrenMap.getOrDefault(0L, new ArrayList<>()).stream()
                .sorted(Comparator.comparing(SysDept::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        for (SysDept root : roots) fillChildren(root, childrenMap);
        return Result.ok(roots);
    }

    private void fillChildren(SysDept parent, Map<Long, List<SysDept>> childrenMap) {
        List<SysDept> children = childrenMap.get(parent.getId());
        if (children != null && !children.isEmpty()) {
            children.sort(Comparator.comparing(SysDept::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
            parent.setChildren(children);
            for (SysDept child : children) fillChildren(child, childrenMap);
        }
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('system:dept:edit')")
    public Result<Void> save(@RequestBody SysDept dept) {
        if (dept.getId() == null) deptMapper.insert(dept); else deptMapper.updateById(dept);
        return Result.ok();
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('system:dept:edit')")
    public Result<Void> delete(@RequestParam Long id) {
        deptMapper.deleteById(id);
        return Result.ok();
    }
}
