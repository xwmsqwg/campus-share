package com.xrf.backend.service;

import com.xrf.backend.vo.LikeStatusVO;

public interface LikeService {

    //查询点赞状态
    LikeStatusVO getStatus(Integer userId, Integer noteId);

    //点赞
    LikeStatusVO like(Integer userId, Integer noteId);

    //取消点赞
    LikeStatusVO unlike(Integer userId, Integer noteId);

}
