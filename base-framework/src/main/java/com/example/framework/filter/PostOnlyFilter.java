package com.example.framework.filter;

import com.example.common.core.ErrorCode;
import com.example.common.core.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/** 全局 POST 约束：非 POST 直接返回 405 + 统一 Result，文档资源放行。 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PostOnlyFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public PostOnlyFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isExcluded(path) || HttpMethod.POST.matches(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        response.setStatus(405);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> body = Result.fail(ErrorCode.METHOD_NOT_ALLOWED);
        objectMapper.writeValue(response.getWriter(), body);
    }

    private boolean isExcluded(String path) {
        return path.startsWith("/doc.html") || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs") || path.startsWith("/webjars")
                || path.startsWith("/error");
    }
}
