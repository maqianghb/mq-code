package com.example.mq.common.enums;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/2/27
 *
 */
public enum PlatformOperateEnum {
	ADD(1, "添加操作"),
	UPDATE(2, "更新操作"),
	DELETE(3, "删除操作");

	private Integer code;
	private String desc;

	PlatformOperateEnum(int code, String desc){
		this.code =code;
		this.desc =desc;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static PlatformOperateEnum getByCode(int code){
		for(PlatformOperateEnum operateEnum : PlatformOperateEnum.values()){
			if(code == operateEnum.getCode()){
				return operateEnum;
			}
		}
		return null;
	}
}
