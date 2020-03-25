package com.example.mq.testcode.leedcode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
        this.next = null;
    }

}

class Interval{
    int start;
    int end;

    Interval(){
        this.start =0;
        this.end =0;
    }

    Interval(int s, int e){
        this.start =s;
        this.end =e;
    }
}

class TreeNode{
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val){
        this.val =val;
        this.left =null;
        this.right =null;
    }
}


/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-07-28 16:31:09
 **/
public class LeedcodeExercise {

    private int n;
    private int k;
    private List<List<Integer>> results =new ArrayList<>();

    public List<List<Integer>> combine(int n, int k) {
        this.n =n;
        this.k =k;
        if(n <k){
            return results;
        }else if(n ==k){
            List<Integer> tmp =new ArrayList<>();
            for(int i=1; i<=n; i++){
                tmp.add(i);
            }
            results.add(tmp);
            return results;
        }
        List<Integer> tmpValue =new ArrayList<>();
        setValue(0, 1, tmpValue);
        return results;

    }

    private void setValue(int index, int startValue, List<Integer> tmpValue){
        if(tmpValue.size() ==k){
            results.add(new ArrayList<>(tmpValue));
            return;
        }
        for(int i=startValue; i<=n-(k-index-1); i++){
            tmpValue.add(i);
            setValue(index+1, i+1, tmpValue);
            tmpValue.remove(tmpValue.size()-1);
        }
    }

    public static void main(String[] args) {
        LeedcodeExercise exercise = new LeedcodeExercise();

        ListNode l1 =new ListNode(1);

        String s =new String("c");
//        int re =exercise.lengthOfLongestSubstring(s);
//        System.out.println(re);

        int[] nums =new int[]{1, 0, 2};
        exercise.sortColors(nums);
        System.out.println("nums:"+ JSONObject.toJSONString(nums));

        String result =exercise.multiply("123", "456");
        System.out.println("result:" + result);

        exercise.combine(3, 2);

    }

    public void sortColors(int[] nums) {
        if(nums == null || nums.length ==1){
            return;
        }
        int indexP0 =0;
        int cur =0;
        while(indexP0 <nums.length && nums[indexP0] ==0){
            indexP0 ++;
            cur ++;
        }
        int indexP2 =nums.length-1;
        while(indexP2 >=0 && nums[indexP2] ==2){
            indexP2--;
        }
        while(cur<=indexP2){
            if(nums[cur] ==0){
                int tmp =nums[indexP0];
                nums[indexP0] =nums[cur];
                nums[cur] =tmp;
                indexP0 ++;
            }
            if(nums[cur] ==2){
                int tmp =nums[indexP2];
                nums[indexP2] =nums[cur];
                nums[cur] =tmp;
                indexP2 --;
            }
            cur ++;
        }
    }

	public String multiply(String num1, String num2) {
		if(num1 =="0" || num2 =="0"){
			return "0";
		}
		if(num1 =="1" || num2 =="1"){
			return num1 =="1" ? num2 : num1;
		}
		int nums2Length =num2.length();
		String sum ="0";
		for(int i=0; i<nums2Length; i++){
			int key2 =num2.charAt(nums2Length-1 -i) -'0';
			if(key2 ==0){
				continue;
			}
			for(int j=0; j<num1.length(); j++){
				int key1 =num1.charAt(num1.length()-1-j) -'0';
				if(key1 ==0){
					continue;
				}
				int keySum =key2*key1;
				String strKeySum =String.valueOf(keySum);
				int num =i+j;
				while(num >0){
					strKeySum +="0";
					num --;
				}
				sum =plus(sum, strKeySum);
			}
		}
		return sum;
	}

	public String plus(String str1, String str2){
		if(str1 =="0" || str2 =="0"){
			return str1 =="0" ? str2 :str1;
		}
		int length = str1.length() >=str2.length()? str1.length() : str2.length();
		int high =0;
		StringBuilder sb =new StringBuilder();
		for(int i=0; i<length; i++){
			int num1 =str1.length() >i ? str1.charAt(str1.length()-1-i)-'0' : 0;
			int num2 =str2.length() >i ? str2.charAt(str2.length()-1-i)-'0' : 0;
			int tmpSum =num1 +num2+ high;
			if(tmpSum >9){
				high =1;
				sb.append(String.valueOf(tmpSum%10));
			}else{
				high =0;
				sb.append(String.valueOf(tmpSum));
			}
		}
		if(high >0){
			sb.append(String.valueOf(high));
		}
		String reverseStr =sb.toString();
		StringBuilder resultSb =new StringBuilder();
		for(int i=0; i<reverseStr.length(); i++){
			resultSb.append(reverseStr.charAt(reverseStr.length()-1 -i));
		}
		return resultSb.toString();
	}

	public void nextPermutation(int[] nums) {
		if(nums ==null || nums.length ==1){
			return;
		}
		int length =nums.length;
		int i =length-2;
		while(i >=0){
			if(nums[i] <nums[i+1]){
				break;
			}
		}
		if(i <0){
			//交换全部
			int s =0;
			int e =length-1;
			while (s <e){
				int tmp =nums[s];
				nums[s] =nums[e];
				nums[e] =tmp;

				s++;
				e--;
			}
			return;
		}
		int j=length-1;
		for(; j>i; j--){
			if(nums[j] > nums[i]){
				break;
			}
		}
		int tmp =nums[i];
		nums[i] =nums[j];
		nums[j] =tmp;

		int s =i+1;
		int e =length-1;
		while(s <e){
			int changeTmp =nums[s];
			nums[s] =nums[e];
			nums[e] =changeTmp;

			s++;
			e--;
		}
	}

	public String convert(String s, int numRows) {
		List<StringBuilder> rows =new ArrayList<>(numRows);
		for(int i=0; i<numRows; i++){
			rows.add(new StringBuilder());
		}

		int rowIndex =0;
		boolean isDown =false;
		for(int i=0; i<s.length(); i++){
			char tmpChar =s.charAt(i);
			rows.get(rowIndex).append(tmpChar);
			if(rowIndex ==0 || rowIndex ==rows.size()-1){
				isDown = !isDown;
			}
			if(isDown){
				rowIndex =rowIndex +1;
			}else{
				rowIndex =rowIndex -1;
			}
		}

		StringBuilder result =new StringBuilder();
		rows.stream().forEach(sb->result.append(sb.toString()));
		return result.toString();

	}

    // 347. 前 K 个高频元素
	public List<Integer> topKFrequent(int[] nums, int k) {
    	Map<Integer, Integer> numMap =new HashMap<>();
		for(int i=0; i<nums.length; i++){
			int tmpNum =nums[i];
			numMap.put(tmpNum, numMap.getOrDefault(tmpNum, 0)+1);
		}
		if(k ==1){
			return new ArrayList<>(numMap.keySet());
		}

		PriorityQueue<Integer> heap =new PriorityQueue<>((n1, n2)->numMap.get(n1)-numMap.get(n2));
			for(int num :numMap.keySet()){
			heap.add(num);
			if(heap.size() >k){
				heap.poll();
			}
		}

		List<Integer> topK =new ArrayList<>();
			while (!heap.isEmpty()){
				topK.add(heap.poll());
			}
		Collections.reverse(topK);
			return topK;
	}

    public int maxArea(int[] height) {
        int result =0;
        for(int i=0;i<height.length-1;i++){
            for(int j=i+1;j<height.length;j++){
                int tmpHeight = height[i] <height[j] ?height[i] :height[j];
                result = result >(j-i)*tmpHeight ?result :(j-i)*tmpHeight;
            }
        }
        return result;
    }

    public String longestPalindrome(String s) {
        if(null ==s || 0 ==s.length()){
            return  s;
        }
        String result ="";
        for(int i =0; i<s.length(); i++){
            int firstIndex =i;
            int lastIndex =i;
            while(firstIndex>=0 && lastIndex<s.length() && s.charAt(firstIndex) ==s.charAt(lastIndex) ){
                if(lastIndex-firstIndex+1 >result.length()){
                    result =s.substring(firstIndex,lastIndex+1);
                }
                firstIndex--;
                lastIndex++;
            }
        }
        for(int i=0;i<s.length(); i++){
            int firstIndex =i;
            int lastIndex =i+1;
            while(firstIndex>=0 && lastIndex<s.length() && s.charAt(firstIndex) ==s.charAt(lastIndex) ){
                if(lastIndex-firstIndex+1 >result.length()){
                    result =s.substring(firstIndex,lastIndex+1);
                }
                firstIndex--;
                lastIndex++;
            }
        }
        return result;
    }

    public int lengthOfLongestSubstring(String s) {
        if(null ==s || 0 ==s.length()){
            return  0;
        }
        int  maxNum =0;
        for(int i=0;i<s.length()-1;i++){
            int tmpCount=0;
            Map<Character, Integer> map = new HashMap<Character, Integer>();
            int j=i;
            while(j<s.length()){
                if( null ==map.get(s.charAt(j))){
                    map.put(s.charAt(j), j);
                    tmpCount ++;
                    j++;
                }else{
                    maxNum = tmpCount >maxNum ?tmpCount :maxNum;
                    break;
                }
            }
            maxNum = maxNum >tmpCount ?maxNum :tmpCount;
            if(j ==s.length()){
                return maxNum;
            }
        }
        return maxNum;
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {

        ListNode currentNode = new ListNode(0);
        ListNode header = currentNode;
        int flag =0;
        while(null !=l1 || null !=l2){
            int val1 = null!=l1 ?l1.val :0;
            int val2 = null!=l2 ?l2.val :0;
            int tmp =val1+val2+ flag;
            currentNode.next = new ListNode(tmp%10);
            currentNode =currentNode.next;
            flag = tmp/10;
            l1 = null !=l1 ?l1.next :null;
            l2 =null !=l2 ?l2.next: null;
        }
        if(1 ==flag){
            currentNode.next =new ListNode(1);
        }
        return header.next;
    }

    public boolean isPalindrome(String s) {
        s =s.toLowerCase();
        if(null ==s || 0 ==s.length()){
            return true;
        }
        int i =0;
        int j=s.length()-1;
        while(i <j){
            if( !isCharOrrNum(s.charAt(i))){
                i++;
                continue;
            }
            if( !isCharOrrNum(s.charAt(j))){
                j--;
                continue;
            }
            if(s.charAt(i) !=s.charAt(j)){
                return false;
            }else{
                i++;
                j--;
            }
        }
        return true;
    }

    private boolean isCharOrrNum(char c){
        if(('a' <=c && c <='z') ||('A' <=c && c <='Z') || ('0' <=c && c <='9')){
            return true;
        }
        return false;

    }

    public boolean isValidBST(TreeNode root) {
        return checkBST(root, null, null);
    }

    private boolean  checkBST(TreeNode root, Integer min, Integer max){
        if(null ==root ){
            return true;
        }

        if(null !=min && root.val <= min){
            return false;
        }
        if(null !=max && root.val >=max){
            return false;
        }
        return checkBST(root.left, min, root.val) &&checkBST(root.right, root.val, max);
    }

    public void merge(int[] nums1, int m, int[] nums2, int n) {
        if(0 ==m){
            for(int i=0; i<n; i++){
                nums1[i] =nums2[i];
            }
            return;
        }
        if(0 ==n){
            return;
        }

        //升序
        if(nums1[0]>nums1[m-1]){
            sortIncrese(nums1);
        }
        if(nums2[0]>nums2[n-1]){
            sortIncrese(nums2);
        }

        //insert nums2 to nums1
        int tmpIndex=m+n-1;
        int i=m-1;
        int j=n-1;
        while(i>=0 && j>=0){
            if(nums1[i]< nums2[j]){
                nums1[tmpIndex] =nums2[j];
                tmpIndex--;
                j--;
            }else{
                nums1[tmpIndex] =nums1[i];
                nums1[i] =nums1[0];
                tmpIndex--;
                i--;
            }
        }
        while(i>=0){
            nums1[tmpIndex] =nums1[i];
            tmpIndex--;
            i--;
        }
        while(j >=0){
            nums1[tmpIndex] =nums2[j];
            tmpIndex--;
            j--;
        }
    }

    public void setZeroes(int[][] matrix) {
        int rowSize =matrix.length;
        int columeSize = matrix[0].length;
        int[] needToZeroRows = new int[rowSize];
        int[] needToZeroColumus = new int[columeSize];

        //标记
        for(int i=0;i<rowSize; i++){
            for(int j=0; j<columeSize; j++){
                if(matrix[i][j] ==1){
                    needToZeroRows[i] =1;
                    needToZeroColumus[j]=1;
                }
            }
        }

        //设置值
        for(int i=0; i<rowSize; i++){
            if(needToZeroRows[i] ==1){
                for(int j=0; j<columeSize; j++){
                    matrix[i][j] =0;
                }
            }
        }
        for(int j=0; j<columeSize; j++){
            if(needToZeroColumus[j] ==0){
                for(int i=0; i<rowSize; i++){
                    matrix[i][j] =0;
                }
            }
        }

    }

    public int climbStairs(int n) {
        if(1 ==n){
            return 1;
        }else if(2 ==n){
            return 2;
        }
        int n1 =1;
        int n2 =2;
        for(int i=3; i<=n; i++){
            int newValue = n1 +n2; //n-2, n-1
            n1 =n2;
            n2 = newValue;
        }
        return n2;
    }

    public List<Interval> insertIntervals(List<Interval> intervals, Interval newInterval){
        if(null == newInterval){
            return intervals;
        }
        if(null == intervals || 0 ==intervals.size()){
            return Arrays.asList(newInterval);
        }

        List<Interval> results = new ArrayList<Interval>();
        for(int i=0; i<intervals.size(); i++){
            if(intervals.get(i).end <newInterval.start) {
                results.add(intervals.get(i));
            }else if(intervals.get(i).start >newInterval.end){
                results.add(newInterval);
                newInterval = intervals.get(i);

            }else{
                int newStart = intervals.get(i).start <newInterval.start ?intervals.get(i).start :newInterval.start;
                int newEnd = intervals.get(i).end >newInterval.end ?intervals.get(i).end :newInterval.end;
                newInterval = new Interval(newStart, newEnd);
            }

        }
        results.add(newInterval);
        return results;
    }

    public List<Interval> mergeIntervals(List<Interval> intervals) {
        if(null == intervals || intervals.size() <=1){
            return intervals;
        }
        intervals = sortIntervalIncrease(intervals);
        List<Interval> results = new ArrayList<Interval>();
        Interval tmp =intervals.get(0);
        for(int i=0; i<intervals.size(); i++){
            if(tmp.end <intervals.get(i).start){
                results.add(intervals.get(i));
                tmp =intervals.get(i);
            }else{
                int newEnd =  tmp.end >intervals.get(i).end ?tmp.end :intervals.get(i).end;
                tmp =new Interval(tmp.start, newEnd);
            }

        }
        return results;

    }

    private List<Interval> sortIntervalIncrease(List<Interval> intervals){
        if(null ==intervals || 0 ==intervals.size()){
            return intervals;
        }
        for(int i=intervals.size()-1; i>=0; i--){
            for(int j=0; j<i; j++){
                if(intervals.get(j).start >intervals.get(j+1).start){
                    Interval tmp = intervals.get(j);
                    intervals.set(j, intervals.get(j+1));
                    intervals.set(j+1, tmp);
                }
            }
        }
        return intervals;
    }

    public int strStr(String haystack, String needle){
        if(null ==needle || 0 ==needle.length()){
            return 0;
        }
        if(haystack.equals(needle)){
            return 0;
        }
        int i=0;
        while(i<=haystack.length()-needle.length()){
            int matchIndex =0;
            while(matchIndex <needle.length()){
                if(haystack.charAt(i +matchIndex) !=needle.charAt(matchIndex)){
                    matchIndex =0;
                    break;
                }
                matchIndex ++;
            }
            if(matchIndex == needle.length()){
                return i;
            }
            i++;
        }
        return -1;


    }

    private ListNode mergeTwoLists(ListNode l1, ListNode l2){
        if(null ==l1 && null ==l2){
            return null;
        }
        if(null == l1 ){
            return l2;
        }
        if(null == l2){
            return l1;
        }
        ListNode header =new ListNode(0);
        ListNode currentNode = header;
        while(true){
            if(null ==l1 && null ==l2){
                break;
            }
            if(null !=l1 && null !=l2){
                if(l1.val <= l2.val){
                    currentNode.next =l1;
                    currentNode =currentNode.next;
                    l1 =l1.next;
                }else{
                    currentNode.next =l2;
                    currentNode =currentNode.next;
                    l2 =l2.next;
                }
            }
            if(null ==l1){
                currentNode.next =l2;
                currentNode =currentNode.next;
                l2 =l2.next;
                continue;
            }
            if(null ==l2){
                currentNode.next =l1;
                currentNode =currentNode.next;
                l1 =l1.next;
                continue;
            }

        }
        return header.next;
    }


    private List<List<Integer>> threeNum(int[] nums) {
        this.sortIncrese(nums);

        List<List<Integer>> results = new ArrayList<List<Integer>>();
        for (int i = 0; i < nums.length - 2; i++) {
            if (nums[i] > 0) {
                return results;
            }
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            for (int j = i + 1; j < nums.length - 1; j++) {
                if (j > i + 1 && nums[j] == nums[j - 1]) {
                    continue;
                }
                if (nums[i] + nums[j] > 0) {
                    continue;
                }
                for (int k = j + 1; k < nums.length; k++) {
                    if (0 == nums[i] + nums[j] + nums[k]) {
                        List<Integer> result = new ArrayList<Integer>();
                        result.add(nums[i]);
                        result.add(nums[j]);
                        result.add(nums[k]);
                        results.add(result);
                        break;
                    }
                    if (nums[i] + nums[j] + nums[k] > 0) {
                        break;
                    }
                }
            }
        }
        return results;
    }

    private void sortIncrese(int[] nums) {
        if (null == nums || 0 == nums.length) {
            return;
        }

        for (int i = nums.length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (nums[j] > nums[j + 1]) {
                    int tmp = nums[j];
                    nums[j] = nums[j + 1];
                    nums[j + 1] = tmp;
                }
            }
        }
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + ",");
        }
        System.out.println();
    }

    private int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> matchMap = new HashMap<Integer, Integer>();//<value,index>
        for (int i = 0; i < nums.length; i++) {
            Integer firstIndex = matchMap.get(target - nums[i]);
            if (null == firstIndex) {
                matchMap.put(nums[i], i);
                continue;
            }
            return new int[]{firstIndex, i};
        }
        return new int[]{0, 0};
    }

    private boolean ValidParentheses(String s) {
        if (null == s || 0 == s.length()) {
            return false;
        }

        Stack<Character> stack = new Stack<Character>();
        char[] charList = s.toCharArray();
        for (int i = 0; i < charList.length; i++) {
            if ('(' == charList[i] || '{' == charList[i] || '[' == charList[i]) {
                stack.push(charList[i]);
                continue;
            }
            if (stack.isEmpty()) {
                return false;
            }
            Character leftChar = stack.pop();
            switch (leftChar) {
                case '(':
                    if (')' != charList[i]) {
                        return false;
                    }
                    break;
                case '{':
                    if ('}' != charList[i]) {
                        return false;
                    }
                    break;
                case '[':
                    if (']' != charList[i]) {
                        return false;
                    }
                    break;
                default:
                    return false;
            }

        }
        if (stack.isEmpty()) {
            return true;
        }
        return false;
    }



}
