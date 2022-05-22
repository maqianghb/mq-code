package com.example.mq.service.bean;

import java.util.Date;

import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/2/27
 *
 */
@Data
public class CustomerOperation {

	private Long id;

	private Long customerNo;

	private String customerName;

	private String md5;

	private Integer operateType;

	private String operateTypeDesc;

	private String content;

	private String operator;

	private Date createTime;

	private Date updateTime;
}
