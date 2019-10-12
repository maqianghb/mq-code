package com.example.mq.api.common;

import java.io.Serializable;

import com.example.mq.api.enums.RespStatusEnum;
import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-09-01 20:34
 */
@Data
public class Response<T> implements Serializable {
	private static final long serialVersionUID = 5947224547529405903L;

    private Boolean success;
    private Integer code;
    private String msg;
    private T data;

    public Response(boolean success, int code, String msg){
        this.success =success;
        this.code =code;
        this.msg =msg;
    }

    public Response(boolean success, int code, String msg, T data){
        this.success =success;
        this.code =code;
        this.msg =msg;
        this.data =data;
    }

    public static Response createBySuccessMsg(String message){
        return new Response(true, RespStatusEnum.SUCCESS.getCode(), message);
    }

	public static Response createBySuccess(){
		return createBySuccessMsg(RespStatusEnum.SUCCESS.getDesc());
	}

    public static <T> Response createBySuccess(T data){
        return new Response(true, RespStatusEnum.SUCCESS.getCode(), RespStatusEnum.SUCCESS.getDesc(), data);
    }

    public static Response createByFailMsg(String message){
        return new Response(true, RespStatusEnum.FAIL.getCode(), message);
    }

	public static Response createByFail(int code, String msg) {
		return new Response(false, code, msg);
	}

}
