package com.example.system.controller;

import com.example.common.core.ErrorCode;
import com.example.common.core.Result;
import com.example.common.exception.BusinessException;
import com.example.framework.security.JwtTokenProvider;
import com.example.system.dto.LoginRequest;
import com.example.system.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口控制器（白名单，无需登录即可访问）。
 *
 * <p>登录流程：
 * 1. Controller 接收用户名密码；
 * 2. AuthenticationManager 委托 DaoAuthenticationProvider 查库 + BCrypt 校验；
 * 3. 认证通过后用 JwtTokenProvider 签发 Token 返回。</p>
 */
@Tag(name = "认证")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    /** 登录：走数据库校验 + BCrypt 密码比对，成功后签发 JWT。 */
    @Operation(summary = "登录，返回 JWT")
    @PostMapping("/login")
    public Result<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            // authenticate() 内部会调用 SysUserDetailsService 查库，并用 BCrypt 校验密码
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = tokenProvider.generateToken(userDetails.getUsername());
            return Result.ok(new TokenResponse(token, "Bearer", tokenProvider.getExpirationMs()));
        } catch (BadCredentialsException e) {
            // 密码错误或用户不存在，统一返回业务错误，避免泄露用户是否存在
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
    }
}
