package com.xrf.backend.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xrf.backend.dto.RegisterRequest;
import com.xrf.backend.dto.UserDTO;
import com.xrf.backend.exception.ApiException;
import com.xrf.backend.vo.UserVO;
import com.xrf.backend.mapper.NoteMapper;
import com.xrf.backend.mapper.UserMapper;
import com.xrf.backend.pojo.*;
import com.xrf.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //注册
    @Override
    public void register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername().trim();
        String password = registerRequest.getPassword();
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "密码不能超过 72 字节");
        }

        if (userMapper.findByUsername(username) != null) {
            throw new ApiException(HttpStatus.CONFLICT, "用户名已存在");
        }
        //创建user对象用于mapper层
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setGender(registerRequest.getGender() == null ? 0 : registerRequest.getGender());
        user.setImage(registerRequest.getImage());
        user.setNickname(registerRequest.getNickname());
        user.setProfile(registerRequest.getProfile());
        user.setRole("user");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            // 防止并发注册绕过前置查询
            throw new ApiException(HttpStatus.CONFLICT, "用户名已存在");
        }
    }

    //登录
    @Override
    public User login(User user) {
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser == null) {
            return null;
        }
        //匹配明文和数据库哈希
        boolean matched = passwordEncoder.matches(
                user.getPassword(),
                existUser.getPassword()
        );
        return matched ? existUser : null;
    }

    //获取当前用户信息
    @Override
    public UserVO getUserVO(Integer id) {
        return userMapper.getById(id);
    }

    //修改用户
    @Override
    public void updateUser(UserDTO userDTO) {
        int affectedRows = userMapper.updateUser(userDTO);
        if (affectedRows == 0) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "用户不存在或资料未发生变化"
            );
        }
    }

    //分页查询用户笔记
    @Override
    public PageBean page(Integer id, Integer page, Integer size) {
        PageHelper.startPage(page , size);
        List<Note> noteList = noteMapper.list(id);
        for (Note note : noteList) {
            note.setImages(noteMapper.listImages(note.getId()));
        }
        Page<Note> p = (Page<Note>) noteList;
        PageBean pageBean = new PageBean(
                p.getResult(),      // records
                p.getTotal(),       // total
                p.getPageNum(),     // page
                p.getPageSize()     // size
        );
        return pageBean;
    }
}
