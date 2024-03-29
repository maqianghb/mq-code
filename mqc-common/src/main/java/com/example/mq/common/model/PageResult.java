package com.example.mq.common.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-12 22:28
 */
@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = -7208213675825333229L;

    private Integer pageNum;
    private Integer pageSize;
    private Long total;
    private List<T> list;

    public PageResult(int pageNum, int pageSize){
        this.pageNum =pageNum;
        this.pageSize =pageSize;
    }

    public PageResult(int pageNum, int pageSize, long total, List<T> list){
        this.pageNum =pageNum;
        this.pageSize =pageSize;
        this.total =total;
        this.list =list;
    }


}
