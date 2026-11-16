package com.example.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 角色 Mapper。继承 BaseMapper 获得基础 CRUD，自定义 SQL 写在 XML 中。 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户 ID 查询该用户拥有的所有角色（联表查询，SQL 见 SysRoleMapper.xml）。
     *
     * @param userId 用户 ID
     * @return 角色列表
     */
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);
}
