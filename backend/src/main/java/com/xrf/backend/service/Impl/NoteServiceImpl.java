package com.xrf.backend.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xrf.backend.dto.CreateNoteDTO;
import com.xrf.backend.mapper.NoteMapper;
import com.xrf.backend.pojo.Note;
import com.xrf.backend.pojo.PageBean;
import com.xrf.backend.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteMapper noteMapper;

    //全站公开笔记分页查询
    @Override
    public PageBean page(Integer page, Integer size, String keyword) {
        PageHelper.startPage(page , size);
        List<Note> allNoteList = noteMapper.allList(keyword);
        for (Note note : allNoteList) {
            note.setImages(noteMapper.listImages(note.getId()));
        }
        Page<Note> p = (Page<Note>) allNoteList;
        PageBean pageBean = new PageBean(
                p.getResult(),      // 查询返回的记录集合
                p.getTotal(),       // 总记录数
                p.getPageNum(),     // 当前页码
                p.getPageSize()     // 每页条数
        );
        return pageBean;
    }

    //按笔记 ID 查一篇笔记的详情
    @Override
    public Note getByNoteId(Integer noteId) {
        Note note = noteMapper.getByNoteId(noteId);
        if (note != null) {
            note.setImages(noteMapper.listImages(noteId));
        }
        return note;
    }

    //创建笔记
    @Override
    public Note createNote(Integer userId, CreateNoteDTO createNoteDTO) {
        Note note = new Note();
        note.setUserId(userId);
        note.setTitle(createNoteDTO.getTitle());
        note.setSummary(createNoteDTO.getSummary());
        note.setContext(createNoteDTO.getContext());
        note.setStatus("已发布");
        note.setViewCount(0L);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        noteMapper.createNoteByUserId(note);
        return note;
    }

    //由笔记id（唯一）编辑笔记
    @Override
    public Note updateNote(Integer userId , Integer noteId, CreateNoteDTO createNoteDTO) {
        Note note = new Note();
        note.setUserId(userId);
        note.setId(noteId);
        note.setTitle(createNoteDTO.getTitle());
        note.setSummary(createNoteDTO.getSummary());
        note.setContext(createNoteDTO.getContext());
        note.setStatus("已发布");
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        int affectedRows = noteMapper.updateNoteByNoteId(note);
        if (affectedRows == 0) {
            return null;
        }
        return noteMapper.getByNoteId(noteId);
    }

    //删除当前用户的多篇笔记
    @Override
    @Transactional
    public int deleteNotes(Integer userId, List<Integer> noteIds) {
        if (userId == null || noteIds == null || noteIds.isEmpty()) {
            return 0;
        }
        List<Integer> ownedNoteIds = noteMapper.listOwnedNoteIds(userId, noteIds);
        if (ownedNoteIds == null || ownedNoteIds.isEmpty()) {
            return 0;
        }
        noteMapper.deleteNoteComments(ownedNoteIds);
        noteMapper.deleteNoteImages(ownedNoteIds);
        noteMapper.deleteNoteLikes(ownedNoteIds);
        noteMapper.deleteNoteFavorites(ownedNoteIds);
        return noteMapper.deleteNotes(userId, ownedNoteIds);
    }

    //判断是否是user的笔记
    @Override
    public boolean isMyNote(Integer userId, Integer noteId) {
        return noteMapper.isMyNote(userId , noteId) == 1;
    }

    //保存笔记URL
    @Override
    public void saveNoteImage(Integer noteId, String imageUrl) {
        noteMapper.saveNoteImage(noteId , imageUrl);
    }
}
