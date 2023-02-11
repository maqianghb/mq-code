package com.example.mq.data.codis.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/3/11
 *
 */
@Component
public class CodisConfig {

	@Value("${codis.conn.zk.server}")
	private String codisConnZkService;

	@Value("${codis.conn.name}")
	private String codisConnName;

	@Value("${codis.conn.password}")
	private String codisConnPassword;


	public String getCodisConnZkService() {
		return codisConnZkService;
	}

	public String getCodisConnName() {
		return codisConnName;
	}

	public String getCodisConnPassword() {
		return codisConnPassword;
	}
}
