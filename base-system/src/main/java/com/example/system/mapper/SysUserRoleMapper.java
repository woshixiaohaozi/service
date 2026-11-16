package com.example.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 用户-角色关联 Mapper。 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /** 根据用户 ID 查询关联的角色 ID 列表。 */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /** 物理删除用户与角色的全部关联（重新分配角色时先删后插）。 */
    int deleteByUserId(@Param("userId") Long userId);

    /** 批量插入用户-角色关联（XML 中使用 <foreach> 批量 insert）。 */
    int batchInsert(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
}
