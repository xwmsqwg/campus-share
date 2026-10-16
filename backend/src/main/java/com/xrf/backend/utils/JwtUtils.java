package com.xrf.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    @Autowired
    private JwtProperties jwtProperties;

    //生成Jwt
    public String generateJwt(Map<String, Object> claims) {
        //读取配置文件
        String secret = jwtProperties.getSecret();
        long expirationMs = jwtProperties.getExpirationMs();
        // 用配置里的 secret 字符串转成 SecretKey
        SecretKey KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .addClaims(claims) // 自定义信息
                .signWith(KEY, Jwts.SIG.HS256)
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .compact();
    }
    // 解析 JWT，失败返回 null
    public Claims parseJWT(String jwt) {
        String secret = jwtProperties.getSecret();
        SecretKey KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        try {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }
}
