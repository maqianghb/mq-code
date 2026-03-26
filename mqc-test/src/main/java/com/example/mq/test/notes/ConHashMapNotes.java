//package com.example.mq.base.notes;
//
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ThreadLocalRandom;
//
//import com.example.mq.base.common.MyException;
//
///**
// * @program: mq-code
// * @description: concurrentHashMap源码阅读
// * @author: maqiang
// * @create: 2018/11/21
// *
// */
//
//public class ConHashMapNotes {
//
//	ConcurrentHashMap<String, MyException> testMap =new ConcurrentHashMap<>();
//
//
//	//最大容量
//	private static final int MAXIMUM_CAPACITY = 1 << 30;
//	//初始容量
//	private static final int DEFAULT_CAPACITY = 16;
//	//数组最大容量
//	static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
//	//默认并发度，兼容1.7及之前版本
//	private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
//	//加载/扩容因子，实际使用n - (n >>> 2)
//	private static final float LOAD_FACTOR = 0.75f;
//	//链表转红黑树的节点数阀值
//	static final int TREEIFY_THRESHOLD = 8;
//	//红黑树转链表的节点数阀值
//	static final int UNTREEIFY_THRESHOLD = 6;
//	//当数组长度还未超过64,优先数组的扩容,否则将链表转为红黑树
//	static final int MIN_TREEIFY_CAPACITY = 64;
//	//扩容时任务的最小转移节点数
//	private static final int MIN_TRANSFER_STRIDE = 16;
//	//sizeCtl中记录stamp的位数
//	private static int RESIZE_STAMP_BITS = 16;
//	//帮助扩容的最大线程数
//	private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;
//	//size在sizeCtl中的偏移量
//	private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;
//
//	//存放Node元素的数组,在第一次插入数据时初始化
//	transient volatile Node<K,V>[] table;
//	//一个过渡的table表,只有在扩容的时候才会使用
//	private transient volatile Node<K,V>[] nextTable;
//	//基础计数器值(size = baseCount + CounterCell[i].value)
//	private transient volatile long baseCount;
//	//控制table初始化和扩容操作
//	private transient volatile int sizeCtl;
//	//节点转移时下一个需要转移的table索引
//	private transient volatile int transferIndex;
//	//元素变化时用于控制自旋
//	private transient volatile int cellsBusy;
//	// 保存table中的每个节点的元素个数 2的幂次方
//	// size = baseCount + CounterCell[i].value
//	private transient volatile CounterCell[] counterCells;
//
//
//
//	/**
//	 * put操作类
//	 * @param key
//	 * @param value
//	 * @return
//	 */
//	public V put(K key, V value) {
//		return putVal(key, value, false);
//	}
//
//	/**
//	 * put操作，重复key不处理
//	 * @param key
//	 * @param value
//	 * @return
//	 */
//	public V putIfAbsent(K key, V value) {
//		return putVal(key, value, true);
//	}
//
//	/**
//	 * put操作基本类
//	 * @param key
//	 * @param value
//	 * @param onlyIfAbsent
//	 * @return
//	 */
//	final V putVal(K key, V value, boolean onlyIfAbsent) {
//		//concurrentHashMap要求key、value都不能为null, 原因在于计算node的hash值时二者都要用到
//		// 参考node的hashcode方法：public final int hashCode()   { return key.hashCode() ^ val.hashCode(); }
//		if (key == null || value == null) throw new NullPointerException();
//		//二次hash：内部掩码hash操作，便于后续通过hashcode的位与计算快速定位到某一段，这也是分段要满足2^n的原因之一
//		int hash = spread(key.hashCode());
//		int binCount = 0;
//		//循环直至put操作成功
//		for (ConcurrentHashMap.Node<K,V>[] tab = table;;) {
//			ConcurrentHashMap.Node<K,V> f; int n, i, fh;
//			if (tab == null || (n = tab.length) == 0)
//				//初始化桶链表，即concurrentHashMap将初始化延迟至第一次放入数据的时候
//				tab = initTable();
//			else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
//				//i：表示桶链表的桶id，f：桶的头节点
//				//f为空表示该桶无数据，CAS无锁插入数据
//				if (casTabAt(tab, i, null,
//						new ConcurrentHashMap.Node<K,V>(hash, key, value, null)))
//					break;                   // no lock when adding to empty bin
//			}
//			else if ((fh = f.hash) == MOVED)
//				//MOVED状态表示该段正在扩容
//				tab = helpTransfer(tab, f);
//			else {
//				//正常插入数据
//				V oldVal = null;
//				//使用synchronized是因为synchronized在jdk1.6后优化较多，性能大幅提高
//				synchronized (f) {
//					//重复检查该桶的首节点f是否未改变
//					if (tabAt(tab, i) == f) {
//						//fh = f.hash，大于等于0表示桶的状态正常且为链表，能插入数据
//						if (fh >= 0) {
//							//binCount在该桶为链表时记录链表长度，转红黑树之后标识为2
//							binCount = 1;
//							for (ConcurrentHashMap.Node<K,V> e = f;; ++binCount) {
//								K ek;
//								if (e.hash == hash &&
//										((ek = e.key) == key ||
//												(ek != null && key.equals(ek)))) {
//									oldVal = e.val;
//									//遇到重复节点，根据onlyIfAbsent判断是否更新为新值
//									//put操作onlyIfAbsent为false，会更新数据
//									//putIfAbsent操作onlyIfAbsent为true，直接跳过，保留旧值
//									if (!onlyIfAbsent)
//										e.val = value;
//									break;
//								}
//								ConcurrentHashMap.Node<K,V> pred = e;
//								if ((e = e.next) == null) {
//									pred.next = new ConcurrentHashMap.Node<K,V>(hash, key,
//											value, null);
//									break;
//								}
//							}
//						}
//						else if (f instanceof ConcurrentHashMap.TreeBin) {
//							//段为红黑树，则数据插入红黑树中
//							ConcurrentHashMap.Node<K,V> p;
//							binCount = 2;
//							if ((p = ((ConcurrentHashMap.TreeBin<K,V>)f).putTreeVal(hash, key,
//									value)) != null) {
//								oldVal = p.val;
//								if (!onlyIfAbsent)
//									p.val = value;
//							}
//						}
//					}
//				}
//				if (binCount != 0) {
//					if (binCount >= TREEIFY_THRESHOLD)
//						//TREEIFY_THRESHOLD:链表转红黑树的阈值，默认为8
//						//binCount>阈值，则链表转红黑树
//						treeifyBin(tab, i);
//					if (oldVal != null)
//						return oldVal;
//					break;
//				}
//			}
//		}
//		//size计数器加1，如果数据满了则扩容
//		addCount(1L, binCount);
//		return null;
//	}
//
//	/**
//	 *计数器加1，有必要时扩容
//	 * @param x
//	 * @param check
//	 */
//	private final void addCount(long x, int check) {
//		CounterCell[] as; long b, s;
//		if ((as = counterCells) != null ||
//				!U.compareAndSwapLong(this, BASECOUNT, b = baseCount, s = b + x)) {
//			CounterCell a; long v; int m;
//			boolean uncontended = true;
//			if (as == null || (m = as.length - 1) < 0 ||
//					(a = as[ThreadLocalRandom.getProbe() & m]) == null ||
//					!(uncontended =
//							U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))) {
//				fullAddCount(x, uncontended);
//				return;
//			}
//			if (check <= 1)
//				return;
//			s = sumCount();
//		}
//		if (check >= 0) {
//			Node<K,V>[] tab, nt; int n, sc;
//			while (s >= (long)(sc = sizeCtl) && (tab = table) != null &&
//					(n = tab.length) < MAXIMUM_CAPACITY) {
//				int rs = resizeStamp(n);
//				if (sc < 0) {
//					if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
//							sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
//							transferIndex <= 0)
//						break;
//					if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
//						transfer(tab, nt);
//				}
//				else if (U.compareAndSwapInt(this, SIZECTL, sc,
//						(rs << RESIZE_STAMP_SHIFT) + 2))
//					transfer(tab, null);
//				s = sumCount();
//			}
//		}
//	}
//
//
//	/**
//	 * 伪代码：
//	 * n = table.length
//	 * nextTable = new Node[2 * n]
//	 * forwardingNode = new ForwardingNode
//	 * forwardingNode.nextTable = nextTable;
//	 * for(table[i] : table){
//	 *     for(p = table[i]; p != null ; p = p.next){
//	 *         if(p.hash & n == 0)
//	 *             将p放入nextTable[i]的数据集合中
//	 *         else
//	 *             将p放入nextTable[i+n]的数据集合中
//	 *     }
//	 *     table[i] = forwardingNode;
//	 * }
//	 * table = nextTable;
//	 * nextTable = null;
//	 */
//	/**
//	 * 扩容，把所有节点从tab转到新的nextTab中
//	 * @param tab
//	 * @param nextTab
//	 */
//	private final void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab) {
//		//n:桶的总大小，tab:桶首节点构成的链表，类似与老版本的segment
//		int n = tab.length, stride;
//		//NCPU为CPU核数, stride大意是每个cpu处理的node数不小于16
//		if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
//			stride = MIN_TRANSFER_STRIDE; // subdivide range
//		//nextTab为空，表示桶尚未扩容
//		if (nextTab == null) {            // initiating
//			try {
//				@SuppressWarnings("unchecked")
//				Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1];
//				//nt为新创建的链表，长度为原桶长度的2倍
//				nextTab = nt;
//			} catch (Throwable ex) {      // try to cope with OOME
//				sizeCtl = Integer.MAX_VALUE;
//				return;
//			}
//			nextTable = nextTab;
//			transferIndex = n;
//		}
//		//扩容后桶的长度
//		int nextn = nextTab.length;
//		//标志节点，指向nextTab，提供find功能
//		ForwardingNode<K,V> fwd = new ForwardingNode<K,V>(nextTab);
//		boolean advance = true;
//		boolean finishing = false; // to ensure sweep before committing nextTab
//		//死循环操作
//		for (int i = 0, bound = 0;;) {
//			//i表示执行table中第i个节点，transferIndex为每个线程执行transfer时的index
//			Node<K,V> f; int fh;
//			while (advance) {
//				int nextIndex, nextBound;
//				if (--i >= bound || finishing)
//					advance = false;
//				else if ((nextIndex = transferIndex) <= 0) {
//					i = -1;
//					advance = false;
//				}
//				else if (U.compareAndSwapInt
//						(this, TRANSFERINDEX, nextIndex,
//								nextBound = (nextIndex > stride ?
//										nextIndex - stride : 0))) {
//					bound = nextBound;
//					i = nextIndex - 1;
//					advance = false;
//				}
//			}
//			//第一次时i=nextIndex-1, nextIndex=transferIndex=n=tab.length, 即i是原有数组长度减去1
//			if (i < 0 || i >= n || i + n >= nextn) {
//				int sc;
//				if (finishing) {
//					nextTable = null;
//					table = nextTab;
//					sizeCtl = (n << 1) - (n >>> 1);
//					return;
//				}
//				if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
//					if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
//						return;
//					finishing = advance = true;
//					i = n; // recheck before commit
//				}
//			}
//			else if ((f = tabAt(tab, i)) == null)
//				advance = casTabAt(tab, i, null, fwd);
//			else if ((fh = f.hash) == MOVED)
//				//如果节点的哈希值为MOVED表示已经处理过了，则处理其他桶
//				advance = true; // already processed
//			else {
//				synchronized (f) {
//					//进行transfer操作
//					//再次判断桶节点是否变化
//					if (tabAt(tab, i) == f) {
//						//ln表示低位节点，hn表示高位节点，扩容后根据 h&(n-1)来计算
//						//ln位置不动，hn移动扩容
//						Node<K,V> ln, hn;
//						if (fh >= 0) {
//							int runBit = fh & n;
//							Node<K,V> lastRun = f;
//							for (Node<K,V> p = f.next; p != null; p = p.next) {
//								int b = p.hash & n;
//								if (b != runBit) {
//									runBit = b;
//									lastRun = p;
//								}
//							}
//							if (runBit == 0) {
//								ln = lastRun;
//								hn = null;
//							}
//							else {
//								hn = lastRun;
//								ln = null;
//							}
//							//从f节点开始遍历，计算ln和fn
//							//注：p.hash&n来决定原node在扩容后的位置，是在高位还是低位(i+n和i的位置)
//							for (Node<K,V> p = f; p != lastRun; p = p.next) {
//								int ph = p.hash; K pk = p.key; V pv = p.val;
//								if ((ph & n) == 0)
//									ln = new Node<K,V>(ph, pk, pv, ln);
//								else
//									hn = new Node<K,V>(ph, pk, pv, hn);
//							}
//							//插入数据
//							setTabAt(nextTab, i, ln);
//							setTabAt(nextTab, i + n, hn);
//							setTabAt(tab, i, fwd);
//							advance = true;
//						}
//						else if (f instanceof TreeBin) {
//							TreeBin<K,V> t = (TreeBin<K,V>)f;
//							TreeNode<K,V> lo = null, loTail = null;
//							TreeNode<K,V> hi = null, hiTail = null;
//							int lc = 0, hc = 0;
//							for (Node<K,V> e = t.first; e != null; e = e.next) {
//								int h = e.hash;
//								TreeNode<K,V> p = new TreeNode<K,V>
//										(h, e.key, e.val, null, null);
//								if ((h & n) == 0) {
//									if ((p.prev = loTail) == null)
//										lo = p;
//									else
//										loTail.next = p;
//									loTail = p;
//									++lc;
//								}
//								else {
//									if ((p.prev = hiTail) == null)
//										hi = p;
//									else
//										hiTail.next = p;
//									hiTail = p;
//									++hc;
//								}
//							}
//							ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
//									(hc != 0) ? new TreeBin<K,V>(lo) : t;
//							hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
//									(lc != 0) ? new TreeBin<K,V>(hi) : t;
//							setTabAt(nextTab, i, ln);
//							setTabAt(nextTab, i + n, hn);
//							setTabAt(tab, i, fwd);
//							advance = true;
//						}
//					}
//				}
//			}
//		}
//	}
//
//
//
//}
