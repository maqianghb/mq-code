package com.example.mq.service.schedule;

import com.example.mq.data.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
	public void executeSchedue() {
		Long startTime = System.currentTimeMillis();
		LOG.info("开始执行定时任务，startTime:{}", DateUtil.formatDateTime(new Date(startTime)));
		try {
			this.doExecute();
		} catch (Exception e) {
			LOG.error("定时任务执行出错！", e);
		}
		LOG.debug("------debug, 定时任务执行结束，costTime:{}ms", (System.currentTimeMillis() - startTime));
		LOG.info("------info, 定时任务执行结束，costTime:{}ms", (System.currentTimeMillis() - startTime));
		LOG.error("------error, 定时任务执行结束，costTime:{}ms", (System.currentTimeMillis() - startTime));
	}

	private void doExecute() throws Exception {
		int intSum = 0;
		long longSum = 0;
		for (int i = 1; i <= 100 * 10000; i++) {
			intSum += i;
			longSum += i;
		}
		LOG.info("定时任务计算结果, intSum:{}|longSum:{}", intSum, longSum);
	}
}
