package com.example.framework.config;

import com.example.framework.security.JwtAuthenticationFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 核心配置。
 *
 * <p>职责：</p>
 * <ol>
 *   <li>配置 DaoAuthenticationProvider：查库加载用户 + BCrypt 校验密码；</li>
 *   <li>暴露 AuthenticationManager 供登录接口调用；</li>
 *   <li>从 {@link SecurityProperties} 读取免登录白名单，不再硬编码路径；</li>
 *   <li>无状态会话 + JWT 过滤器，自定义 401/403 统一错误响应。</li>
 * </ol>
 */
@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final UserDetailsService userDetailsService;
    private final SecurityProperties securityProperties;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter, UserDetailsService userDetailsService,
                          SecurityProperties securityProperties) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
        this.securityProperties = securityProperties;
    }

    /** DaoAuthenticationProvider：调用 UserDetailsService 查库 + BCrypt 比对密码。 */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /** 暴露 AuthenticationManager 给登录 Controller 调用 authenticate()。 */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(daoAuthenticationProvider());
    }

    /** BCrypt 单向哈希加密，每次加密自动加盐。 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 过滤链：按配置放行免登录白名单，其余请求一律需要认证。
     * 白名单为空时不调用 requestMatchers，避免 Spring Security 因空数组报错。
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                if (!securityProperties.getIgnoreUrls().isEmpty()) {
                    auth.requestMatchers(securityProperties.getIgnoreUrls().toArray(String[]::new)).permitAll();
                }
                auth.anyRequest().authenticated();
            })
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> {
                    res.setStatus(401);
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"code\":40100,\"message\":\"未登录或登录已过期\"}");
                })
                .accessDeniedHandler((req, res, ex) -> {
                    res.setStatus(403);
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"code\":40300,\"message\":\"没有权限访问\"}");
                }))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
