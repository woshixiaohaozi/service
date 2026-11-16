package com.example.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.system.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 角色-菜单关联 Mapper。 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /** 根据角色 ID 查询关联的菜单 ID 列表。 */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /** 物理删除角色与菜单的全部关联（重新分配权限时先删后插）。 */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /** 批量插入角色-菜单关联。 */
    int batchInsert(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);
}
