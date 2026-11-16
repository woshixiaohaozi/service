package com.example.common.exception;

import com.example.common.core.ErrorCode;
import lombok.Getter;

/**
 * 业务异常，用于表达"可预期的、业务规则层面"的失败。
 *
 * <p>设计意图：把正常业务流程中主动抛出的失败（如数据不存在、状态不合法）
 * 与程序缺陷导致的系统异常区分开——前者是可预期的业务分支，
 * 使用本类携带业务错误码，由全局异常处理器转换为 {@code Result} 返回给前端。
 *
 * <p>使用场景：Service 层校验业务规则失败时抛出；
 * 需要区分不同错误码时使用带 code 或 ErrorCode 的构造器，
 * 简单场景可用 {@link #of(ErrorCode)} 快速构造。
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 业务错误码，取值遵循 {@link ErrorCode} 的分段约定。
     * 由全局异常处理器读取并回填到响应Result中。
     */
    private final int code;

    /**
     * 构造默认业务异常，使用 {@link ErrorCode#BUSINESS_ERROR} 的通用错误码。
     *
     * @param message 错误描述，会直接返回给调用方
     */
    public BusinessException(String message) {
        super(message);
        this.code = ErrorCode.BUSINESS_ERROR.getCode();
    }

    /**
     * 构造指定错误码的业务异常。
     *
     * @param code    业务错误码
     * @param message 错误描述
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 依据错误码枚举构造异常，code 与默认文案均取自枚举。
     *
     * @param errorCode 错误码枚举，不可为 null
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 依据错误码枚举构造异常，但用自定义 message 覆盖枚举的默认文案。
     * 适用于需要在通用错误码上附加具体上下文（如具体哪个 ID 不存在）的场景。
     *
     * @param errorCode 错误码枚举，不可为 null
     * @param message   自定义错误描述
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    /**
     * 静态工厂方法，按错误码枚举快速构造异常。
     *
     * @param errorCode 错误码枚举
     * @return 对应的业务异常实例
     */
    public static BusinessException of(ErrorCode errorCode) {
        return new BusinessException(errorCode);
    }
}
