package com.xrf.backend.service;

import com.xrf.backend.pojo.PageBean;
import com.xrf.backend.vo.FavoriteStatusVO;

public interface FavoriteService {

    // 查询当前用户是否收藏了这篇笔记
    FavoriteStatusVO getStatus(Integer userId, Integer noteId);

    //收藏笔记
    void favorite(Integer userId , Integer noteId);

    // 取消收藏
    void unfavorite(Integer userId, Integer noteId);

    // 查询当前用户收藏的笔记
    PageBean pageMyFavorites(Integer userId, Integer page, Integer size);

}
