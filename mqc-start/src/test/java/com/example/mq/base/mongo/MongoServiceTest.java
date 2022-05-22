package com.example.mq.base.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.example.mq.controller.ControllerApplication;
import com.example.mq.controller.customer.CustomerControllerTest;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018/11/23
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ControllerApplication.class)
public class MongoServiceTest {
	private static final Logger LOG = LoggerFactory.getLogger(MongoServiceTest.class);

	@Autowired
	private MongoService mongoService;

	@Test
	public void getById() {
		ThreadPoolExecutor threadPoolExecutor =new ThreadPoolExecutor(
				20, 50,
				60, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(10000),
				new ThreadPoolExecutor.AbortPolicy()
		);

		long startTime =System.currentTimeMillis();
		List<Future<Document>> futureList =new ArrayList<>(10000);
		for(int i=0; i<10000; i++){
			long id =1024223L + System.currentTimeMillis()%3 -1;
			Future<Document> future =threadPoolExecutor.submit(new Callable<Document>() {
				@Override
				public Document call() throws Exception {
					return new Document();
				}
			});
			futureList.add(future);
		};

		List<Document> documentLsit =new ArrayList<>(futureList.size());
		futureList.forEach(future -> {
			try {
				Document tmpDocument =null;
				if(null !=(tmpDocument =future.get())){
					documentLsit.add(tmpDocument);
				}
			} catch (InterruptedException | ExecutionException e) {
				LOG.error("futureList get error:{}", e);
			}
		});
		LOG.info("costTime:{}ms", System.currentTimeMillis()-startTime);
		LOG.info("documentList size:{}", documentLsit.size());
		Assert.assertTrue( documentLsit.size() > 2000);
	}
}