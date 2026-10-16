package com.xrf.backend.Interceptor;

import com.alibaba.fastjson2.JSONObject;
import com.xrf.backend.pojo.Result;
import com.xrf.backend.utils.GetUserIdUtils;
import com.xrf.backend.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.regex.Pattern;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求方法和url
        String method = request.getMethod();
        String url = request.getRequestURI().substring(request.getContextPath().length());

        // 去除末尾自动补充的尾斜杠
        if (url.length() > 1 && url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        log.info("请求方法：{}，请求路径：{}", method, url);
        // 公开请求不要求登录。
        if (isPublicRequest(method, url)) {
            return true;
        }

        // 其他接口统一检查 token 请求头。
        String jwt = GetUserIdUtils.getToken(request);
        if(!StringUtils.hasLength(jwt)){
            log.info("token为空");
            writeError(response, 401, "未登录");
            return false;
        }

        // 解析 JWT
        Claims claims = jwtUtils.parseJWT(jwt);
        if (claims == null) {
            log.info("解析 jwt 失败");
            writeError(response, 401, "登录状态已失效");
            return false;
        }

        // admin鉴权
        if (isAdminRequest(url) && !isAdmin(claims)) {
            log.info("当前用户不是管理员，拒绝访问管理员接口");
            writeError(response, 403, "无管理员权限");
            return false;
        }

        return true;
    }

    private void writeError(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONObject.toJSONString(Result.error(status, message)));
    }

    //判断是否为管理员接口
    private boolean isAdminRequest(String url){
        return "/admin".equals(url) || url.startsWith("/admin/");
    }

    //解析jwt判断是否为admin
    private boolean isAdmin(Claims claims){
        String role = claims.get("role" , String.class);
        return "admin".equalsIgnoreCase(role);
    }

    //判断是否是公共请求
    private boolean isPublicRequest(String method, String url) {
        //含POST的注册和登录放行
        if ("POST".equalsIgnoreCase(method)
                && ("/auth/login".equals(url) || "/auth/register".equals(url))) {
            return true;
        }
        //非GET不放行
        if (!"GET".equalsIgnoreCase(method)) {
            return false;
        }
        //部分GET放行（正则表达式匹配）
        return Pattern.matches("/users/\\d+", url)           //完全匹配/users/（一个或多个数字）
                || Pattern.matches("/users/\\d+/notes", url) //完全匹配/users/（一个或多个数字）/notes
                || "/notes".equals(url)
                || Pattern.matches("/notes/\\d+", url)
                || Pattern.matches("/notes/\\d+/comments" , url);
    }
}
