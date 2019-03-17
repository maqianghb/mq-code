package com.example.mq.base.common;

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
    private List<T> result;

    public PageResult(int pageNum, int pageSize){
        this.pageNum =pageNum;
        this.pageSize =pageSize;
    }

    public PageResult(int pageNum, int pageSize, long total, List<T> result){
        this.pageNum =pageNum;
        this.pageSize =pageSize;
        this.total =total;
        this.result =result;
    }


}
