package com.example.mq.testcode.elasticjob.task;

import java.util.Date;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.example.mq.data.mongo.MongoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/1/9
 *
 */
@Component
public class TestJob1 implements SimpleJob {
	private static final Logger LOG = LoggerFactory.getLogger(TestJob1.class);

	@Autowired
	private MongoService mongoService;

	@Override
	public void execute(ShardingContext shardingContext) {
		long startTime =System.currentTimeMillis();
		LOG.info("testJob1定时任务启动，startTime:{}",startTime);
		try {
			int startNum =111;
			int endNum =999;
			this.doExecuteTask(startNum, endNum);
			LOG.info("testJob1定时任务结束, costTime:{}ms|startNum:{}|endNum:{}", System.currentTimeMillis()-startTime, startNum, endNum);
		} catch (Exception e) {
			LOG.error("定时任务执行出错！", e);
		}
	}

	private void doExecuteTask(int startNum, int endNum) throws Exception{
		if(endNum <=startNum){
			throw  new IllegalAccessException("参数不正确");
		}
		int intProduct =1;
		long longProduct = 1;
		for (int i = startNum; i <= endNum; i++) {
			intProduct= intProduct * i;
			longProduct = intProduct * i;
		}
		LOG.info("定时任务计算结果, section:[{}, {}]|intProduct:{}|longProduct:{}",startNum, endNum, intProduct, longProduct);
	}
}
