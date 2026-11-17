package com.example.framework.filter;

import com.example.common.core.ErrorCode;
import com.example.common.core.Result;
import com.example.framework.config.SecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 全局 POST 约束过滤器。
 *
 * <p>全站接口统一 POST；非 POST 请求直接返回 405 + 统一 Result。</p>
 * <p>允许使用非 POST 方法的路径从配置 {@code security.allow-method-urls} 读取，
 * 支持 Ant 风格通配符（如 /public/**），新增文档或回调接口时无需改代码。</p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PostOnlyFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final SecurityProperties securityProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public PostOnlyFilter(ObjectMapper objectMapper, SecurityProperties securityProperties) {
        this.objectMapper = objectMapper;
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getServletPath();
        if (path == null || path.isEmpty()) {
            path = request.getRequestURI();
        }
        if (isExcluded(path) || HttpMethod.POST.matches(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        response.setStatus(405);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> body = Result.fail(ErrorCode.METHOD_NOT_ALLOWED);
        objectMapper.writeValue(response.getWriter(), body);
    }

    /** 判断当前路径是否命中 POST 约束白名单（Ant 风格匹配）。 */
    private boolean isExcluded(String path) {
        return securityProperties.getAllowMethodUrls().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
