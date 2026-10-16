package com.xrf.backend.service;

import com.xrf.backend.dto.RegisterRequest;
import com.xrf.backend.dto.UserDTO;
import com.xrf.backend.vo.UserVO;
import com.xrf.backend.pojo.*;

public interface UserService {

    //注册
    void register(RegisterRequest registerRequest);

    //登录
    User login(User user);

    //获取当前用户信息
    UserVO getUserVO(Integer id);

    //修改用户
    void updateUser(UserDTO userDTO);

    //用户笔记
    PageBean page(Integer id , Integer page , Integer size);

}
