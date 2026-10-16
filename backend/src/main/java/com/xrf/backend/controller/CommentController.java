package com.xrf.backend.controller;

import com.xrf.backend.dto.CommentDTO;
import com.xrf.backend.pojo.Comment;
import com.xrf.backend.pojo.Result;
import com.xrf.backend.service.CommentService;
import com.xrf.backend.utils.GetUserIdUtils;
import com.xrf.backend.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtUtils jwtUtils;

    //查询评论
    @GetMapping("/notes/{id}/comments")
    public Result getCom(@PathVariable("id")Integer noteId){
        List<Comment> comments = commentService.getCom(noteId);
        return Result.success(comments);
    }

    //发表评论
    @PostMapping("/notes/{id}/comments")
    public Result postCom(HttpServletRequest request , @PathVariable("id")Integer noteId , @RequestBody CommentDTO commentDTO){
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if (userId == null) {
            return Result.error("登录失效，请重新登录");
        }
        if(commentDTO.getContent() == null || commentDTO.getContent().trim().isEmpty()){
            return Result.error("评论不能为空");
        }
        Comment comment = commentService.postCom(noteId , userId , commentDTO);
        return Result.success(comment);
    }

    //删除评论
    @DeleteMapping("/comments/{id}")
    public Result deleteCom(HttpServletRequest request , @PathVariable("id") Integer commentId){
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if (userId == null) {
            return Result.error("登录失效，请重新登录");
        }
        String token = GetUserIdUtils.getToken(request);
        Claims claims = jwtUtils.parseJWT(token);
        String role = claims == null ? null : claims.get("role", String.class);
        if("admin".equalsIgnoreCase(role)){
            boolean deleted = commentService.deleteByAdmin(commentId);
            if (!deleted) {
                return Result.error("评论不存在");
            }
            return Result.successMessage("评论删除成功");
        }
        boolean deleted = commentService.deleteByUser(commentId , userId);
        if (!deleted) {
            return Result.error("评论不存在或无权删除");
        }
        return Result.successMessage("评论删除成功");
    }

}
