package com.xrf.backend.mapper;

import com.xrf.backend.pojo.Note;
import org.apache.ibatis.annotations.*;


import java.util.List;

@Mapper
public interface FavoriteMapper {

    //查询当前用户是否有收藏笔记
    @Select("select count(*) from favorite_record where user_id = #{userId} and note_id = #{noteId}")
    int countByUserAndNote(@Param("userId") Integer userId,
                           @Param("noteId") Integer noteId);

    // 收藏笔记
    @Insert("insert ignore into favorite_record (user_id, note_id) values (#{userId} , #{noteId})")
    int insert(@Param("userId") Integer userId,
               @Param("noteId") Integer noteId);

    // 取消收藏
    @Delete("delete from favorite_record where user_id = #{userId} and note_id = #{noteId}")
    int delete(@Param("userId") Integer userId,
               @Param("noteId") Integer noteId);

    // 查询当前用户收藏的笔记
    List<Note> pageMyFavorites(Integer userId);
}
