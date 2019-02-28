package com.example.mq.testcode.leedcode.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-07-28 16:31:09
 **/
public class CountDownLatchTest {

    private static final Logger log =LoggerFactory.getLogger(CountDownLatch.class);

    private static int num =0;

    private static class Worker implements Runnable{

        private CountDownLatch latch;
        private int id;

        public Worker (CountDownLatch latch, int id){
            this.latch =latch;
            this.id =id;
        }

        @Override
        public void run() {
            System.out.println("work section 1, id:" +id);
            try {
                Thread.sleep(2*1000);
            } catch (InterruptedException e) {
                log.error("thread is interrupted!", e);
            }
            System.out.println("work section 2, id:" +id);
            latch.countDown();
        }
    }

    public static void main(String[] args){
        int threadNum =3;
        CountDownLatch latch =new CountDownLatch(threadNum);
        ExecutorService executor =new ThreadPoolExecutor(threadNum, threadNum, 10, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>());
        for(int i=0;i<threadNum;i++){
            executor.execute(new Worker(latch, i));
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("thread is interrupted!", e);
        }
        System.out.println("work end");

    }
}
