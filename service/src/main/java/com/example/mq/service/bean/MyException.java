package com.example.mq.service.bean;

import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-12 23:12
 */
@Data
public class MyException extends RuntimeException {

    private Integer code;
    private String desc;

    public MyException(Integer code) {
        this.code = code;
    }

    public MyException(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public MyException(String message, Integer code, String desc) {
        super(message);
        this.code = code;
        this.desc = desc;
    }

}
