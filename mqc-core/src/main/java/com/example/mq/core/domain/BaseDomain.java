package com.example.mq.core.domain;

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
    private Integer pageSize;
}
