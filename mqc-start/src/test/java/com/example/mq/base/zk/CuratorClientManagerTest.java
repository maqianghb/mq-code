package com.example.mq.base.zk;

import com.example.mq.controller.ControllerApplication;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/2/27
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ControllerApplication.class)
public class CuratorClientManagerTest {

	@Autowired
	private CuratorClientManager curatorClientManager;

	private CuratorFramework zkClient =null;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void getZkClient() {
	}

	@Test
	public void isNodeExist() {
	}

	@Test
	public void createNode() {
	}

	@Test
	public void deleteNode() {
	}

	@Test
	public void update() {
	}

	@Test
	public void saveOrUpdate() {
	}

	@Test
	public void getData() {
	}

	@Test
	public void getChildren() {
	}

	@Test
	public void watchNode() {
	}

	@Test
	public void watchChildrens() {
	}
}