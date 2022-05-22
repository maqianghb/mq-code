package com.example.mq.service.bean.dubbo;

import java.util.List;

import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/29
 *
 */
@Data
public class DubboRequestConfig {

	private String featureName;

	private String protocol;

	private String registerId;

	private Boolean check =true;

	private Integer retries =3;

	private Integer timeout =150;

	private String interfaceName;

	private String version;

	private String method;

	private List<DubboParamType> paramTypes;

	private String responseType;

}
