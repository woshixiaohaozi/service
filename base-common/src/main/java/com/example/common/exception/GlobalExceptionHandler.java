package com.example.common.exception;

import com.example.common.constant.CommonConstant;
import com.example.common.core.ErrorCode;
import com.example.common.core.Result;
import com.example.common.core.TraceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器，通过 {@code @RestControllerAdvice} 统一兜底所有 Controller 抛出的异常。
 *
 * <p>设计意图：保证接口在任何情况下都返回 {@link Result} 结构，
 * 避免 Spring 默认错误页面或裸异常堆栈泄露给前端；
 * 按异常类型分级处理——业务异常与参数异常用 warn 级别的简要日志并返回业务码，
 * 未知系统异常用 error 级别记录完整堆栈、对外只暴露通用文案，防止内部信息泄露。
 *
 * <p>使用场景：全局注册一次即可，业务代码中无需 try-catch 这些已知异常类型。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务代码主动抛出的 {@link BusinessException}。
     * 属于可预期失败，用 warn 记录即可，并把异常中的业务码与消息原样返回。
     *
     * @param e       业务异常，携带业务错误码
     * @param request 当前请求，用于日志中打印 URI 便于定位
     * @return 携带业务错误码的失败响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e, HttpServletRequest request) {
        log.warn("[{}] 业务异常: {}", request.getRequestURI(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理 {@code @RequestBody} 参数上的 Bean Validation 校验失败（MethodArgumentNotValidException）。
     * 将多个字段的错误文案拼接为一条信息，方便前端一次性展示。
     *
     * @param e 参数校验失败异常
     * @return 参数校验失败码（10001）的响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                // 多个字段错误用分号拼接，避免只返回第一个错误导致反复提交
                .collect(Collectors.joining("; "));
        return Result.fail(ErrorCode.PARAM_VALIDATE_FAILED.getCode(), msg);
    }

    /**
     * 处理表单/路径参数绑定时的校验失败（BindException），处理策略与 handleValid 一致。
     *
     * @param e 绑定校验失败异常
     * @return 参数校验失败码（10001）的响应
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBind(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.fail(ErrorCode.PARAM_VALIDATE_FAILED.getCode(), msg);
    }

    /**
     * 处理 {@code @Validated} 类级别约束校验失败（ConstraintViolationException），
     * 常见于方法参数上的 @Min / @NotBlank 等约束。
     *
     * @param e 约束违规异常
     * @return 参数校验失败码（10001）的响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraint(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                // 合并所有违规项消息，保持与另外两种参数异常一致的输出格式
                .collect(Collectors.joining("; "));
        return Result.fail(ErrorCode.PARAM_VALIDATE_FAILED.getCode(), msg);
    }

    /**
     * 处理非法参数异常（IllegalArgumentException），映射为通用参数错误码。
     *
     * @param e 非法参数异常
     * @return 参数错误码（10000）的响应，消息取自异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgument(IllegalArgumentException e) {
        return Result.fail(ErrorCode.PARAM_ERROR.getCode(), e.getMessage());
    }

    /**
     * 处理请求方法不支持异常（如用 GET 调用了仅允许 POST 的接口）。
     *
     * @param e 请求方法不支持异常
     * @return 固定使用 METHOD_NOT_ALLOWED 错误码，不把框架原始文案透传给前端
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return Result.fail(ErrorCode.METHOD_NOT_ALLOWED.getCode(),
                "仅支持 POST 请求，当前请求方法不被允许");
    }

    /**
     * 兜底处理所有未被上面捕获的异常。
     *
     * <p>注意两点：一是用 error 级别打印完整堆栈（含 traceId），供研发排查；
     * 二是对外只返回通用"系统繁忙"文案，绝不暴露异常细节；
     * 同时把 HTTP 状态设为 500，让调用方与监控系统能区分系统级失败与业务级失败
     * （业务失败走 Result 结构但 HTTP 状态仍为 200）。
     *
     * @param e       未知异常
     * @param request 当前请求，用于打印 URI 与 traceId
     * @return HTTP 500 响应，body 为统一失败结构
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e, HttpServletRequest request) {
        log.error("[{}] 系统异常, traceId={}", request.getRequestURI(), TraceContext.getTraceId(), e);
        Result<Void> body = Result.fail(ErrorCode.SYSTEM_ERROR);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
