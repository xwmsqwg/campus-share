package com.xrf.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeStatusVO {

    private boolean liked;
    private Long likeCount;

}
