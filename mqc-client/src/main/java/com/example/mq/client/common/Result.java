package com.example.mq.client.common;

import com.example.mq.client.enums.ResultStatusEnum;
import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-09-01 20:34
 */
@Data
public class Result<T> {

    private Boolean success;
    private Integer code;
    private String msg;
    private T data;
    private Integer totalCount;


	public static Result success(){
        Result result = new Result<>();
        result.success =true;
        result.code = ResultStatusEnum.SUCCESS.getCode();
        result.msg = ResultStatusEnum.SUCCESS.getDesc();
        return result;
	}

    public static Result success(String message){
        Result result = new Result<>();
        result.success =true;
        result.code = ResultStatusEnum.SUCCESS.getCode();
        result.msg = message;
        return result;
    }

    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>();
        result.success =true;
        result.code = ResultStatusEnum.SUCCESS.getCode();
        result.msg = ResultStatusEnum.SUCCESS.getDesc();
        result.data =data;
        return result;
    }

    public static <T> Result<T> success(T data, Integer totalCount){
        Result<T> result = new Result<>();
        result.success =true;
        result.code = ResultStatusEnum.SUCCESS.getCode();
        result.msg = ResultStatusEnum.SUCCESS.getDesc();
        result.data =data;
        result.totalCount= totalCount;
        return result;
    }


    public static Result fail(String message){
        Result result = new Result<>();
        result.success =false;
        result.code = ResultStatusEnum.FAIL.getCode();
        result.msg = message;
        return result;
    }

	public static Result fail(int code, String message) {
        Result result = new Result<>();
        result.success =false;
        result.code = code;
        result.msg = message;
        return result;
	}

}
