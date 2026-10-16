package com.xrf.backend.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xrf.backend.mapper.AdminMapper;
import com.xrf.backend.mapper.NoteMapper;
import com.xrf.backend.pojo.Note;
import com.xrf.backend.pojo.PageBean;
import com.xrf.backend.service.AdminService;
import com.xrf.backend.vo.AdminDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private NoteMapper noteMapper;

    //分页查询所有笔记
    @Override
    public PageBean pageAllNotes(Integer page, Integer size, String keyword, String status) {
        PageHelper.startPage(page , size);
        List<Note> noteList = adminMapper.listAllNotes(keyword , status);
        for(Note note : noteList){
            note.setImages(noteMapper.listImages(note.getId()));
        }
        Page<Note> pageData = (Page<Note>) noteList;
        return new PageBean(
                pageData.getResult(),
                pageData.getTotal(),
                pageData.getPageNum(),
                pageData.getPageSize()
        );
    }

    //删除笔记
    @Override
    @Transactional
    public boolean deleteNote(Integer noteId) {
        Note note = noteMapper.getByNoteId(noteId);
        if(note == null){
            return false;
        }
        //把单个noteId包装为List
        List<Integer> noteIds = Collections.singletonList(noteId);
        noteMapper.deleteNoteComments(noteIds);
        noteMapper.deleteNoteImages(noteIds);
        noteMapper.deleteNoteLikes(noteIds);
        noteMapper.deleteNoteFavorites(noteIds);
        int affectedRows = noteMapper.adminDeleteNote(noteId);
        return affectedRows > 0;
    }

    //统计信息


    @Override
    public AdminDataVO getData() {
        return new AdminDataVO(
                adminMapper.countUsers(),
                adminMapper.countNotes(),
                adminMapper.countPublishedNotes(),
                adminMapper.countComments(),
                adminMapper.countLikes(),
                adminMapper.countFavorites()
        );
    }
}
