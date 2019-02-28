package com.example.mq.testcode.leedcode.filterchain;

import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/2/12
 *
 */
@Data
public class MqString {

	private String value;

	public MqString(String string){
		this.value =string;
	}
}
