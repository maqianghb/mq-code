package com.example.mq.client.base;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseDTO {

    private Long id;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

}
