package com.xrf.backend.controller;

import com.xrf.backend.pojo.Result;
import com.xrf.backend.service.LikeService;
import com.xrf.backend.utils.GetUserIdUtils;
import com.xrf.backend.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/notes")
public class LikeController {

    @Autowired
    private LikeService likeService;
    @Autowired
    private JwtUtils jwtUtils;

    //查询点赞状态
    @GetMapping("/{id}/like/status")
    public Result getStatus(@PathVariable("id") Integer noteId , HttpServletRequest request){
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if (userId == null) {
            return Result.error("登录失效，请重新登录");
        }
        return Result.success(likeService.getStatus(userId , noteId));
    }

    //点赞
    @PostMapping("/{id}/like")
    public Result like(@PathVariable("id") Integer noteId , HttpServletRequest request){
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if(userId == null){
            return Result.error("登录失效，请重新登录");
        }
        return Result.success(likeService.like(userId , noteId));
    }

    //取消点赞
    @DeleteMapping("/{id}/like")
    public Result unlike(@PathVariable("id") Integer noteId, HttpServletRequest request) {
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if (userId == null) {
            return Result.error("登录失效，请重新登录");
        }
        return Result.success(likeService.unlike(userId , noteId));
    }
}
