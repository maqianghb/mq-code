//package com.example.mq.testcode.zk;
//
//import java.util.Objects;
//
//import com.example.mq.base.zk.CuratorClientManager;
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.framework.recipes.cache.NodeCache;
//import org.apache.curator.framework.recipes.cache.NodeCacheListener;
//import org.apache.curator.framework.recipes.cache.PathChildrenCache;
//import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
//import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
//import org.apache.zookeeper.CreateMode;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
///**
// * @program: mq-code
// * @description: zk客户端使用
// * @author: maqiang
// * @create: 2018/12/7
// *
// */
////@Component
//public class CuratorTest {
//	private static final Logger LOG = LoggerFactory.getLogger(CuratorTest.class);
//
//	@Autowired
//	private CuratorClientManager curatorClientManager;
//
//	private CuratorFramework zkClient =null;
//
//	public void test() throws Exception{
//		this.zkClient =curatorClientManager.getZkClient();
//		this.testCreateAndDeleteNode();
//		this.testWatch();
//	}
//
//	private void testCreateAndDeleteNode() throws Exception{
//		String testPath = "/test/testPath";
//		//测试
//		curatorClientManager.createNode(testPath, CreateMode.PERSISTENT);
//		curatorClientManager.deleteNode(testPath);
//		curatorClientManager.createNode(testPath, CreateMode.PERSISTENT_SEQUENTIAL);
//		curatorClientManager.deleteNode(testPath);
//		curatorClientManager.createNode(testPath, CreateMode.EPHEMERAL);
//		curatorClientManager.deleteNode(testPath);
//		curatorClientManager.createNode(testPath, CreateMode.EPHEMERAL_SEQUENTIAL);
//		curatorClientManager.deleteNode(testPath);
//		//清理
//		curatorClientManager.deleteNode("/test");
//	}
//
//	private void testWatch() throws Exception{
//		String testPath ="/test/watchNode";
//		//监听本节点的创建及数据变化的事件
//		final NodeCache nodeCache =new NodeCache(this.zkClient, testPath);
//		nodeCache.getListenable().addListener(new NodeCacheListener() {
//			@Override
//			public void nodeChanged() throws Exception {
//
//				if(!Objects.isNull(nodeCache.getCurrentData())){
//					System.out.println("------base in watchNode:" + new String(nodeCache.getCurrentData().getData()));
//				}
//			}
//		});
//		//初始化时获取node值并缓存
//		nodeCache.start(true);
//		if(Objects.isNull(this.zkClient.checkExists().forPath(testPath))){
//			this.zkClient.create().creatingParentsIfNeeded().forPath(testPath,"testWatchData".getBytes());
//		}
//
//		//监听子节点事件，包括创建、删除、数据更新
//		final PathChildrenCache childrenCache =new PathChildrenCache(this.zkClient, testPath, true);
//		childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
//			@Override
//			public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
//				switch (pathChildrenCacheEvent.getType()){
//					case CHILD_ADDED:
//						System.out.println("------add childNode:"+ pathChildrenCacheEvent.getData().getPath());
//						break;
//					case CHILD_UPDATED:
//						System.out.println("------base in childNode changed, path:" +pathChildrenCacheEvent.getData().getPath());
//						System.out.println("------ new base in childNode:" + new String(pathChildrenCacheEvent.getData().getData()));
//						break;
//					case CHILD_REMOVED:
//						System.out.println("------remove childNode:"+ pathChildrenCacheEvent.getData().getPath());
//						break;
//				}
//			}
//		});
//		childrenCache.start();
//
//		//测试
//		String childPath =testPath + "/childPath";
//		this.zkClient.create().forPath(childPath, "base".getBytes());
//		this.zkClient.setData().forPath(childPath, "newData".getBytes());
//		this.zkClient.delete().forPath(childPath);
//		this.zkClient.delete().deletingChildrenIfNeeded().forPath(testPath);
//	}
//
//}
