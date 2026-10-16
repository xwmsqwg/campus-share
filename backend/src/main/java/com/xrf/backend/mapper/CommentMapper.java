package com.xrf.backend.mapper;

import com.xrf.backend.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    //由笔记查询查询评论
    List<Comment> getComByNoteId(Integer noteId);

    //新增评论
    int postCom(Comment comment);

    //admin删除评论
    int deleteByAdmin(
            @Param("commentId") Integer commentId
    );

    //user删除评论
    int deleteByUser(
            @Param("commentId") Integer commentId,
            @Param("userId") Integer userId
    );

    //有评论Id查询评论
    Comment getByComId(Integer commentId);

}
