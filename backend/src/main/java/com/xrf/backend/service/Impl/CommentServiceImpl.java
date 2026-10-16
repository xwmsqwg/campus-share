package com.xrf.backend.service.Impl;

import com.xrf.backend.dto.CommentDTO;
import com.xrf.backend.exception.ApiException;
import com.xrf.backend.mapper.CommentMapper;
import com.xrf.backend.mapper.NoteMapper;
import com.xrf.backend.pojo.Comment;
import com.xrf.backend.pojo.Note;
import com.xrf.backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private NoteMapper noteMapper;

    //查询评论
    @Override
    public List<Comment> getCom(Integer noteId) {
        Note note = noteMapper.getByNoteId(noteId);
        if (note == null || !"已发布".equals(note.getStatus())) {
            throw new ApiException(
                    HttpStatus.NOT_FOUND,
                    "笔记不存在或未发布"
            );
        }
        return commentMapper.getComByNoteId(noteId);
    }

    //新增评论
    @Override
    public Comment postCom(Integer noteId, Integer userId, CommentDTO commentDTO) {
        Note note = noteMapper.getByNoteId(noteId);
        if (note == null || !"已发布".equals(note.getStatus())) {
            throw new ApiException(
                    HttpStatus.NOT_FOUND,
                    "笔记不存在或未发布"
            );
        }
        Comment comment = new Comment();
        comment.setNoteId(noteId);
        comment.setUserId(userId);
        comment.setContent(commentDTO.getContent());
        comment.setStatus("正常");
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        commentMapper.postCom(comment);
        return commentMapper.getByComId(comment.getId());
    }

    //admin删除评论
    @Override
    public boolean deleteByAdmin(Integer commentId) {
        int rows = commentMapper.deleteByAdmin(commentId);
        return rows > 0;
    }

    //user删除评论
    @Override
    public boolean deleteByUser(Integer commentId, Integer userId) {
        int rows = commentMapper.deleteByUser(commentId , userId);
        return rows > 0;
    }
}
