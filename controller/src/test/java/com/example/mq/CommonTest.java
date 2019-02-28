package com.example.mq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.alibaba.fastjson.JSONObject;

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
		commonTest.testStrToList();

		System.out.println("------test end!");


	}

	public void testStrToList(){
		List<String> testList =new ArrayList<>();
		testList.add("str1");
		testList.add("str2");
		testList.add("str3");
		String tmpStr1 = JSONObject.toJSONString(testList);
		String tmpStr2 = testList.toString();
		System.out.println("tmpStr1:" + tmpStr1);
		System.out.println("tmpStr2:" + tmpStr2);

		List<String> tmpResult1 =JSONObject.parseArray(tmpStr1, String.class);
		tmpResult1.stream().forEach(str -> {
			System.out.println("tmpResult1 value:" +str);
		});

		List<String> tmpResult2 =JSONObject.parseArray(tmpStr2, String.class);
		tmpResult2.stream().forEach(str -> {
			System.out.println("tmpResult2 value:" +str);
		});

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
