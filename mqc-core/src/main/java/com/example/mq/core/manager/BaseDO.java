package com.example.mq.core.manager;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseDO implements Serializable {

    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    private String creator;
    private String operator;

    private Integer currentIndex;
    private Integer pageSize;

}
