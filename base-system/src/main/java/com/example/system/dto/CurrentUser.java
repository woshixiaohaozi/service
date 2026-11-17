package com.example.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/** 当前登录用户信息，供前端加载权限与展示。 */
@Data
@AllArgsConstructor
public class CurrentUser {
    private String username;
    private String realName;
    private List<String> permissions;
    private List<String> roles;
}
