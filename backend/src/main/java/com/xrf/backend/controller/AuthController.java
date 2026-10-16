package com.xrf.backend.controller;

import com.xrf.backend.dto.RegisterRequest;
import com.xrf.backend.pojo.Result;
import com.xrf.backend.pojo.User;
import com.xrf.backend.utils.GetUserIdUtils;
import com.xrf.backend.vo.UserVO;
import com.xrf.backend.service.UserService;
import com.xrf.backend.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    //用户注册
    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterRequest registerRequest){
        log.info("用户注册：username={}, nickname={}",
                registerRequest.getUsername(),
                registerRequest.getNickname());
        userService.register(registerRequest);
        return Result.successMessage("注册成功");
    }

    //用户登录
    @PostMapping("/login")
    public Result login(@RequestBody User user){
        log.info("用户登录：{}",user.getUsername());
        User u = userService.login(user);
        if (u != null) {
            //把用户 id、昵称、用户名放进 JWT 的 claims 中
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", u.getId());
            claims.put("username", u.getUsername());
            claims.put("nickname", u.getNickname());
            claims.put("image", u.getImage());
            claims.put("role", u.getRole());
            String jwt = jwtUtils.generateJwt(claims);
            Map<String , Object> data = new HashMap<>();
            data.put("token" , jwt);
            data.put("user",claims);
            return Result.success(data);
        } else {
            return Result.error("用户名或密码错误");
        }
    }

    //获取当前用户信息
    @GetMapping("/me")
    public Result getUser(HttpServletRequest request){
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if(userId == null){
            return Result.error("令牌中没有用户信息");
        }
        UserVO userVO = userService.getUserVO(userId);
        if(userVO == null){
            return Result.error("用户不存在");
        }
        return Result.success(userVO);
    }

}
