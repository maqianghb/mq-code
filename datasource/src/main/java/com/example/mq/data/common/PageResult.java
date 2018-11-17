package com.example.mq.data.common;

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

    private int pageNum;
    private int pageSize;
    private long total;
    private List<T> result;

    public PageResult(Integer pageNum, Integer pageSize){
        this.pageNum =pageNum;
        this.pageSize =pageSize;
    }

    public PageResult(Integer pageNum, Integer pageSize, Integer total, List<T> result){
        this.pageNum =pageNum;
        this.pageSize =pageSize;
        this.total =total;
        this.result =result;
    }


}
