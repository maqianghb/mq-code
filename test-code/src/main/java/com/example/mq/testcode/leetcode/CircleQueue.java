package com.example.mq.testcode.leetcode;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/9/19
 *
 */

public class CircleQueue <T> {

	private Node current =null;

	private Node first =null;
	private Node end =null;
	private Integer num =0;

	public boolean add(T value){
		Node<T> newNode =new Node<>(value, end, first);
		if(null ==current){
			//空队列
			newNode.prev =newNode;
			newNode.next =newNode;

			current =newNode;
			first =newNode;
			end =newNode;
		}else{
			//非空队列
			end.next =newNode;
			first.prev =newNode;

			end =newNode;
		}
		num ++;
		return true;
	}

	private boolean remove(){

	}

	private T currentValue(){
		if(current ==null){
			return null;
		}
		return (T) current.item;
	}

	private T next(){
		if(current ==null){
			return null;
		}
		current =current.next;
		return (T)current.item;
	}

	private T prev(){
		if(current ==null){
			return null;
		}
		current =current.prev;
		return (T)current.item;
	}


	private class Node<T>{
		T item;
		Node<T> prev;
		Node<T> next;

		public Node(T item, Node<T> prev, Node<T> next) {
			this.item = item;
			this.prev = prev;
			this.next = next;
		}
	}

	public static void main(String[] args) {
		CircleQueue<Integer> circleQueue =new CircleQueue<>();
		circleQueue.add(1);
		circleQueue.add(2);
		circleQueue.add(3);
		circleQueue.add(4);

		for(int i=0; i<10; i++){
			System.out.println("item:" + circleQueue.currentValue());
			circleQueue.next();
		}
	}
}
