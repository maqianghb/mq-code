package com.example.mq.client.enums;

import java.util.Objects;

/**
 * @program: mq-code
 * @description: 返回响应状态
 * @author: maqiang
 * @create: 2018/10/9
 *
 */
public enum ResultStatusEnum {
	SUCCESS(200, "成功"),
	FAIL(-1,"失败"),
	NO_AUTH(-1,"无授权，请重新登录");

	private Integer code;

	private String desc;

	public Integer getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	ResultStatusEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public static ResultStatusEnum getByCode(int code){
		for(ResultStatusEnum statusEnum: ResultStatusEnum.values()){
			if(Objects.equals(statusEnum.getCode(), code)){
				return statusEnum;
			}
		}

		return null;
	}
}
