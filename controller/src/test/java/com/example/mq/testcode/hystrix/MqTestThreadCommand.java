package com.example.mq.testcode.hystrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.controller.ControllerApplication;
import com.example.mq.service.hystrix.HystrixConfig;
import com.example.mq.service.hystrix.MqThreadCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/1/16
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ControllerApplication.class)
public class MqTestThreadCommand {

	private HystrixConfig config;

	@Before
	public void setUp() throws Exception {

		config =new HystrixConfig();
		config.setExecuteTimeOutInMillis(10 * 1000);
		config.setCorePoolSize(2);
		config.setMaxQueueSize(10);
		config.setMaxPoolSize(5);
		config.setKeepAliveTime(30);
		config.setQueueRejectSize(20);
		config.setCircuitBreakerEnabled(true);
		config.setCbErrorThresholdPercent(30);
		config.setCbRequestVolumeThreshold(100);
		config.setCbSleepWindowInMillis(5 * 1000);
	}

	@Test
	public void testHystrixThreadCommand() {
		List<Future<Map<String, Object>>> futures =new ArrayList<>(100);
		for(int i=1; i<=100; i++){
			futures.add(new MqThreadCommand(Long.parseLong(String.valueOf(i)), config).queue());
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Map<String, Object> resultMap = new HashMap<>();
		futures.forEach(future -> {
			try {
				resultMap.putAll(future.get());
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("futureList get error:"+ e);
			}
		});
		System.out.println("resultMap:"+ JSONObject.toJSONString(resultMap));
	}
}