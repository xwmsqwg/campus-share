package com.xrf.backend.mapper;

import com.xrf.backend.pojo.Note;
import com.xrf.backend.pojo.NoteImage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NoteMapper {

    //当前用户笔记分页查询
    List<Note> list (Integer userId);

    //全站公开笔记分页查询
    List<Note> allList(String keyword);

    //查询笔记关联图片
    @Select("select id, note_id, image_url, sort_order, create_time from note_image where note_id = #{noteId} order by sort_order")
    List<NoteImage> listImages(Integer noteId);

    //按笔记 ID 查一篇笔记的详情
    @Select("select * from note where id = #{noteId}")
    Note getByNoteId(Integer noteId);

    //创建笔记
    int createNoteByUserId(Note note);

    //由笔记id（唯一）编辑笔记
    int updateNoteByNoteId(Note note);

    //批量删除当前用户的笔记
    //@Param是防止 MyBatis 会默认将多个参数封装成一个 Map
    int deleteNotes(@Param("userId") Integer userId,
                    @Param("noteIds") List<Integer> noteIds);

    //查询当前用户实际拥有的笔记 ID
    List<Integer> listOwnedNoteIds(@Param("userId") Integer userId,
                                   @Param("noteIds") List<Integer> noteIds);

    //删除笔记关联评论
    int deleteNoteComments(@Param("noteIds") List<Integer> noteIds);

    //删除笔记关联图片记录
    int deleteNoteImages(@Param("noteIds") List<Integer> noteIds);

    //删除笔记关联点赞
    int deleteNoteLikes(@Param("noteIds") List<Integer> noteIds);

    //删除笔记关联收藏
    int deleteNoteFavorites(@Param("noteIds") List<Integer> noteIds);

    //判断是不是user的笔记
    @Select("select count(*) from note where id = #{noteId} and user_id = #{userId}")
    int isMyNote(@Param("userId") Integer userId,
                 @Param("noteId") Integer noteId);

    //保存笔记url
    @Insert("insert into note_image (note_id , image_url) values (#{noteId} , #{imageUrl})")
    int saveNoteImage(@Param("noteId") Integer noteId,
                      @Param("imageUrl") String imageUrl);

    //admin删除笔记
    int adminDeleteNote(Integer noteId);

}
