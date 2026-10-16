package com.xrf.backend.utils;

import com.xrf.backend.mapper.NoteMapper;
import com.xrf.backend.pojo.Note;
import com.xrf.backend.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CheckNoteUtils {

    @Autowired
    private NoteMapper noteMapper;

    public void checkNote(Integer noteId) {
        Note note = noteMapper.getByNoteId(noteId);

        if (note == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "笔记不存在");
        }

        if (!"已发布".equals(note.getStatus())) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "笔记未发布，不能进行收藏操作"
            );
        }
    }

}
