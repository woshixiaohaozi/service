package com.example.system.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.system.entity.SysUser;
import com.example.system.mapper.SysMenuMapper;
import com.example.system.mapper.SysUserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring Security 的 UserDetailsService 实现。
 *
 * <p>登录认证时由 DaoAuthenticationProvider 调用 loadUserByUsername，
 * 从数据库加载用户，并通过三表联查加载该用户的全部权限标识，
 * 组装成 Spring Security 的 UserDetails（含权限集合）。</p>
 */
@Service
public class SysUserDetailsService implements UserDetailsService {

    private final SysUserMapper userMapper;
    private final SysMenuMapper menuMapper;

    public SysUserDetailsService(SysUserMapper userMapper, SysMenuMapper menuMapper) {
        this.userMapper = userMapper;
        this.menuMapper = menuMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 从数据库按用户名查询用户（逻辑删除的记录会被 MyBatis-Plus 自动过滤）
        SysUser user = userMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        // 2. 查询该用户的权限标识列表（SQL 见 SysMenuMapper.xml）
        List<String> permissions = menuMapper.selectPermissionsByUserId(user.getId());
        // 3. 转成 Spring Security 的 GrantedAuthority 集合
        List<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        // 4. 密码由 DaoAuthenticationProvider 用 PasswordEncoder 自动校验
        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .disabled(user.getStatus() == null || user.getStatus() != 1)
                .authorities(authorities)
                .build();
    }
}
