package com.example.mq.core.domain.base;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseDomain implements Serializable {

    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    private String creator;
    private String operator;

    private Integer currentPage;
    private Integer currentIndex;
    private Integer pageSize;

}
