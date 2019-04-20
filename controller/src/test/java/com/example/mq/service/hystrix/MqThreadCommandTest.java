package com.example.mq.service.hystrix;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.mq.base.util.CommonUtils;
import com.example.mq.controller.ControllerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/19
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ControllerApplication.class)
public class MqThreadCommandTest {
	private static Logger LOG = LoggerFactory.getLogger(MqThreadCommandTest.class);

	@Test
	public void testHytrixCommand(){
		HystrixConfig hystrixConfig =new HystrixConfig();
		hystrixConfig.setExecuteTimeOutInMillis(200);
		hystrixConfig.setCorePoolSize(2);
		hystrixConfig.setMaxQueueSize(10);
		hystrixConfig.setMaxPoolSize(2);
		hystrixConfig.setKeepAliveTime(60);
		hystrixConfig.setQueueRejectSize(8);

		hystrixConfig.setCircuitBreakerEnabled(true);
		hystrixConfig.setCbErrorThresholdPercent(50);
		hystrixConfig.setCbRequestVolumeThreshold(20);
		hystrixConfig.setCbSleepWindowInMillis(5*1000);


		int timeoutNums =0;
		int fallbackNums =0;
		for(int i=0; i<100; i++){
			long customerNo = CommonUtils.createRandomId(6);
			MqThreadCommand command =new MqThreadCommand(customerNo, hystrixConfig);
			long startTime =System.currentTimeMillis();
			Map<String, Object> result =command.execute();
			if(System.currentTimeMillis() -startTime > 200){
				timeoutNums ++;
			}
			if(result.size() ==0){
				fallbackNums ++;
			}
			if(command.isCircuitBreakerOpen()){
				LOG.info(" circuitBreaker open.");
			}
		}

		try {
			Thread.sleep(100 *1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LOG.info(" execute result, timeoutNums:{}|fallbackNums:{}", timeoutNums, fallbackNums);

	}

}