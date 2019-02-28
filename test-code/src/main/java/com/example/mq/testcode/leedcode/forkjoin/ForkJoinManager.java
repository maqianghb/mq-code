package com.example.mq.testcode.leedcode.forkjoin;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

/**
 * @program: mq-code
 * @description: fork/join框架管理器
 * @author: maqiang
 * @create: 2018/11/5
 *
 */
@Component
public class ForkJoinManager {
	private static final Logger LOG = LoggerFactory.getLogger(ForkJoinManager.class);

	private ForkJoinPool taskManagerPool;

	private final long  TASK_TIME_OUT =10*1000L;

	@PostConstruct
	private void init(){
		//设置并行度
		int parallelism = Runtime.getRuntime().availableProcessors() * 2;
		taskManagerPool =new ForkJoinPool(parallelism);
	}

	public Integer calculate(Integer startNum, Integer endNum){
		if(Objects.isNull(startNum) || Objects.isNull(endNum) || startNum >=endNum){
			return 0;
		}
		ForkJoinTask<Integer> task =taskManagerPool.submit(new CalculateSumTask(startNum, endNum));
		try {
			Integer taskResult =task.get(TASK_TIME_OUT, TimeUnit.MILLISECONDS);
			if(!Objects.isNull(taskResult)){
				return taskResult;
			}else{
				return 0;
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			LOG.error("CalculateSumTask execute err!", e);
		}
		return 0;
	}

	public static void main(String[] args){
		ForkJoinManager manager =new ForkJoinManager();
		manager.init();
		int result =manager.calculate(1, 10000);
		System.out.println("------calculate result:"+result);
	}


}
