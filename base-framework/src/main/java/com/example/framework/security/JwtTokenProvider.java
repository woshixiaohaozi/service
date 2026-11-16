package com.example.framework.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 签发与解析工具。
 *
 * <p>采用 HMAC-SHA 对称签名：服务端用同一个密钥签名与验签。
 * 密钥从配置 jwt.secret 读取，生产环境务必通过环境变量注入足够长的随机串。</p>
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:base-framework-default-secret-key-must-be-at-least-32-chars}")
    private String secret;

    @Value("${jwt.expiration-ms:7200000}")
    private long expirationMs;

    /** 根据密钥字符串生成 HMAC 签名密钥。 */
    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** 签发 Token：subject 放用户名，设置签发时间与过期时间。 */
    public String generateToken(String username) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key())
                .compact();
    }

    /** 解析 Token 并取出用户名（subject）。 */
    public String getUsername(String token) {
        Claims claims = Jwts.parser().verifyWith(key()).build()
                .parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }

    /** 获取 Token 有效期（毫秒），登录接口据此返回 expiresIn。 */
    public long getExpirationMs() {
        return expirationMs;
    }

    /** 校验 Token 合法性（签名正确且未过期）。 */
    public boolean validate(String token) {
        try {
            getUsername(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
