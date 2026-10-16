package com.xrf.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String password;
    private Integer gender;
    private String image;
    private String nickname;
    private String profile;
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
