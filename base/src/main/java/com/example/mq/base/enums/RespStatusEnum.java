package com.example.mq.base.enums;

/**
 * @program: mq-code
 * @description: 返回响应状态
 * @author: maqiang
 * @create: 2018/10/9
 *
 */
public enum RespStatusEnum {
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

	RespStatusEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public static RespStatusEnum getByCode(int code){
		for(RespStatusEnum statusEnum: RespStatusEnum.values()){
			if(code ==statusEnum.getCode()){
				return statusEnum;
			}
		}
		return null;
	}
}
