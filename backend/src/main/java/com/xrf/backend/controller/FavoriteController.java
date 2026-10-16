package com.xrf.backend.controller;

import com.xrf.backend.pojo.PageBean;
import com.xrf.backend.pojo.Result;
import com.xrf.backend.service.FavoriteService;
import com.xrf.backend.utils.GetUserIdUtils;
import com.xrf.backend.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private JwtUtils jwtUtils;

    //查询当前用户是否收藏了这篇笔记
    @GetMapping("/notes/{id}/favorite/status")
    public Result getStatus(@PathVariable("id") Integer noteId , HttpServletRequest request){
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if (userId == null) {
            return Result.error("登录失效，请重新登录");
        }
        return Result.success(favoriteService.getStatus(userId , noteId));
    }

    //收藏笔记
    @PostMapping("/notes/{id}/favorite")
    public Result favorite(@PathVariable("id") Integer noteId , HttpServletRequest request) {
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if (userId == null) {
            return Result.error("登录失效，请重新登录");
        }
        favoriteService.favorite(userId, noteId);
        return Result.successMessage("收藏成功");
    }

    //取消收藏
    @DeleteMapping("/notes/{id}/favorite")
    public Result unfavorite(@PathVariable("id") Integer noteId, HttpServletRequest request) {
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if (userId == null) {
            return Result.error("登录失效，请重新登录");
        }
        favoriteService.unfavorite(userId, noteId);
        return Result.successMessage("取消收藏成功");
    }

    //分页查询当前用户的收藏
    @GetMapping("/users/me/favorites")
    public Result pageFavorites(
            @RequestParam(defaultValue = "1") Integer page ,
            @RequestParam(defaultValue = "9") Integer size ,
            HttpServletRequest request) {
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if(userId == null){
            return Result.error("登录失效，请重新登录");
        }
        PageBean pageBean = favoriteService.pageMyFavorites(userId , page , size);
        return Result.success(pageBean);
    }

}
