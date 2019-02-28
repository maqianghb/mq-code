package com.example.mq.testcode.leedcode;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.testcode.leedcode.threadpool.MyThreadPoolExecutor;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-07-28 16:31:09
 **/
public class SortExercise {

	private Integer CORE_POOL_SIZE =2;
	private Integer MAX_POOL_SIZE =5;
	private Integer BROCK_QUEUE_SIZE =100;

	private ThreadPoolExecutor executor = new MyThreadPoolExecutor("sortThreadPool", CORE_POOL_SIZE, MAX_POOL_SIZE,30, TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(BROCK_QUEUE_SIZE));

    public static void main(String[] args){
        int[] arr = new int[]{1,7,6,4,2,5,3};
        SortExercise exercise = new SortExercise();
		exercise.testMultThreadSort(arr);
    }

    private void testMultThreadSort(int[] arr){
		CountDownLatch latch =new CountDownLatch(100);
    	for(int i=0; i<100; i++){
    		executor.execute(new Runnable() {
				@Override
				public void run() {
					bubbleSort(arr);
				}
			});
		}
		System.out.println("result:"+ JSONObject.toJSONString(arr));
	}

    private void bubbleSort(int[] arr){
        for(int i=0 ;i<arr.length;i++){
            for(int j=arr.length-1; j>i; j--){
                if(arr[j-1] >arr[j]){
                    int tmp =arr[j-1];
                    arr[j-1] =arr[j];
                    arr[j] =tmp;
                }
            }
        }
    }

    private void chooseSort(int[] arr){
        for(int i=0;i<arr.length-1; i++){
            int min =i;
            for(int j= i+1;j<arr.length;j++){
                if(arr[j]< arr[min]){
                    min =j;
                }
            }
            if(min !=i){
                int tmp =arr[i];
                arr[i] =arr[min];
                arr[min] =tmp;
            }
        }
    }

    private void insertSort(int[] arr){
        for(int i=1; i<arr.length;i++){
            for(int j=i; j>0;j--){
                if(arr[j] <arr[j-1]){
                    int tmp =arr[j];
                    arr[j] =arr[j-1];
                    arr[j-1] =tmp;
                }
            }
        }
    }

    private void quickSort(int[] arr, int start, int end){
        if(start <end){
            int p =patition(arr,  start, end);
            quickSort(arr, start,p-1);
            quickSort(arr, p+1, end);
        }
    }

    private int patition(int[] arr, int p, int q){
        int i=p;
        int j=q;
        int key =arr[p];
        while (i<j){
            while(i<j && arr[i]<=key ){
                i++;
            }
            while(j>i && arr[j] >=key){
                j--;
            }
            if(i <j){
                int tmp =arr[i];
                arr[i] =arr[j];
                arr[j] =tmp;
            }
        }
        arr[p] =arr[j];
        arr[j] =key;
        return j;

    }
}
