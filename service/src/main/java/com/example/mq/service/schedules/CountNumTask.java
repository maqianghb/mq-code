package com.example.mq.service.schedules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018/9/5
 *
 */
@Component
public class CountNumTask {
	private static final Logger LOG = LoggerFactory.getLogger(CountNumTask.class);

	@Scheduled(cron = "${count.num.schedule.cron}")
	public void executeSchedue(){
		Long startTime =System.currentTimeMillis();
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		LOG.info("开始执行定时任务，startTime:{}", sdf.format(new Date(startTime)));
		try {
			this.doExecute();
		} catch (Exception e) {
			LOG.error("定时任务执行出错！", e);
		}
		LOG.info("定时任务执行结束，costTime:{}ms", (System.currentTimeMillis()-startTime));
	}

	private void doExecute() throws Exception{
		int sum =0;
		for(int i=0;i<10000; i++){
			sum +=i;
		}
		System.out.println("calculate sum:" +sum);
	}
}
