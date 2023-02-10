package com.example.mq.client.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseDTO implements Serializable {

    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    private String operator;

    private String creator;

}
