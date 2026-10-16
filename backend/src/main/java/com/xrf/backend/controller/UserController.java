package com.xrf.backend.controller;

import com.xrf.backend.pojo.PageBean;
import com.xrf.backend.pojo.Result;
import com.xrf.backend.dto.UserDTO;
import com.xrf.backend.utils.AliOSSUtils;
import com.xrf.backend.utils.GetUserIdUtils;
import com.xrf.backend.vo.UserVO;
import com.xrf.backend.service.UserService;
import com.xrf.backend.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AliOSSUtils aliOSSUtils;

    //由id查询用户（可用于修改用户）
    @GetMapping("/{id}")
    public Result getUser(@PathVariable Integer id) {
        log.info("由id：{}查询user", id);
        UserVO userVO = userService.getUserVO(id);
        if (userVO == null) {
            return Result.error("用户不存在");
        }
        return Result.success(userVO);
    }

    //修改用户
    @PutMapping("/me")
    public Result updateUser(HttpServletRequest request, @RequestBody UserDTO userDTO) {
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if (userId == null) {
            return Result.error("令牌中没有用户信息");
        }
        boolean hasUpdate = userDTO.getGender() != null
                || userDTO.getImage() != null
                || (userDTO.getNickname() != null && !userDTO.getNickname().trim().isEmpty())
                || userDTO.getProfile() != null;
        if (!hasUpdate) {
            return Result.error("至少填写一项要修改的资料");
        }
        userDTO.setId(userId);
        userService.updateUser(userDTO);
        return Result.successMessage("资料修改成功");
    }

    //上传用户头像
    @PostMapping("/me/avatar")
    public Result uploadAvatar(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if (userId == null) {
            return Result.error("登录失效，请重新登录");
        }
        if (file == null || file.isEmpty()) {
            return Result.error("请选择头像图片");
        }
        try {
            String imageUrl = aliOSSUtils.upload(file);
            UserDTO userDTO = new UserDTO();
            userDTO.setId(userId);
            userDTO.setImage(imageUrl);
            userService.updateUser(userDTO);
            return Result.success(Map.of("image", imageUrl));
        } catch (Exception e) {
            log.error("用户头像上传失败，userId={}", userId, e);
            return Result.error("头像上传失败");
        }
    }

    //按用户 ID 查这个用户的公开笔记列表
    @GetMapping("/{id}/notes")
    public Result page(@PathVariable("id") Integer userId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "9") Integer size) {
        PageBean pageBean = userService.page(userId, page, size);
        return Result.success(pageBean);
    }

}
