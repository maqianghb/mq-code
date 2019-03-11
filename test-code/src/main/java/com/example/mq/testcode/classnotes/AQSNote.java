//package com.example.mq.testcode.classnotes;
//
//import java.util.concurrent.locks.LockSupport;
//
///**
// * @program: mq-code
// * @description: AQS相关笔记
// * @author: maqiang
// * @create: 2019/3/5
// *
// */
//
//public class AQSNote {
//
//	//等待队列的头结点，慢加载，等待队列中的头节点都是获取到锁的线程的节点
//	private transient volatile Node head;
//
//	//等待队列的尾节点，慢加载
//	private transient volatile Node tail;
//
//	//AQS维护的状态值，表示当前线程获取锁的次数，即为共享资源
//	private volatile int state;
//
//	//线程自旋的等待时间，单位是纳秒
//	static final long spinForTimeoutThreshold = 1000L;
//
//
//
//	/**
//	 * 独占模式下获取资源，未获取时进入等待队列直至成功拿到共享资源
//	 * @param arg
//	 */
//	public final void acquire(int arg) {
//		if (!tryAcquire(arg) &&
//				acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
//			selfInterrupt();
//	}
//
//	protected boolean tryAcquire(int arg) {
//		throw new UnsupportedOperationException();
//		//待自己实现具体内容
//	}
//
//	private Node addWaiter(Node mode) {
//		//mode取值有两种：SHARED、EXCLUSIVE
//		Node node = new Node(Thread.currentThread(), mode);
//
//		//先快速放到对味，失败则通过enq()入队
//		Node pred = tail;
//		if (pred != null) {
//			node.prev = pred;
//			if (compareAndSetTail(pred, node)) {
//				pred.next = node;
//				return node;
//			}
//		}
//		//入队
//		enq(node);
//		return node;
//	}
//
//	private Node enq(final Node node) {
//		//死循环操作，直到成功
//		for (;;) {
//			Node t = tail;
//			if (t == null) {
//				//队列为空，创建空节点为head节点，并将tail指向head
//				if (compareAndSetHead(new Node()))
//					tail = head;
//			} else {
//				//正常CAS放入队尾
//				node.prev = t;
//				if (compareAndSetTail(t, node)) {
//					t.next = node;
//					return t;
//				}
//			}
//		}
//	}
//
//
//	/**
//	 * 1. 进入队尾并检查状态，看是否能进入waiting状态，
//	 * 2. 调用parking()方法进入waiting状态，等待unpark()或interrupt()唤醒自己；
//	 * 3. 唤醒后看是否能拿到资源，如果取到资源则返回中断状态，未取到则继续进入waiting()状态
//	 * @param node
//	 * @param arg
//	 * @return 被中断返回true，且如果被中断是不响应的，要在tryAcquire()后进行自我中断操作selfInterrupt()，将中断补上
//	 */
//	final boolean acquireQueued(final Node node, int arg) {
//		//标记是否成功拿到资源
//		boolean failed = true;
//		try {
//			//等待过程中是否被中断标识
//			boolean interrupted = false;
//			//死循环一直尝试
//			for (;;) {
//				final Node p = node.predecessor();
//				//前面没有排队node，即可以尝试获取资源
//				if (p == head && tryAcquire(arg)) {
//					setHead(node);
//					p.next = null; // help GC
//					failed = false;
//					return interrupted;
//				}
//				//如果未获取资源，则进入waiting状态，直至被unpark()
//				if (shouldParkAfterFailedAcquire(p, node) &&
//						parkAndCheckInterrupt())
//					interrupted = true;
//			}
//		} finally {
//			if (failed)
//				cancelAcquire(node);
//		}
//	}
//
//	/**
//	 *检查状态并判断节点对应的thread是否能进入waiting状态
//	 * @param pred
//	 * @param node
//	 * @return
//	 */
//	private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
//		int ws = pred.waitStatus;
//		if (ws == Node.SIGNAL)
//			/*
//			 * This node has already set status asking a release
//			 * to signal it, so it can safely park.
//			 */
//			//如果已经告诉前节点在释放资源后通知自己，则进入waiting状态
//			return true;
//		if (ws > 0) {
//			/*
//			 * Predecessor was cancelled. Skip over predecessors and
//			 * indicate retry.
//			 */
//			//如果前节点被取消，则一直向前找到一个状态“正常”的节点，并重新设置节点引用
//			do {
//				node.prev = pred = pred.prev;
//			} while (pred.waitStatus > 0);
//			pred.next = node;
//		} else {
//			/*
//			 * waitStatus must be 0 or PROPAGATE.  Indicate that we
//			 * need a signal, but don't park yet.  Caller will need to
//			 * retry to make sure it cannot acquire before parking.
//			 */
//			//如果前节点状态“正常”，则设置前节点状态为SIGNAL
//			compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
//		}
//		return false;
//	}
//
//	private final boolean parkAndCheckInterrupt() {
//		//调用park()方法使线程进入waiting状态
//		LockSupport.park(this);
//		//被唤醒时查看自己是否被中断
//		return Thread.interrupted();
//	}
//
//
//
//	//节点数据结构
//	static final class Node {
//		//共享模式下的节点，单例
//		static final Node SHARED = new Node();
//		//独占模式下的节点
//		static final Node EXCLUSIVE = null;
//
//		//当前线程被取消
//		static final int CANCELLED =  1;
//		//当前节点的后继节点所包含的线程需要运行
//		static final int SIGNAL    = -1;
//		//当前节点正在等待condition
//		static final int CONDITION = -2;
//		//当前场景下后续的acquireShared能够得以执行
//		static final int PROPAGATE = -3;
//
//
//		//当前节点在sync队列中，等待着获取锁
//		volatile int waitStatus;
//
//		//前驱节点
//		volatile Node prev;
//
//		//后节点
//		volatile Node next;
//
//		//当前节点对应的线程
//		volatile Thread thread;
//
//		//存储condition队列中的后继节点
//		Node nextWaiter;
//
//		/**
//		 * Returns true if node is waiting in shared mode.
//		 */
//		final boolean isShared() {
//			return nextWaiter == SHARED;
//		}
//
//		final Node predecessor() throws NullPointerException {
//			Node p = prev;
//			if (p == null)
//				throw new NullPointerException();
//			else
//				return p;
//		}
//
//		//用于构造列表头节点或共享节点
//		Node() {    // Used to establish initial head or SHARED marker
//		}
//
//		//构造等待队列
//		Node(Thread thread, Node mode) {     // Used by addWaiter
//			this.nextWaiter = mode;
//			this.thread = thread;
//		}
//
//		//用于构造condition
//		Node(Thread thread, int waitStatus) { // Used by Condition
//			this.waitStatus = waitStatus;
//			this.thread = thread;
//		}
//	}
//
//
//}
