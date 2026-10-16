package com.xrf.backend.service.Impl;

import com.xrf.backend.mapper.LikeMapper;
import com.xrf.backend.service.LikeService;
import com.xrf.backend.utils.CheckNoteUtils;
import com.xrf.backend.vo.LikeStatusVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private CheckNoteUtils checkNoteUtils;

    //查询点赞状态
    @Override
    public LikeStatusVO getStatus(Integer userId, Integer noteId) {
        checkNoteUtils.checkNote(noteId);
        boolean liked = likeMapper.countByUserAndNote(userId, noteId) > 0;
        long likeCount = likeMapper.countByNoteId(noteId);
        return new LikeStatusVO(liked , likeCount);
    }

    //点赞
    @Override
    public LikeStatusVO like(Integer userId, Integer noteId) {
        checkNoteUtils.checkNote(noteId);
        likeMapper.insert(userId , noteId);
        return getStatus(userId , noteId);
    }

    //取消点赞
    @Override
    public LikeStatusVO unlike(Integer userId, Integer noteId) {
        checkNoteUtils.checkNote(noteId);
        likeMapper.delete(userId , noteId);
        return getStatus(userId , noteId);
    }
}
