package com.example.job.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 业务向定时任务示例：每天凌晨清理逻辑删除的过期数据。
 *
 * <p>设计意图：</p>
 * <ol>
 *   <li>演示一个真实业务场景的任务结构：计算时间窗口、调用清理逻辑、记录结果日志。
 *       当前用日志占位（log 输出将要清理的时间范围），不执行任何真实数据库操作，
 *       因此本模块无需依赖 base-system，也不会造成模块间循环依赖。</li>
 *   <li><b>为什么不能注入 base-system 的 Mapper：</b>
 *       依赖方向为 base-admin -> base-job -> base-framework -> base-common，
 *       而 base-system 与 base-job 是平级模块，base-job 的 pom 只依赖 base-framework。
 *       如果这里直接 import com.example.system.mapper.XxxMapper，
 *       就需要让 base-job 依赖 base-system；一旦 base-system 又需要引用任务能力，
 *       就会形成循环依赖导致构建失败。因此本模块保持「只依赖框架层」的干净边界。</li>
 *   <li><b>扩展方式（注释说明）：</b>
 *       若确实需要清理具体业务表，推荐两种做法之一：
 *       (a) 在 base-framework/base-common 中定义通用的清理接口（如 {@code DataCleaner}），
 *           由 base-system 提供实现并通过 Spring 注入到本任务；
 *       (b) 把具体的定时清理任务下沉到拥有该数据的业务模块（如 base-system）中，
 *           base-job 只保留通用调度能力与示例。
 *       这样可以维持单向依赖，避免循环。</li>
 *   <li>用 {@code @ConditionalOnProperty} 控制：{@code job.cleanup.enabled=false} 时
 *       任务不注册；缺省为 true（这是一个业务向任务示例，默认开启演示效果）。</li>
 * </ol>
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "job.cleanup", name = "enabled", matchIfMissing = true)
public class DataCleanupTask {

    /**
     * 每天凌晨 2 点执行清理。
     *
     * <p>cron 表达式 {@code "0 0 2 * * *"}（Spring 6 位：秒 分 时 日 月 周）：</p>
     * <ul>
     *   <li>{@code 0 0 2}：第 0 秒、第 0 分、第 2 小时，即每天 02:00:00；</li>
     *   <li>{@code * * *}：每天、每月、每周的每一天；</li>
     *   <li>{@code zone = "Asia/Shanghai"}：显式指定东八区。
     *       容器/服务器的默认时区可能是 UTC，不指定 zone 会导致任务在北京时间上午 10 点执行，
     *       而非期望的凌晨 2 点，这是生产环境定时任务的常见事故。</li>
     * </ul>
     *
     * <p>选在凌晨 2 点的原因：通常是一天中业务量最低的时段，
     * 大批量删除对在线用户和数据库主从同步的影响最小。</p>
     *
     * <p>注意：逻辑删除（deleted=1）的数据不宜立刻物理删除，
     * 通常保留 30~90 天以便审计和恢复；此处仅计算并打印时间窗口作为占位。</p>
     */
    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Shanghai")
    public void cleanupExpiredLogicDeletedData() {
        // 占位实现：计算 30 天前的时间点，表示将清理该时间点之前的逻辑删除数据
        long retainDays = 30L;
        long expiredBefore = System.currentTimeMillis() - retainDays * 24 * 60 * 60 * 1000L;
        log.info("DataCleanupTask 开始清理逻辑删除的过期数据, 清理 deleted=1 且删除时间早于 {} 的记录(占位, 未执行真实删除)", expiredBefore);

        // TODO 扩展点（避免循环依赖，故用注释说明）：
        // 1. 注入在 framework/common 层定义的通用 DataCleaner 接口；
        // 2. 由 base-system 实现该接口并注册为 Bean；
        // 3. 此处调用 cleaner.clean(expiredBefore) 并返回清理条数写入日志；
        // 4. 真正的物理删除建议分批进行（如每次 500 条循环），并开启事务，避免长事务锁表。

        log.info("DataCleanupTask 清理任务执行结束(占位)");
    }
}
