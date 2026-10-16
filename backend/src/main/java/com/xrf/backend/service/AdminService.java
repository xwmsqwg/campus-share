package com.xrf.backend.service;

import com.xrf.backend.pojo.PageBean;
import com.xrf.backend.vo.AdminDataVO;

public interface AdminService {

    //分页查询所有笔记
    PageBean pageAllNotes(Integer page , Integer size , String keyword , String status);

    //删除指定笔记
    boolean deleteNote(Integer noteId);

    //统计信息
    AdminDataVO getData();
}
