package com.example.mq.wrapper.stock.enums;


import org.apache.commons.lang3.StringUtils;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018/11/17
 *
 */
public enum FinanceReportTypeEnum {

	QUARTER_1("1季报", "1季报"),
	HALF_YEAR("中报", "中报"),
	QUARTER_3("3季报", "3季报"),
	ALL_YEAR("年报", "年报"),

	SINGLE_Q_1("Q1", "Q1"),
	SINGLE_Q_2("Q2", "Q2"),
	SINGLE_Q_3("Q3", "Q3"),
	SINGLE_Q_4("Q4", "Q4");

	private String code;
	private String desc;

	FinanceReportTypeEnum(String code, String desc){
		this.code =code;
		this.desc =desc;
	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static FinanceReportTypeEnum getByCode(String code){
		if(StringUtils.isEmpty(code)){
			return null;
		}
		for(FinanceReportTypeEnum typeEnum: FinanceReportTypeEnum.values()){
			if(StringUtils.equals(typeEnum.getCode(), code)){
				return typeEnum;
			}
		}
		return null;
	}
}
