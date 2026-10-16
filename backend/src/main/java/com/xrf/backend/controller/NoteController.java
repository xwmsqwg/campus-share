package com.xrf.backend.controller;

import com.xrf.backend.dto.CreateNoteDTO;
import com.xrf.backend.dto.DeleteNoteDTO;
import com.xrf.backend.pojo.Note;
import com.xrf.backend.pojo.PageBean;
import com.xrf.backend.pojo.Result;
import com.xrf.backend.service.NoteService;
import com.xrf.backend.utils.AliOSSUtils;
import com.xrf.backend.utils.GetUserIdUtils;
import com.xrf.backend.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;
    @Autowired
    private AliOSSUtils aliOSSUtils;
    @Autowired
    private JwtUtils jwtUtils;

    //分页查询全站所有公开笔记
    @GetMapping
    public Result page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "9") Integer size,
            @RequestParam(required = false) String keyword ){
        PageBean pageBean = noteService.page(page , size , keyword);
        return Result.success(pageBean);
    }

    //按笔记 ID 查一篇笔记的详情
    @GetMapping("/{id}")
    public Result page(@PathVariable("id") Integer noteId){
            Note note = noteService.getByNoteId(noteId);
            if(note == null || !"已发布".equals(note.getStatus())){
                return Result.error("笔记不存在或未发布");
            }
            return Result.success(note);
    }

    // 进行可否修改的判断
    private boolean invalidNote(CreateNoteDTO createNoteDTO) {
        return createNoteDTO == null
                || createNoteDTO.getTitle() == null
                || createNoteDTO.getTitle().trim().isEmpty()
                || createNoteDTO.getSummary() == null
                || createNoteDTO.getSummary().trim().isEmpty()
                || createNoteDTO.getContext() == null
                || createNoteDTO.getContext().trim().isEmpty();
    }

    //创建笔记
    @PostMapping
    public Result createNote(HttpServletRequest request , @RequestBody CreateNoteDTO createNoteDTO){
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if(userId == null){
            return Result.error("登录失效，请重新登录");
        }
        if (invalidNote(createNoteDTO)) {
            return Result.error("标题、摘要和正文不能为空");
        }
        Note note = noteService.createNote(userId , createNoteDTO);
        return Result.success(note);
    }

    //由笔记id（唯一）编辑笔记
    @PutMapping("/{id}")
    public Result updateNote(@PathVariable("id") Integer noteId , HttpServletRequest request , @RequestBody CreateNoteDTO createNoteDTO){
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if(userId == null){
            return Result.error("登录失效，请重新登录");
        }
        if (invalidNote(createNoteDTO)) {
            return Result.error("标题、摘要和正文不能为空");
        }
        Note note = noteService.updateNote(userId , noteId , createNoteDTO);
        if(note == null){
            return Result.error("用户笔记不存在");
        }
        return Result.success(note);
    }

    //删除一篇或多篇当前用户的笔记
    @DeleteMapping
    public Result deleteNotes(HttpServletRequest request, @RequestBody DeleteNoteDTO deleteNoteDTO) {
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if (userId == null) {
            return Result.error("登录失效，请重新登录");
        }
        List<Integer> noteIds = deleteNoteDTO == null ? null : deleteNoteDTO.getIds();
        if (noteIds == null || noteIds.isEmpty()) {
            return Result.error("至少选择一篇笔记");
        }
        int affectedRows = noteService.deleteNotes(userId, noteIds);
        if (affectedRows == 0) {
            return Result.error("用户笔记不存在");
        }
        return Result.successMessage("成功删除" + affectedRows + "篇笔记");
    }

    //上传笔记图片
    @PostMapping("/{id}/images")
    public Result uploadImage(
            @PathVariable("id") Integer noteId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        Integer userId = GetUserIdUtils.getUserId(request, jwtUtils);
        if (userId == null) {
            return Result.error("登录失效，请重新登录");
        }
        if (file == null || file.isEmpty()) {
            return Result.error("请选择图片");
        }
        if (!noteService.isMyNote(userId, noteId)) {
            return Result.error("笔记不存在或无权上传图片");
        }
        try {
            String imageUrl = aliOSSUtils.upload(file);
            noteService.saveNoteImage(noteId, imageUrl);
            return Result.success(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            log.error("笔记图片上传失败，noteId={}", noteId, e);
            return Result.error("图片上传失败");
        }
    }

}
