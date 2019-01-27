package com.example.mq.service.bean;

import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/1/22
 *
 */
@Data
public class Seller {

	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * id
	 */
	private Long sellerId;

	/**
	 * 姓名
	 */
	private String name;
}
