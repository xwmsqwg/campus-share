package com.xrf.backend.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xrf.backend.mapper.FavoriteMapper;
import com.xrf.backend.mapper.NoteMapper;
import com.xrf.backend.pojo.Note;
import com.xrf.backend.pojo.PageBean;
import com.xrf.backend.service.FavoriteService;
import com.xrf.backend.utils.CheckNoteUtils;
import com.xrf.backend.vo.FavoriteStatusVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private CheckNoteUtils checkNoteUtils;

    // 查询当前用户是否收藏了这篇笔记
    @Override
    public FavoriteStatusVO getStatus(Integer userId, Integer noteId) {
        checkNoteUtils.checkNote(noteId);
        boolean favorite = favoriteMapper.countByUserAndNote(userId , noteId) > 0;
        return new FavoriteStatusVO(favorite);
    }

    // 收藏笔记
    @Override
    public void favorite(Integer userId, Integer noteId) {
        checkNoteUtils.checkNote(noteId);
        favoriteMapper.insert(userId , noteId);
    }

    // 取消收藏
    @Override
    public void unfavorite(Integer userId, Integer noteId) {
        checkNoteUtils.checkNote(noteId);
        favoriteMapper.delete(userId , noteId);
    }

    // 查询当前用户收藏的笔记
    @Override
    public PageBean pageMyFavorites(Integer userId, Integer page, Integer size) {
        PageHelper.startPage(page , size);
        List<Note> noteList = favoriteMapper.pageMyFavorites(userId);
        Page<Note> pageData = (Page<Note>) noteList;
        for (Note note : noteList) {
            note.setImages(noteMapper.listImages(note.getId()));
        }
        return new PageBean(
                pageData.getResult(),
                pageData.getTotal(),
                pageData.getPageNum(),
                pageData.getPageSize()
        );
    }

}
