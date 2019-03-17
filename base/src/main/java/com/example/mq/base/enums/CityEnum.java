package com.example.mq.base.enums;


import org.apache.commons.lang3.StringUtils;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018/11/17
 *
 */
public enum CityEnum {
	HANGZHOU("0571", "杭州"),
	BEIJING("010", "北京");

	private String code;
	private String name;

	CityEnum(String code, String name){
		this.code =code;
		this.name =name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public static CityEnum getByCode(String code){
		if(StringUtils.isEmpty(code)){
			return null;
		}
		for(CityEnum cityEnum: CityEnum.values()){
			if(cityEnum.getCode().equals(code)){
				return cityEnum;
			}
		}
		return null;
	}
}
