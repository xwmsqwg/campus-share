package com.xrf.backend.utils;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

public class GetUserIdUtils {

    public static String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.isBlank()) {
            return null;
        }
        // 检查是否有Bearer认证方案前缀
        if (!authorization.startsWith("Bearer ")) {
            return null;
        }
        // 截取除Bearer外的token
        String token = authorization.substring(7).trim();
        return token.isEmpty() ? null : token;
    }

    public static Integer getUserId(HttpServletRequest request, JwtUtils jwtUtils) {
        String token = getToken(request);
        if (token == null) {
            return null;
        }
        Claims claims = jwtUtils.parseJWT(token);
        if (claims == null) {
            return null;
        }
        return claims.get("id", Integer.class);
    }
}
