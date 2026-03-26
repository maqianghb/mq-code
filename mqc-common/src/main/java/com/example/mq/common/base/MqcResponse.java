package com.example.mq.common.base;

import com.example.mq.common.enums.base.ResultStatusEnum;
import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-09-01 20:34
 */
@Data
public class MqcResponse<T> {

    private Boolean success;
    private Integer code;
    private String msg;
    private T data;


	public static MqcResponse success(){
        MqcResponse response = new MqcResponse<>();
        response.setSuccess(true);
        response.setCode(ResultStatusEnum.SUCCESS.getCode());
        response.setMsg(ResultStatusEnum.SUCCESS.getDesc());
        return response;
	}

    public static <T> MqcResponse<T> success(T data){
        MqcResponse<T> response = new MqcResponse<>();
        response.setSuccess(true);
        response.setCode(ResultStatusEnum.SUCCESS.getCode());
        response.setMsg(ResultStatusEnum.SUCCESS.getDesc());
        response.setData(data);
        return response;
    }

    public static MqcResponse fail(String errMsg){
        MqcResponse response = new MqcResponse<>();
        response.setSuccess(false);
        response.setCode(ResultStatusEnum.FAIL.getCode());
        response.setMsg(errMsg);
        return response;
    }

	public static MqcResponse fail(int errCode, String errMsg) {
        MqcResponse response = new MqcResponse<>();
        response.setSuccess(false);
        response.setCode(errCode);
        response.setMsg(errMsg);
        return response;
	}

}
