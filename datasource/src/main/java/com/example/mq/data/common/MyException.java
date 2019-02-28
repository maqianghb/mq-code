package com.example.mq.data.common;

import lombok.Data;

/**
 * @program: mq-code
 * @description: 自定义异常
 * @author: maqiang
 * @create: 2018-10-12 23:12
 */
@Data
public class MyException extends RuntimeException {

    private Integer code;
    private String desc;

	public MyException(String desc) {
		super(desc);
		this.code = -1;
		this.desc =desc;
	}

    public MyException(int code, String desc) {
		super(desc);
        this.code = code;
        this.desc = desc;
    }

    public MyException(int code, String desc, String message) {
        super(message);
        this.code = code;
        this.desc = desc;
    }

}
