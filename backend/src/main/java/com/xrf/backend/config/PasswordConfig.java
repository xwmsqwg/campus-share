package com.xrf.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean   // 方法返回的对象，交给 Spring 容器管理
    // PasswordEncoder：一个接口,它规定了密码加密的规范
    public PasswordEncoder passwordEncoder(){
        // 这个接口的具体实现
        return new BCryptPasswordEncoder();
    }

}
