package com.example.framework.config;

import com.example.common.core.ErrorCode;
import com.example.common.core.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 安全异常专门处理器。
 *
 * <p>为什么需要它：方法级鉴权（@PreAuthorize）抛出的 AccessDeniedException 发生在
 * Controller 调用阶段，会被 Spring MVC 的 @RestControllerAdvice 捕获；若不单独处理，
 * 就会被 GlobalExceptionHandler 的兜底 @ExceptionHandler(Exception.class) 当成
 * "系统异常"打成 500 并打印整栈堆栈，既误导排障又泄露内部信息。</p>
 *
 * <p>这里用 @Order(HIGHEST_PRECEDENCE) 保证优先级高于全局兜底处理器，
 * 将安全异常转成干净的 401/403 + 统一 Result 结构。</p>
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(SecurityExceptionHandler.class);

    /** 权限不足（有认证但无权限）→ 403。 */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDenied(AccessDeniedException e) {
        // 权限不足是可预期结果，用 warn 简要记录，不打堆栈
        log.warn("权限不足: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.fail(ErrorCode.FORBIDDEN.getCode(), ErrorCode.FORBIDDEN.getMessage()));
    }

    /** 认证失败（未登录或 token 无效）→ 401。 */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<Void>> handleAuthentication(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Result.fail(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage()));
    }
}
