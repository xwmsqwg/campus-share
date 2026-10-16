package com.xrf.backend.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface LikeMapper {

    //查询点赞状态
    @Select("select count(*) from like_record where user_id = #{userId} and note_id = #{noteId}")
    int countByUserAndNote(@Param("userId") Integer userId,
                           @Param("noteId") Integer noteId);

    @Select("select count(*) from like_record where note_id = #{noteId}")
    long countByNoteId(Integer noteId);

    //点赞
    @Insert("insert ignore into like_record (user_id, note_id) values (#{userId} , #{noteId})")
    int insert(@Param("userId") Integer userId,
               @Param("noteId") Integer noteId);

    //取消点赞
    @Delete("delete from like_record where user_id = #{userId} and note_id = #{noteId}")
    int delete(@Param("userId") Integer userId,
               @Param("noteId") Integer noteId);

}
