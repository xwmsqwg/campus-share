package com.xrf.backend.service;

import com.xrf.backend.dto.CommentDTO;
import com.xrf.backend.pojo.Comment;

import java.util.List;

public interface CommentService {

    //查询评论
    List<Comment> getCom(Integer noteId);

    //新增评论
    Comment postCom(Integer noteId , Integer userId , CommentDTO commentDTO);

    //admin删除评论
    boolean deleteByAdmin(Integer commentId);

    //user删除评论
    boolean deleteByUser(Integer commentId, Integer userId);

}
