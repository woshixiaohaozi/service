package com.example.common.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局错误码枚举。
 * 分段：0 成功 / 1xxxx 参数 / 2xxxx 业务 / 4xxxx 认证授权 / 5xxxx 系统。
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    /** 成功码，取值与 {@code CommonConstant.SUCCESS_CODE} 保持一致 */
    SUCCESS(0, "success"),

    // ===== 参数错误 1xxxx：入参缺失、格式错误或校验不通过 =====
    /** 通用参数错误，通常由 IllegalArgumentException 映射而来 */
    PARAM_ERROR(10000, "参数错误"),
    /** Bean Validation 校验失败，多个字段错误由处理器拼接后返回 */
    PARAM_VALIDATE_FAILED(10001, "参数校验失败"),
    /** 请求方法不被支持，本系统约定仅允许 POST */
    METHOD_NOT_ALLOWED(10002, "仅支持 POST 请求"),

    // ===== 业务错误 2xxxx：业务规则校验失败或数据状态异常 =====
    /** 通用业务处理失败，业务异常的默认码 */
    BUSINESS_ERROR(20000, "业务处理失败"),
    /** 查询的目标数据不存在 */
    DATA_NOT_FOUND(20001, "数据不存在"),
    /** 违反唯一性约束，数据已存在 */
    DATA_ALREADY_EXISTS(20002, "数据已存在"),

    // ===== 认证授权 4xxxx：登录态或权限问题 =====
    /** 未登录或登录态已过期 */
    UNAUTHORIZED(40100, "未登录或登录已过期"),
    /** 已登录但没有访问权限 */
    FORBIDDEN(40300, "没有权限访问"),
    /** 用户名或密码校验失败 */
    LOGIN_FAILED(40101, "用户名或密码错误"),
    USER_DISABLED(40102, "账号已被禁用，请联系管理员"),

    // ===== 系统错误 5xxxx：服务端未知异常，通常需要研发介入排查 =====
    /** 系统繁忙，对客户端隐藏具体异常细节 */
    SYSTEM_ERROR(50000, "系统繁忙，请稍后重试"),
    /** 兜底的未知异常码 */
    UNKNOWN_EXCEPTION(59999, "未知异常");

    /** 业务状态码，取值遵循上述分段约定 */
    private final int code;
    /** 默认提示信息，业务方抛异常时可用自定义 message 覆盖 */
    private final String message;
}
