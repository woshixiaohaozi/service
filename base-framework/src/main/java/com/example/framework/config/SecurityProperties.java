package com.example.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全白名单配置属性，对应配置文件中的 security 节点。
 *
 * <p>分为两类白名单，职责互不混淆：</p>
 * <ol>
 *   <li>{@code ignore-urls}：免登录白名单，这些路径无需携带 JWT 即可访问；</li>
 *   <li>{@code allow-method-urls}：POST 约束白名单，这些路径允许使用非 POST 方法
 *       （如 Swagger 文档、错误页等需要 GET 的资源）。</li>
 * </ol>
 * <p>两者都支持 Ant 风格通配符，例如 /public/**、/auth/*。
 * 注意：白名单只放开 URL 层认证，Controller 上的 @PreAuthorize 注解鉴权仍然生效。</p>
 */
@Data
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /** 免登录白名单：无需 JWT 认证即可访问的路径。 */
    private List<String> ignoreUrls = new ArrayList<>();

    /** POST 约束白名单：允许非 POST 方法访问的路径。 */
    private List<String> allowMethodUrls = new ArrayList<>();
}
