package com.example.common.core;

import java.util.UUID;

/**
 * 轻量级链路追踪（traceId）上下文，基于 {@link ThreadLocal} 在单次请求线程内传递追踪标识。
 *
 * <p>设计意图：
 * <ul>
 *     <li>由框架的 TraceIdFilter 在请求入口调用 {@link #setTraceId(String)} 写入，
 *         业务代码只需通过 {@link #getTraceId()} 读取，无需感知写入时机；</li>
 *     <li>刻意使用 ThreadLocal 而非 Slf4j MDC，使 base-common 保持零日志实现依赖，
 *         避免把 logback / log4j 绑定到公共模块；</li>
 *     <li>请求结束时必须调用 {@link #clear()}，否则线程池复用场景下会出现串数据甚至内存泄漏。</li>
 * </ul>
 *
 * <p>使用场景：全局异常处理器打印日志、{@link Result} 填充 traceId，
 * 以及任何需要把一次请求的多条日志串联起来的位置。
 */
public final class TraceContext {

    // 用 ThreadLocal 将 traceId 与当前线程绑定，天然隔离并发请求
    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    // 工具类，禁止实例化
    private TraceContext() {}

    /**
     * 在当前线程写入追踪 ID。通常由入口 Filter 调用，业务代码一般无需调用。
     *
     * @param traceId 追踪 ID，建议由 {@link #generateTraceId()} 生成
     */
    public static void setTraceId(String traceId) {
        TRACE_ID.set(traceId);
    }

    /**
     * 读取当前线程的追踪 ID。
     *
     * @return 当前 traceId；若尚未设置则返回空串，省去调用方的 null 判断
     */
    public static String getTraceId() {
        String id = TRACE_ID.get();
        // 返回空串而不是 null，简化日志拼接与响应填充处的空值处理
        return id == null ? "" : id;
    }

    /**
     * 生成新的追踪 ID：去掉 UUID 中的连字符，得到 32 位紧凑字符串。
     *
     * @return 32 位无连字符的随机字符串
     */
    public static String generateTraceId() {
        // 去连字符可缩短长度，并规避部分日志系统或 HTTP 头对连字符的处理差异
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 清除当前线程的追踪 ID。必须在请求收尾时调用，
     * 防止线程池复用时上一次请求的 traceId 被下一次请求读取到。
     */
    public static void clear() {
        // 用 remove 而非 set(null)，可避免 ThreadLocalMap 中残留无效的弱引用条目
        TRACE_ID.remove();
    }
}
