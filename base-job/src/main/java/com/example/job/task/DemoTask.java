package com.example.job.task;

import com.example.common.core.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 演示型定时任务：每 10 秒打印一次带 traceId 的日志。
 *
 * <p>设计意图：</p>
 * <ol>
 *   <li>演示 Spring 自带 @Scheduled 的最简用法，不引入任何新依赖；</li>
 *   <li>演示如何为定时任务手动构造链路追踪上下文：</li>
 * </ol>
 * <p>定时任务没有 HTTP 请求入口，因此没有 TraceIdInterceptor 自动写入 traceId。
 * 这里手动生成 traceId 并写入 TraceContext 与 MDC，使任务日志同样具备可追踪性，
 * 并在 finally 中清理，防止线程池复用 ThreadLocal 造成串号。</p>
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "job.demo", name = "enabled", havingValue = "true")
public class DemoTask {

    /**
     * 每 10 秒执行一次。
     *
     * <p>cron 为 Spring 的 6 位格式，字段依次为：秒、分、时、日、月、周，
     * 与 Linux 的 5 位 crontab 不同。表达式含义：</p>
     * <ul>
     *   <li>第 1 位（秒）为「斜杠 10」，表示每 10 秒触发一次；</li>
     *   <li>第 2 至第 6 位均为星号，表示每分钟、每小时、每天、每月、每周都匹配。</li>
     * </ul>
     *
     * <p>三种触发方式对比：</p>
     * <ol>
     *   <li>cron：按日历时间触发，适合「每天凌晨 2 点」这类复杂调度；</li>
     *   <li>fixedRate：以上一次任务「开始时刻」为基准固定间隔触发，
     *       若单次执行超时会补跑，可能造成堆积；</li>
     *   <li>fixedDelay：以上一次任务「结束时刻」为基准再延迟固定毫秒，
     *       天然保证同一任务不并发执行，适合耗时波动大的串行任务。</li>
     * </ol>
     *
     * <p>注意：@Scheduled 方法必须无参、返回 void；
     * 同一定时任务中 fixedRate / fixedDelay / cron 三者只能选其一。</p>
     */
    @Scheduled(cron = "*/10 * * * * *")
    public void runEveryTenSeconds() {
        String traceId = TraceContext.generateTraceId();
        TraceContext.setTraceId(traceId);
        MDC.put("traceId", traceId);
        try {
            log.info("演示定时任务执行，当前时间: {}", java.time.LocalDateTime.now());
        } finally {
            TraceContext.clear();
            MDC.remove("traceId");
        }
    }
}
