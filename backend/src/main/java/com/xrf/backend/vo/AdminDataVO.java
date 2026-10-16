package com.xrf.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDataVO {

    // 用户总数
    private Long userCount;
    // 笔记总数，包含不同状态的笔记
    private Long noteCount;
    // 已发布笔记数量
    private Long publishedNoteCount;
    // 评论记录总数
    private Long commentCount;
    // 点赞记录总数
    private Long likeCount;
    // 收藏记录总数
    private Long favoriteCount;

}
