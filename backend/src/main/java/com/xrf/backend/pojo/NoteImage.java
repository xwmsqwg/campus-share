package com.xrf.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteImage {
    private Integer id;
    private Integer noteId;
    private String imageUrl;
    private Integer sortOrder;
    private LocalDateTime createTime;
}
