package com.example.mq.service.leedcode.forkjoin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: fork/join分解任务
 * @author: maqiang
 * @create: 2018/11/5
 *
 */

public class CalculateSumTask extends RecursiveTask<Integer> {
	private static final Logger LOG = LoggerFactory.getLogger(CalculateSumTask.class);

	private static AtomicInteger taskNum =new AtomicInteger(1);

	private final int THRESLOD =10;
	private final long TASK_TIME_OUT =1 *1000L;
	private Integer startNum;
	private Integer endNum;

	public CalculateSumTask(Integer startNum, Integer endNum){
		this.startNum =startNum;
		this.endNum =endNum;
	}

	@Override
	protected Integer compute() {
		int taskIndex = taskNum.getAndIncrement();
		System.out.println("------taskIndex:"+taskIndex);
		Long startTime =System.currentTimeMillis();
		if(endNum -startNum <=THRESLOD){
			int sum =0;
			for(int i=startNum; i<=endNum; i++){
				sum +=i;
//				try {
//					Thread.sleep(10L);
//				} catch (InterruptedException e) {
//					LOG.info("thread InterruptedException. ", e);
//				}
			}
			return sum;
		}

		//并行处理
		Integer div1 =startNum +(endNum-startNum)/3;
		Integer div2 =div1 + (endNum-startNum)/3;
		List<ForkJoinTask> taskList = new ArrayList<>();
		taskList.add(new CalculateSumTask(startNum, div1).fork());
		taskList.add(new CalculateSumTask(div1+1, div2).fork());
		taskList.add(new CalculateSumTask(div2+1, endNum).fork());

		Integer sum =0;
		for(ForkJoinTask<Integer> task :taskList){
			long forkTime = TASK_TIME_OUT -(System.currentTimeMillis() -startTime);
			if(forkTime <=0){
				forkTime =1L;
			}
			try {
				Integer taskResult =task.get(forkTime, TimeUnit.MILLISECONDS);
				if(!Objects.isNull(taskResult)){
					sum +=taskResult;
				}
			} catch (InterruptedException | ExecutionException | TimeoutException  e) {
				LOG.error("CalculateSumTask execute err!", e);
			}
		}
		return sum;
	}
}
