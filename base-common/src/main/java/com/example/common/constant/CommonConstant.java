package com.example.common.constant;

/**
 * 全局通用常量集合，集中收纳跨模块复用的状态码、请求头名与默认配置值。
 *
 * <p>设计意图：把散落在各处的硬编码字面量收敛到一处，
 * 避免同一含义的常量在不同类中值不一致（例如分页上限）；
 * 以 final 类 + 私有构造的方式防止被实例化或继承。
 *
 * <p>使用场景：Controller / Filter 读取请求头名、构造响应时取成功码、
 * 分页参数校验取上限值、审计字段取系统用户标识等。
 */
public final class CommonConstant {

    // 私有构造：工具类不应被实例化
    private CommonConstant() {}

    /**
     * 业务成功状态码。约定为 0，与 {@code ErrorCode.SUCCESS} 的 code 一致，
     * {@code Result.isSuccess()} 也以此作为判定口径。
     */
    public static final int SUCCESS_CODE = 0;

    /**
     * 链路追踪请求头名称。Filter 从该 Header 读取或写入 traceId，
     * 使 traceId 能跨服务传递，串联分布式调用链。
     */
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    /**
     * 默认租户 ID（"0" 代表平台级/通用租户）。
     * 用于多租户场景下未显式指定租户时的兜底值。
     */
    public static final String DEFAULT_TENANT_ID = "0";

    /**
     * 系统操作者标识。定时任务或内部系统调用产生数据变更时，
     * 审计字段 createBy / updateBy 使用该值，区别于真实人工账号。
     */
    public static final String SYSTEM_USER = "system";

    /**
     * 分页每页条数上限（500）。与 {@code BaseQuery.pageSize} 的 {@code @Max} 校验值保持一致，
     * 防止一次性查询过多数据压垮数据库。
     */
    public static final int MAX_PAGE_SIZE = 500;
}
