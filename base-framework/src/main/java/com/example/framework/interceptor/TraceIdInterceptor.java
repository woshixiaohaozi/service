package com.example.framework.interceptor;

import com.example.common.constant.CommonConstant;
import com.example.common.core.TraceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 链路追踪拦截器：
 * 1. 从请求头读取 X-Trace-Id，缺失则生成；
 * 2. 写入 TraceContext 与 Slf4j MDC，日志自动带 traceId；
 * 3. 通过响应头回传 traceId；
 * 4. 请求结束清理 ThreadLocal，防止线程池串号。
 */
@Component
public class TraceIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader(CommonConstant.TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = TraceContext.generateTraceId();
        }
        TraceContext.setTraceId(traceId);
        MDC.put("traceId", traceId);
        response.setHeader(CommonConstant.TRACE_ID_HEADER, traceId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        TraceContext.clear();
        MDC.remove("traceId");
    }
}
