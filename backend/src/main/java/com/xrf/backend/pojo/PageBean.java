package com.xrf.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageBean {
    //当前页的实际数据列表。
    private List records;
    //符合查询条件的总记录数
    private long total;
    //当前请求的是第几页
    private Integer page;
    //每页显示多少条数据
    private Integer size;
}
