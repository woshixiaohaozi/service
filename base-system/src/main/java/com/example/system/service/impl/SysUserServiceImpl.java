package com.example.system.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.core.PageResult;
import com.example.system.dto.UserQuery;
import com.example.system.entity.SysUser;
import com.example.system.mapper.SysUserMapper;
import com.example.system.service.SysUserService;
import org.springframework.stereotype.Service;

/** 用户服务实现：分页查询走从库（@DS 演示），写操作走主库。 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    /** 分页查询用户，支持用户名模糊与状态过滤。 */
    @Override
//    @DS("slave_1")
    public PageResult<SysUser> pageUsers(UserQuery query) {
        Page<SysUser> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<SysUser> result = baseMapper.selectPage(page, Wrappers.<SysUser>lambdaQuery()
                .like(query.getUsername() != null && !query.getUsername().isEmpty(),
                      SysUser::getUsername, query.getUsername())
                .eq(query.getStatus() != null, SysUser::getStatus, query.getStatus())
                .orderByAsc(SysUser::getId));
        return PageResult.of(result.getTotal(), result.getSize(), result.getCurrent(), result.getRecords());
    }
}
