package com.example.mq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/1/28
 *
 */

public class CommonTest {

	public static void main(String[] args) {
		CommonTest commonTest =new CommonTest();
		commonTest.testReplaceSpace();

		System.out.println("------test end!");


	}

	public void testReplaceSpace(){
		String str ="  12  34  56  ";
		System.out.println("result:"+ str.replace(" ", ""));
	}

	public void testStrCompare(){
		String str1 ="20181226";
		String str2 ="20181225";
		System.out.println("compare result:"+ str1.compareTo(str2));
	}

	public void testCode(){
		HashMap map =new HashMap();
		List<String> list =new ArrayList<>();
		LinkedBlockingQueue queue =new LinkedBlockingQueue();
	}
}