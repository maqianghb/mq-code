package com.example.mq.data.zk;

import java.util.List;
import java.util.Objects;

import com.example.mq.data.common.MyException;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: mq-code
 * @description: CuratorClient
 * @author: maqiang
 * @create: 2018/12/7
 *
 */
@Component
public class CuratorClientManager {
	private static Logger LOG = LoggerFactory.getLogger(CuratorClientManager.class);

	private volatile static CuratorFramework ZK_CLIENT =null;

	@Autowired
	private ZkConfig zkConfig;

	public CuratorFramework getZkClient(){
		if(Objects.isNull(ZK_CLIENT)){
			synchronized (CuratorFramework.class){
				if(Objects.isNull(ZK_CLIENT)){
					ZK_CLIENT = CuratorFrameworkFactory.builder()
							.connectString(zkConfig.getZkAddress())
							.sessionTimeoutMs(zkConfig.getSessionTimeoutMs())
							.connectionTimeoutMs(zkConfig.getConnectionTimeoutMs())
							.retryPolicy(new ExponentialBackoffRetry(zkConfig.getSleepTime(), zkConfig.getMaxRetries()))
							.namespace(zkConfig.getNamespace())
							.build();
					ZK_CLIENT.start();
				}
			}
		}
		return ZK_CLIENT;
	}

	public boolean isNodeExist(String zkPath) throws Exception{
		if(StringUtils.isEmpty(zkPath)){
			throw new IllegalArgumentException("参数为空！");
		}
		Stat stat =this.getZkClient().checkExists().forPath(zkPath);
		return Objects.isNull(stat) ? false:true;
	}

	public int createNode(String zkPath, CreateMode createMode) throws Exception{
		if(StringUtils.isEmpty(zkPath) || Objects.isNull(createMode)){
			throw new IllegalArgumentException("参数为空！");
		}
		if(!this.isNodeExist(zkPath)){
			this.getZkClient().create()
					.creatingParentContainersIfNeeded()
					.withMode(createMode)
					.forPath(zkPath);
		}
		return 1;
	}

	public int deleteNode(String zkPath) throws Exception{
		if(StringUtils.isEmpty(zkPath)){
			throw new IllegalArgumentException("参数为空！");
		}
		if(this.isNodeExist(zkPath)){
			this.getZkClient().delete().deletingChildrenIfNeeded().forPath(zkPath);
		}
		return 1;
	}

	public Stat update(String zkPath, byte[] bytes) throws Exception{
		if(StringUtils.isEmpty(zkPath) || Objects.isNull(bytes)){
			throw new IllegalArgumentException("参数为空！");
		}
		if(!this.isNodeExist(zkPath)){
			throw new MyException(String.format("zk节点不存在，zkPath:%s", zkPath));
		}
		return this.getZkClient().setData().forPath(zkPath, bytes);
	}

	public int saveOrUpdate(String zkPath, byte[] bytes, CreateMode createMode) throws Exception{
		if(StringUtils.isEmpty(zkPath) || Objects.isNull(bytes) || Objects.isNull(createMode)){
			throw new IllegalArgumentException("参数为空！");
		}
		if(!this.isNodeExist(zkPath)){
			return this.createNode(zkPath, createMode);
		}
		Stat stat =this.getZkClient().setData().forPath(zkPath, bytes);
		return Objects.isNull(stat) ? 0:1;
	}

	public byte[] getData(String zkPath) throws Exception{
		if(StringUtils.isEmpty(zkPath)){
			throw new IllegalArgumentException("参数为空！");
		}
		return this.getZkClient().getData().forPath(zkPath);
	}

	public List<String> getChildren(String zkPath) throws Exception{
		if(StringUtils.isEmpty(zkPath)){
			throw new IllegalArgumentException("参数为空！");
		}
		return this.getZkClient().getChildren().forPath(zkPath);
	}

	public void watchNode(String zkPath, NodeCacheListener listener) throws Exception{
		if(StringUtils.isEmpty(zkPath) || Objects.isNull(listener)){
			throw new IllegalAccessException("参数为空！");
		}
		if(!this.isNodeExist(zkPath)){
			throw new MyException(String.format("zk节点不存在，zkPath:%s", zkPath));
		}
		//监听本节点的创建及数据变化的事件
		NodeCache nodeCache =new NodeCache(this.getZkClient(), zkPath);
		nodeCache.getListenable().addListener(listener);
		//初始化时获取node值并缓存
		nodeCache.start(true);
	}

	public void watchChildrens(String zkPath, PathChildrenCacheListener listener) throws Exception{
		if(StringUtils.isEmpty(zkPath) || Objects.isNull(listener)){
			throw new IllegalAccessException("参数为空！");
		}
		if(!this.isNodeExist(zkPath)){
			throw new MyException(String.format("zk节点不存在，zkPath:%s", zkPath));
		}
		//监听子节点事件，包括创建、删除、数据更新
		PathChildrenCache childrenCache =new PathChildrenCache(this.getZkClient(), zkPath, true);
		childrenCache.getListenable().addListener(listener);
		childrenCache.start(true);
	}
}
