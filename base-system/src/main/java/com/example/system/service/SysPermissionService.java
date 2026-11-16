package com.example.system.service;

import com.example.system.mapper.SysMenuMapper;
import com.example.system.mapper.SysRoleMapper;
import com.example.system.mapper.SysRoleMenuMapper;
import com.example.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限管理聚合服务：负责角色分配与权限查询。
 *
 * <p>把跨多张表的写操作放在一个 Service 中，用 @Transactional 保证原子性，
 * 避免出现“删了旧关联但插入新关联失败”的中间状态。</p>
 */
@Service
@RequiredArgsConstructor
public class SysPermissionService {

    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;

    /** 查询用户拥有的角色列表。 */
    public List<?> listRolesByUser(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    /** 查询用户拥有的权限标识列表。 */
    public List<String> listPermissionsByUser(Long userId) {
        return menuMapper.selectPermissionsByUserId(userId);
    }

    /**
     * 为用户重新分配角色：先物理删除旧关联，再批量插入新关联。
     * @Transactional 保证两步要么都成功、要么都回滚。
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.deleteByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            userRoleMapper.batchInsert(userId, roleIds);
        }
    }

    /**
     * 为角色重新分配菜单权限：先删后插，保证权限变更原子性。
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        roleMenuMapper.deleteByRoleId(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            roleMenuMapper.batchInsert(roleId, menuIds);
        }
    }

    /** 查询角色已分配的菜单 ID，用于前端回显勾选状态。 */
    public List<Long> listMenuIdsByRole(Long roleId) {
        return new ArrayList<>(roleMenuMapper.selectMenuIdsByRoleId(roleId));
    }
}
