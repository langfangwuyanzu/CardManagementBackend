package com.yindi.card.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 适配 jjwt 0.11.x 的最小可跑版本
 */
public class JwtUtil {

    private final SecretKey secretKey;
    private final long ttlMillis;

    public JwtUtil(String secret, long ttlMillis) {
        // 建议 >= 32 bytes
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttlMillis = ttlMillis;
    }

    /** 生成 Token: subject + roles + 过期时间 */
    public String generate(String subject, List<String> roles) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(Map.of("roles", roles))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttlMillis))
                // 0.11.x 用法：signWith(key, SignatureAlgorithm)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 解析并校验签名/过期，返回 Claims；失败抛异常 */
    public Claims parse(String token) {
        // 0.11.x 用法：parserBuilder().setSigningKey(key)...
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
