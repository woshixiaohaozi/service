package com.example.system.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.core.PageResult;
import com.example.system.dto.UserQuery;
import com.example.system.entity.SysUser;
import com.example.system.mapper.SysUserMapper;
import com.example.system.service.SysUserService;
import org.springframework.stereotype.Service;

/** 用 @DS("slave_1") 演示多数据源切换到从库。 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    @DS("slave_1")
    public PageResult<SysUser> pageUsers(UserQuery query) {
        Page<SysUser> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<SysUser> result = baseMapper.selectPage(page, null);
        return PageResult.of(result.getTotal(), result.getSize(), result.getCurrent(), result.getRecords());
    }
}
