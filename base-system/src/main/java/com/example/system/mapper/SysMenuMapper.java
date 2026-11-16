package com.example.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 菜单/权限 Mapper。自定义 SQL 写在 XML 中。 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据用户 ID 查询其拥有的全部权限标识（通过用户-角色-菜单三表联查）。
     * 登录时加载用户权限即调用此方法，结果形如 system:user:list。
     *
     * @param userId 用户 ID
     * @return 权限标识列表
     */
    List<String> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 根据多个角色 ID 批量查询权限标识（<foreach> 动态 SQL 示例）。
     *
     * @param roleIds 角色 ID 集合
     * @return 权限标识列表
     */
    List<String> selectPermissionsByRoleIds(@Param("roleIds") List<Long> roleIds);
}
