package com.example.mq.testcode.leedcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @program: mq-code
 * @description: 380. 常数时间插入、删除和获取随机元素
 * @author: maqiang
 * @create: 2019/12/9
 *
 */

class RandomizedSet {

	private List<Integer> values =new ArrayList();
	private Map<Integer, Integer> posMap =new HashMap<>();


	/** Initialize your data structure here. */
	public RandomizedSet() {

	}

	/** Inserts a value to the set. Returns true if the set did not already contain the specified element. */
	public boolean insert(int val) {
		if(posMap.containsKey(val)){
			return false;
		}
		values.add(val);
		posMap.put(val, values.size()-1);
		return true;

	}

	/** Removes a value from the set. Returns true if the set contained the specified element. */
	public boolean remove(int val) {
		if(!posMap.containsKey(val)){
			return false;
		}
		int index =posMap.get(val);
		if(index ==values.size() -1){
			values.remove(values.size() -1);
			posMap.remove(val);
		}else{
			values.set(index, values.get(values.size()-1));
			posMap.put(values.get(values.size()-1), index);
			values.remove(values.size()-1);
			posMap.remove(val);
		}
		return true;
	}

	/** Get a random element from the set. */
	public int getRandom() {
		int index =new Random().nextInt(values.size());
		return values.get(index);
	}

	public static void main(String[] args) {
		RandomizedSet randomizedSet =new RandomizedSet();
		randomizedSet.insert(0);
		randomizedSet.insert(1);
		randomizedSet.remove(0);
		randomizedSet.insert(2);
		randomizedSet.remove(1);
		randomizedSet.getRandom();

	}
}
