package com.example.mq.service.customer.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.service.bean.Customer;
import com.example.mq.service.customer.CustomerSaveService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/3
 *
 */
@Service("asyncCustomerSaveService")
public class AsyncCustomerSaveServiceImpl implements CustomerSaveService {
	private static final Logger LOG = LoggerFactory.getLogger(AsyncCustomerSaveServiceImpl.class);

	private final Integer CORE_THREAD_POOL_SIZE =2;
	private final Integer MAX_THREAD_POOL_SIZE =5;
	private final Integer MAX_THREAD_POOL_QUEUE_SIZE =50;
	private final Integer MAX_LINKED_QUEUE_SIZE =100 *1000;

	/**
	 * customer
	 */
	private static ConcurrentLinkedQueue<Customer> saveQueue =new ConcurrentLinkedQueue<>();

	private ThreadFactory saveThreadFactory =new ThreadFactoryBuilder().setNameFormat("save-customer-pool-%d").build();
	private ExecutorService saveExecutor = new ThreadPoolExecutor(
			CORE_THREAD_POOL_SIZE,
			MAX_THREAD_POOL_SIZE,
			60,
			TimeUnit.MINUTES,
			new LinkedBlockingQueue<>(MAX_THREAD_POOL_QUEUE_SIZE),
			saveThreadFactory,
			new ThreadPoolExecutor.AbortPolicy()
	);


	@Override
	public long saveCustomer(Customer customer) throws Exception {
		if(null ==customer){
			throw new IllegalArgumentException(" saveCustomer 操作，参数为空！");
		}
		long result =1;
		//save queue
		if(saveQueue.size() >= MAX_LINKED_QUEUE_SIZE){
			LOG.error(" saveQueue 队列已满, queueSize:{}|customer:{}", saveQueue.size(),
					JSONObject.toJSONString(customer));
			result =0;
		}else {
			if(!saveQueue.offer(customer)){
				LOG.error("saveQueue 添加数据失败，customer:{}", JSONObject.toJSONString(customer));
				result =0;
			}
		}

		//execute
		this.startExecutor();

		LOG.info(" saveCustomer result, isSuccess:{}|customer:{}", result >0, JSONObject.toJSONString(customer));
		return result;
	}

	private void startExecutor() {
		saveExecutor.execute(new Runnable() {
			@Override
			public void run() {
				while(saveQueue.size() >0){
					Customer customer =saveQueue.poll();
					if(null ==customer){
						break;
					}

					//execute
					try {
						if(doSaveCustomer(customer) <=0){
							LOG.error(" doSaveCustomer 操作失败, thread:{}|customer:{}",
									Thread.currentThread().getName(), customer);
						}
					} catch (Exception e) {
						LOG.error(" doSaveCustomer 操作异常，thread:{}|customer:{}",
								Thread.currentThread().getName(), customer, e);
					}
				}
			}
		});
		return ;
	}

	private long doSaveCustomer(Customer customer) throws Exception{
		return 0;
	}
}
