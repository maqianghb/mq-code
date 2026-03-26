package com.example.mq.domain.base;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseDomain implements Serializable {

    private Long id;

    private String createBy;

    private Date createDate;

    private String updateBy;

    private Date updateDate;

}
