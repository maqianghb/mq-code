package com.example.mq.app.job;

import com.example.mq.common.enums.base.BizErrorEnum;
import com.example.mq.common.utils.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018/9/5
 *
 */
@Component
public class CountNumJob {
	private static final Logger LOG = LoggerFactory.getLogger(CountNumJob.class);

	@Scheduled(cron = "${count.num.schedule.cron}")
	public void executeSchedue() {
		Long startTime = System.currentTimeMillis();
		LOG.warn("开始执行定时任务，startTime:{}", startTime);
		try {
			this.doExecute(1, 100 * 10000);
		} catch (Exception e) {
			LOG.error("定时任务执行出错！", e);
		}
		LOG.warn("定时任务执行结束，costTime:{}ms", (System.currentTimeMillis() - startTime));
	}

	private void doExecute(int startNum, int endNum){
		AssertUtils.assertTrue(startNum <= endNum, BizErrorEnum.PARAM_INVALID);

		int intSum =0;
		long longSum = 0;
		for (int i = startNum; i <= endNum; i++) {
			intSum += i;
			longSum += i;
		}
		LOG.debug("------[debug]定时任务计算结果, section:[{}, {}]|intSum:{}|longSum:{}",startNum, endNum, intSum, longSum);
		LOG.info("------[info]定时任务计算结果, section:[{}, {}]|intSum:{}|longSum:{}",startNum, endNum, intSum, longSum);
		LOG.warn("------[warn]定时任务计算结果, section:[{}, {}]|intSum:{}|longSum:{}",startNum, endNum, intSum, longSum);
		LOG.error("------[error]定时任务计算结果, section:[{}, {}]|intSum:{}|longSum:{}",startNum, endNum, intSum, longSum);
	}

	public void manualExecuteCountNum(int startNum, int endNum) {
		Long startTime =System.currentTimeMillis();
		LOG.warn("手工执行定时任务启动，startNum:{}|endNum:{}|startTime:{}", startNum, endNum,
				startTime);

		this.doExecute(startNum, endNum);
		LOG.info("手工执行定时任务结束，startNum:{}|endNum:{}|costTime:{}ms", startNum, endNum,
				System.currentTimeMillis() -startTime);
	}

}
