package com.example.mq.service.bean.dubbo;

import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/29
 *
 */
@Data
public class DubboParamType {

	private String methodName;

	private Integer order =0;

	private String paramName;

	private String paramType;

	private String field;

	private Object defaultValue;

	private Long createTime;

	private Long updateTime;
}
