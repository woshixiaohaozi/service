package com.example.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.core.PageResult;
import com.example.system.dto.UserQuery;
import com.example.system.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    PageResult<SysUser> pageUsers(UserQuery query);
}
