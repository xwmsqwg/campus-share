package com.xrf.backend.service;

import com.xrf.backend.dto.CreateNoteDTO;
import com.xrf.backend.pojo.Note;
import com.xrf.backend.pojo.PageBean;

import java.util.List;

public interface NoteService {

    //笔记分页查询
    PageBean page(Integer page , Integer size , String keyword);

    //按笔记 ID 查一篇笔记的详情
    Note getByNoteId(Integer noteId);

    //创建笔记
    Note createNote(Integer userId , CreateNoteDTO createNoteDTO);

    //由笔记id（唯一）编辑笔记
    Note updateNote(Integer userId , Integer noteId , CreateNoteDTO createNoteDTO);

    //删除当前用户的多篇笔记
    int deleteNotes(Integer userId, List<Integer> noteIds);

    //判断是否是当前user的笔记
    boolean isMyNote(Integer userId , Integer noteId);

    //保存笔记URL
    void saveNoteImage(Integer noteId, String imageUrl);

}
