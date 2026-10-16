package com.xrf.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Integer id;
    private Integer noteId;
    private Integer userId;
    private String content;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updatedAt;
    private String nickname;
    private String image;
}
