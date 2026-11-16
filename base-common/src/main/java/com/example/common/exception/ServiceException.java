package com.example.common.exception;

/**
 * 带 HTTP 状态码语义的服务异常，是 {@link BusinessException} 的子类。
 *
 * <p>设计意图：认证/授权等场景除了业务错误码，还需要让响应携带特定的 HTTP 状态
 * （如 401 未登录、403 无权限），因此在父类基础上扩展 status 字段。
 *
 * <p>使用场景：安全框架或认证过滤器判定登录态失效、权限不足时抛出；
 * 全局异常处理器按父类 BusinessException 捕获后会把 code 与 message 写入 Result，
 * status 则由调用方（如过滤器）决定如何映射到 HTTP 响应。
 */
public class ServiceException extends BusinessException {

    /** HTTP 状态码，如 401（未授权）、403（禁止访问） */
    private final int status;

    /**
     * 构造带 HTTP 状态的服务异常，业务码使用 {@code BusinessException} 的默认通用业务码。
     *
     * @param status  HTTP 状态码
     * @param message 错误描述
     */
    public ServiceException(int status, String message) {
        super(message);
        this.status = status;
    }

    /**
     * 构造同时指定 HTTP 状态码与业务错误码的服务异常。
     *
     * @param status  HTTP 状态码
     * @param code    业务错误码
     * @param message 错误描述
     */
    public ServiceException(int status, int code, String message) {
        super(code, message);
        this.status = status;
    }

    /**
     * 获取 HTTP 状态码。
     *
     * @return 构造时传入的 HTTP 状态码（如 401、403）
     */
    public int getStatus() {
        return status;
    }
}
