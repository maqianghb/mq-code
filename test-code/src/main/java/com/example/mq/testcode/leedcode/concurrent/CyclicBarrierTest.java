package com.example.mq.testcode.leedcode.concurrent;

import java.util.concurrent.CyclicBarrier;
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
 * @create: 2018-07-28 19:15:21
 **/
public class CyclicBarrierTest {

    private static final Logger log =LoggerFactory.getLogger(CyclicBarrierTest.class);

    static class Worker implements Runnable{

        private CyclicBarrier barrier;
        private int id;

        public Worker(CyclicBarrier barrier, int id){
            this.barrier =barrier;
            this.id =id;
        }

        @Override
        public void run() {
            System.out.println("work section 1, id:" +id);
            try {
                Thread.sleep(2*1000);

            } catch (InterruptedException e) {
                log.error(" thread is interrupted!", e);
            }
            try {
                barrier.await();
            } catch (Exception e) {
                log.error(" barrier is interrupted!", e);
            }
            System.out.println("work section 2, id:" +id);
        }
    }

    public static void main(String[] args){
        int threadNum =3;
        CyclicBarrier barrier =new CyclicBarrier(threadNum,()->{
            System.out.println("start do next work section!");
        });
        ExecutorService executor = new ThreadPoolExecutor(threadNum, threadNum, 10, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>());
        for(int i=0;i<threadNum;i++){
            executor.execute(new Worker(barrier, i));
        }
    }
}
