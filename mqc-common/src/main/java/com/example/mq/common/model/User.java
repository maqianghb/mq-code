package com.example.mq.common.model;

import java.io.Serializable;

import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/2/27
 *
 */
@Data
public class User implements Serializable {

	private static final long serialVersionUID = -63542399109185405L;

	private Long id;

	private Long userNo;

	private String userName;

	private String phone;

	private Long companyNo;

	private String companyName;
}
