package com.xrf.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2 , max = 20 , message = "用户名长度必须为2-20个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 72, message = "密码长度必须为 8-72 个字符")
    private String password;

    private Integer gender;
    private String image;

    @NotBlank(message = "昵称不能为空")
    @Size(min = 2 , max = 50, message = "昵称长度必须为 2-50 个字符")
    private String nickname;

    @Size(max = 500, message = "个人简介不能超过 500 个字符")
    private String profile;
}
