package com.xrf.backend.controller;

import com.xrf.backend.pojo.PageBean;
import com.xrf.backend.pojo.Result;
import com.xrf.backend.service.AdminService;
import com.xrf.backend.vo.AdminDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 分页查询全部笔记
    @GetMapping("/notes")
    public Result pageNote(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "9") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status){
        PageBean pageBean = adminService.pageAllNotes(page , size , keyword , status);
        return Result.success(pageBean);
    }

    //删除指定笔记
    @DeleteMapping("/notes/{id}")
    public Result deleteNote(@PathVariable("id") Integer noteId){
        boolean deleted = adminService.deleteNote(noteId);
        if(!deleted){
            return Result.error("笔记不存在");
        }
        return Result.successMessage("笔记删除成功");
    }

    //统计用户信息
    @GetMapping("/statistics")
    public Result getData(){
        AdminDataVO adminDataVO = adminService.getData();
        return Result.success(adminDataVO);
    }

}
