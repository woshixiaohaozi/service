package com.example.common.core;

import com.example.common.constant.CommonConstant;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 全站统一的接口响应包装结构，泛型 T 代表业务数据类型。
 *
 * <p>设计意图：
 * <ul>
 *     <li>用固定结构（code / message / data / traceId / timestamp）替代裸对象或 Map，
 *         让前端只需一套解析逻辑即可处理所有接口；</li>
 *     <li>内置链路追踪 ID 与服务端时间戳，便于跨链路检索日志、定位问题；</li>
 *     <li>通过 {@link #isSuccess()} 提供统一的成功判定口径，避免各处硬编码状态码比较。</li>
 * </ul>
 *
 * <p>使用场景：所有 Controller 方法的返回值都应包装为本类；成功用 {@link #ok(Object)}
 * 系列工厂方法，失败用 {@link #fail(int, String)} 系列。异常场景由全局异常处理器
 * {@code GlobalExceptionHandler} 统一构造失败结果，业务代码一般不直接 new。
 *
 * @param <T> 业务数据的类型
 */
@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务状态码：0 表示成功，非 0 表示各类失败。
     * 取值遵循 {@link ErrorCode} 的分段约定（1xxxx 参数、2xxxx 业务、4xxxx 授权、5xxxx 系统）。
     */
    private int code;
    /**
     * 面向调用方的提示信息：成功时为固定文案 "success"，失败时为可读的错误描述。
     */
    private String message;
    /**
     * 实际业务数据。成功时承载返回值；失败时通常为 null，但允许携带部分补偿数据。
     */
    private T data;
    /**
     * 链路追踪 ID，取自当前线程的 {@link TraceContext}，用于把接口响应与服务端日志关联起来。
     */
    private String traceId;
    /**
     * 服务端生成响应的时间戳（毫秒），由工厂方法自动填充，便于排查时钟偏差与缓存问题。
     */
    private long timestamp;

    /**
     * 构造一个成功的空响应（不携带业务数据）。
     *
     * @param <T> 业务数据类型，由调用方上下文推断
     * @return code 为成功码、data 为 null 的响应实例
     */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /**
     * 构造一个携带业务数据的成功响应，自动写入 traceId 与服务器时间戳，调用方无需手动赋值。
     *
     * @param data 业务数据，可为 null
     * @param <T>  业务数据类型
     * @return 成功响应实例
     */
    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.setCode(CommonConstant.SUCCESS_CODE);
        r.setMessage("success");
        r.setData(data);
        // 从 ThreadLocal 取 traceId，保证返回给前端的 ID 与服务端日志中的一致
        r.setTraceId(TraceContext.getTraceId());
        // 时间统一由服务端生成，避免依赖客户端时钟
        r.setTimestamp(System.currentTimeMillis());
        return r;
    }

    /**
     * 构造一个失败的空响应（不携带附加数据）。
     *
     * @param code    业务错误码，建议取自 {@link ErrorCode}
     * @param message 错误描述
     * @param <T>     业务数据类型
     * @return 失败响应实例
     */
    public static <T> Result<T> fail(int code, String message) {
        return fail(code, message, null);
    }

    /**
     * 构造一个可携带附加数据的失败响应。
     *
     * @param code    业务错误码
     * @param message 错误描述
     * @param data    失败时希望一并返回给调用方的补偿数据，可为 null
     * @param <T>     业务数据类型
     * @return 失败响应实例
     */
    public static <T> Result<T> fail(int code, String message, T data) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(message);
        r.setData(data);
        // 与成功响应保持一致，失败时同样带上 traceId，方便用同一个 ID 检索全链路日志
        r.setTraceId(TraceContext.getTraceId());
        r.setTimestamp(System.currentTimeMillis());
        return r;
    }

    /**
     * 依据错误码枚举构造失败响应，code 与默认文案均取自枚举。
     *
     * @param errorCode 错误码枚举，不可为 null，否则抛 {@link NullPointerException}
     * @param <T>       业务数据类型
     * @return 失败响应实例
     */
    public static <T> Result<T> fail(ErrorCode errorCode) {
        return fail(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 判断当前响应是否代表业务成功。
     *
     * @return code 等于成功码时返回 true，否则返回 false
     */
    public boolean isSuccess() {
        return this.code == CommonConstant.SUCCESS_CODE;
    }
}
