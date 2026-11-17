package com.example.system.controller;

import com.example.common.core.Result;
import com.example.system.dto.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 当前登录用户接口，路径挂在 /auth 下便于前端统一加载。
 * 实际逻辑委托给 SysUserController#currentUser。
 */
@Tag(name = "认证")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class CurrentUserController {

    private final SysUserController userController;

    @PostMapping("/current-user")
    public Result<CurrentUser> currentUser() {
        return userController.currentUser();
    }
}
