package com.xrf.backend.mapper;

import com.xrf.backend.pojo.Note;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminMapper {

    //查询所有笔记
    List<Note> listAllNotes(@Param("keyword") String keyword,
                           @Param("status") String status);

    // 查询用户总数
    long countUsers();

    // 查询笔记总数
    long countNotes();

    // 查询已发布笔记数量
    long countPublishedNotes();

    // 查询评论总数
    long countComments();

    // 查询点赞记录总数
    long countLikes();

    // 查询收藏记录总数
    long countFavorites();

}
