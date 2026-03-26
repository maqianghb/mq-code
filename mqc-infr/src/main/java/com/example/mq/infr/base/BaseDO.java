package com.example.mq.infr.base;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: maqiang
 * @CreateTime: 2026-03-25 15:45:38
 * @Description:
 */
@Data
public class BaseDO implements Serializable {

    private Long id;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private Boolean isDeleted;

}
