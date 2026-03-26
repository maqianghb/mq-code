package com.example.mq.testcode.stream;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/8/23
 *
 */

public class StreamTest {

	public static void main(String[] args) {
		StreamTest test =new StreamTest();
		test.testCollect();
		System.out.println("---test end.");

	}

	private void testCollect(){
		//stream
		Stream<Student> studentStream =Stream.of(new Student(101L, "a", 11),
				new Student(102L, "bb", 13), new Student(103L, "ccc", 11));
		//stream to list
		List<Student> studentList =studentStream.collect(Collectors.toList());

		//group
		Map<Integer, Long> countMap =studentList.stream()
				.collect(Collectors.groupingBy(Student::getAge, Collectors.counting()));
		System.out.println("countMap:"+countMap.toString());
		Map<Integer, List<Student>> groupMap = studentList.stream().collect(Collectors.groupingBy(Student::getAge));
		System.out.println("groupMap:" + JSONObject.toJSONString(groupMap));
		Map<Integer, Set<Student>> groupSet = studentList.stream().collect(Collectors.groupingBy(Student::getAge, Collectors.toSet()));
		System.out.println("groupSet:" + JSONObject.toJSONString(groupSet));

		//to map
		Map<String, Student> nameMap = studentList.stream().collect(Collectors.toMap(Student::getName,
				Function.identity(), (oldO, newO)->newO));
		System.out.println("nameMap:" + JSONObject.toJSONString(nameMap));

		//to concurrentHashMap
		ConcurrentHashMap<String, Student> conNameMap = studentList.stream().collect(Collectors.toMap(Student::getName,
				Function.identity(), (oldO, newO) -> newO, ConcurrentHashMap::new));
		System.out.println("conNameMap:" + JSONObject.toJSONString(conNameMap));

		//sort
		List<Student> sortedList = studentList.stream().sorted(Comparator.comparing(Student::getAge)).collect(Collectors.toList());
		System.out.println("sortedList:" +JSONObject.toJSONString(sortedList));
		sortedList =studentList.stream().sorted(Comparator.comparing(Student::getAge).reversed()).collect(Collectors.toList());
		System.out.println("sortedList:" +JSONObject.toJSONString(sortedList));

		sortedList = studentList.stream().sorted(Comparator.comparing(Student::getName,
				(a1, a2) -> a1.length() > a2.length() ? 1 : -1)).collect(Collectors.toList());
		System.out.println("sortedList:" + JSONObject.toJSONString(sortedList));
		sortedList = studentList.stream().sorted(Comparator.comparing(Student::getName,
				(a1, a2) -> a1.length() > a2.length() ? 1 : -1).reversed()).collect(Collectors.toList());
		System.out.println("sortedList:" + JSONObject.toJSONString(sortedList));

		//map
		TreeMap<String, Student> treeMap = nameMap.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> newVal, TreeMap::new));
		System.out.println("treeMap:" +JSONObject.toJSONString(treeMap));

		// list to treeSet
		TreeSet<String> treeSetStr = nameMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toCollection(
				()->new TreeSet<>(Comparator.comparing(str -> str.length(), (o1, o2)-> o1 < o2 ? 1 : -1))));
		System.out.println("treeSetStr:" +JSONObject.toJSONString(treeSetStr));

		TreeSet<String> treeSetNames = nameMap.entrySet().stream().map(Map.Entry::getKey)
				.collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(String::length))));
		System.out.println("treeSetNames:" + JSONObject.toJSONString(treeSetNames));


		Set<Student> students = nameMap.entrySet().stream().filter(entry -> !StringUtils.isEmpty(entry.getKey()))
				.map(entry -> entry.getValue()).collect(Collectors.toSet());
		System.out.println("students:" +JSONObject.toJSONString(students));


		TreeSet<String> keys = nameMap.entrySet().stream().map(entry -> entry.getKey())
				.collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(str -> str.length(), (o1, o2) -> o1 < o2 ? 1 : -1))));
		System.out.println("keys:"+ JSONObject.toJSONString(keys));




	}



	private class Student{
		private Long id;
		private String name;
		private Integer age;

		public Student(Long id, String name, Integer age) {
			this.id = id;
			this.name = name;
			this.age = age;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}


	}
}
