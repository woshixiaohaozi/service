package com.example.job.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 定时任务总开关配置类。
 *
 * <p>设计意图：</p>
 * <ol>
 *   <li>通过 {@code @EnableScheduling} 开启 Spring 的定时任务调度能力，
 *       让容器中所有带 {@code @Scheduled} 的方法被调度器注册并执行。</li>
 *   <li>不使用 Spring Boot 默认的单线程调度器，而是自定义 {@link ThreadPoolTaskScheduler}。
 *       默认的单线程调度器存在两个问题：一是所有任务串行执行，
 *       某个耗时任务会阻塞其它任务；二是某个任务抛出未捕获异常会导致后续调度线程终止。
 *       自定义线程池可以隔离任务、提高吞吐，并且方便统一命名与优雅关闭。</li>
 *   <li>整个配置类用 {@code @ConditionalOnProperty} 控制：
 *       只有配置项 {@code job.scheduling.enabled=true}（缺省也视为 true）时，
 *       调度配置及 {@code @EnableScheduling} 才会生效。
 *       这样在关闭调度的环境（如本地开发、单元测试）中，任务完全不会被注册，
 *       避免测试时后台任务意外执行。</li>
 * </ol>
 *
 * <p>为什么要覆盖默认调度器 Bean：</p>
 * Spring 的 {@code ScheduledAnnotationBeanPostProcessor} 会按类型查找
 * {@code TaskScheduler} Bean；容器中一旦存在我们声明的 {@code ThreadPoolTaskScheduler}，
 * 所有 {@code @Scheduled} 任务都会使用它来调度，从而实现统一管控。
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "job.scheduling", name = "enabled", matchIfMissing = true)
public class SchedulingConfig {

    /**
     * 定义定时任务使用的线程池调度器。
     *
     * <p>参数设计说明：</p>
     * <ul>
     *   <li><b>poolSize = 4</b>：本模块目前只有两个示例任务，预留一定余量。
     *       设为 4 既能保证 DemoTask 的秒级高频任务与 DataCleanupTask 的每日批处理互不影响，
     *       又不会创建过多线程浪费资源。生产环境可根据任务数量和单任务耗时调整；
     *       经验值是「并发任务数 + 1~2 个冗余」，并考虑任务是否为 IO 密集型。</li>
     *   <li><b>threadNamePrefix = "base-job-"</b>：统一线程名前缀，
     *       便于在日志、线程 dump、监控（jstack / Arthas）中快速识别定时任务线程，
     *       与其它业务线程池（如 Web 请求线程）区分开。</li>
     *   <li><b>setWaitForTasksToCompleteOnShutdown(true)</b>：应用关闭（如 K8s 滚动发布、
     *       SIGTERM）时，调度器会等待正在执行的任务跑完再关闭线程池，
     *       避免清理类任务执行到一半被强杀，造成数据状态不一致。</li>
     *   <li><b>setAwaitTerminationSeconds(30)</b>：等待任务结束的最长秒数。
     *       与上一项配合：给优雅停机设一个上限，防止某个卡死的任务让应用永远停不下来；
     *       30 秒是兼顾数据完整性与发布效率的折中值，可按任务最长执行时间调整。</li>
     * </ul>
     *
     * @return 配置完毕的任务调度器，由 Spring 容器管理生命周期
     */
    @Bean(name = "taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4);
        scheduler.setThreadNamePrefix("base-job-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        // 允许设置线程名后由 Spring 在初始化时真正创建线程池
        scheduler.initialize();
        return scheduler;
    }
}
