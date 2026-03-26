package com.example.mq.common.enums.base;

import java.util.Objects;

/**
 * @program: mq-code
 * @description: 返回响应状态
 * @author: maqiang
 * @create: 2018/10/9
 *
 */
public enum ResultStatusEnum {
	SUCCESS("0000", "成功"),
	FAIL("0100","失败"),
	NO_AUTH("0200","无授权，请重新登录");

	private String code;

	private String desc;

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	ResultStatusEnum(String code, String desc) {
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
