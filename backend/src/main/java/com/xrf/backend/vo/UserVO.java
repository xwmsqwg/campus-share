package com.xrf.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    private Integer id;
    private String username;
    private Integer gender;
    private String image;
    private String nickname;
    private String profile;
    private String role;
}
