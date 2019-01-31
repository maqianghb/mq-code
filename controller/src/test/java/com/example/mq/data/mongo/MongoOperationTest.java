package com.example.mq.data.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.data.util.SnowflakeIdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: penalty-platform
 * @description: mongoTest类
 * @author: maqiang
 * @create: 2018/11/23
 *
 */
@Service("mongoOperationTest")
public class MongoOperationTest {
	private final Logger LOG = LoggerFactory.getLogger(MongoOperationTest.class);

	private final String TEST_COLLECTION ="mq_test";
	private final int TEST_NUMBER =1000;

	private static final SnowflakeIdWorker idWorker =new SnowflakeIdWorker(10L, 11L);

	@Autowired
	private MongoService mongoService;

	public void testIndexQuery(){

	}

	public void testIdOperation(){
		int queryErrNum =0;
		int updateErrNum =0;
		int delErrNum =0;
		List<Long> ids =new ArrayList<>(TEST_NUMBER);
		for(int i=0; i<TEST_NUMBER; i++){
			ids.add(idWorker.nextId());
		}

		//插入测试
		Long startTime = System.currentTimeMillis();
		for(Long id : ids){
			Map<String, Object> testMap =new HashMap<>();
			testMap.put("01_id", id);
			testMap.put("02_strId", String.valueOf(id));
			mongoService.insertById(id, JSONObject.toJSONString(testMap), TEST_COLLECTION);
		}
		int insertCount =mongoService.countByOptions(TEST_COLLECTION, new HashMap<>());
		LOG.info("------id插入测试结果, totalTestNum:{}|insertCount:{}|costTime:{}",
				TEST_NUMBER, insertCount, System.currentTimeMillis()-startTime);

		//更新测试
		startTime =System.currentTimeMillis();
		for(Long id : ids){
			Map<String, Object> testMap =new HashMap<>();
			testMap.put("03_updateFlag", 1);
			if(mongoService.updateById(id, JSONObject.toJSONString(testMap), TEST_COLLECTION) <=0){
				updateErrNum ++;
				LOG.info("id更新失败，collection:{}|id:{}|id_Type:{}", TEST_COLLECTION, id, id.getClass().getTypeName());
			}
		}
		Map<String, Object> options =new HashMap<>(2);
		options.put("03_updateFlag", 1);
		int updateCount =mongoService.countByOptions(TEST_COLLECTION, options);
		LOG.info("------id更新测试结果, totalTestNum:{}|updateCount:{}|costTime:{}",
				TEST_NUMBER, updateCount, System.currentTimeMillis()-startTime);

		//查询测试
		startTime =System.currentTimeMillis();
		for(Long id : ids){
			if(Objects.isNull(mongoService.getById(id, TEST_COLLECTION))){
				queryErrNum ++;
				LOG.info("id查询失败，collection:{}|id:{}|id_Type:{}", TEST_COLLECTION, id, id.getClass().getTypeName());
			}
		}
		LOG.info("------id查询测试结果, totalTestNum:{}|costTime:{}",
				TEST_NUMBER, System.currentTimeMillis()-startTime);

		//删除测试
		startTime =System.currentTimeMillis();
		for(Long id : ids){
			if(mongoService.deleteById(id, TEST_COLLECTION) <=0){
				delErrNum ++;
				LOG.info("id删除失败，collection:{}|id:{}|id_Type:{}", TEST_COLLECTION, id, id.getClass().getTypeName());
			}
		}
		int recordNum =mongoService.countByOptions(TEST_COLLECTION, new HashMap<>());
		LOG.info("------id删除测试结果, totalTestNum:{}|deleteNum:{}|costTime:{}",
				TEST_NUMBER, TEST_NUMBER -recordNum, System.currentTimeMillis()-startTime);

		LOG.info("------id测试结果统计, testNum:{}|queryErrNum:{}|updateErrNum:{}|delErrNum:{}",
				TEST_NUMBER, queryErrNum, updateErrNum, delErrNum);
	}

	public void testStrIdOperation(){
		int queryErrNum =0;
		int updateErrNum =0;
		int delErrNum =0;
		List<String> strIds =new ArrayList<>(TEST_NUMBER);
		for(int i=0; i<TEST_NUMBER; i++){
			strIds.add(String.valueOf(idWorker.nextId()));
		}

		//插入测试
		Long startTime = System.currentTimeMillis();
		for(String strId : strIds){
			Map<String, Object> testMap =new HashMap<>();
			testMap.put("01_id", Long.valueOf(strId));
			testMap.put("02_strId", strId);
			mongoService.insertById(strId, JSONObject.toJSONString(testMap), TEST_COLLECTION);
		}
		int insertCount =mongoService.countByOptions(TEST_COLLECTION, new HashMap<>());
		LOG.info("------strId插入测试结果, totalTestNum:{}|insertCount:{}|costTime:{}",
				TEST_NUMBER, insertCount, System.currentTimeMillis()-startTime);

		//更新测试
		startTime =System.currentTimeMillis();
		for(String id : strIds){
			Map<String, Object> testMap =new HashMap<>();
			testMap.put("03_updateFlag", 1);
			if(mongoService.updateById(id, JSONObject.toJSONString(testMap), TEST_COLLECTION) <=0){
				updateErrNum ++;
				LOG.info("strId更新失败，collection:{}|id:{}|id_Type:{}", TEST_COLLECTION, id, id.getClass().getTypeName());
			}
		}
		Map<String, Object> options =new HashMap<>(2);
		options.put("03_updateFlag", 1);
		int updateCount =mongoService.countByOptions(TEST_COLLECTION, options);
		LOG.info("------strId更新测试结果, totalTestNum:{}|updateCount:{}|costTime:{}",
				TEST_NUMBER, updateCount, System.currentTimeMillis()-startTime);

		//查询测试
		startTime =System.currentTimeMillis();
		for(String id : strIds){
			if(Objects.isNull(mongoService.getById(id, TEST_COLLECTION))){
				queryErrNum ++;
				LOG.info("strId查询失败，collection:{}|id:{}|id_Type:{}", TEST_COLLECTION, id, id.getClass().getTypeName());
			}
		}
		LOG.info("------strId查询测试结果, totalTestNum:{}|costTime:{}",
				TEST_NUMBER, System.currentTimeMillis()-startTime);

		//删除测试
		startTime =System.currentTimeMillis();
		for(String id : strIds){
			if(mongoService.deleteById(id, TEST_COLLECTION) <=0){
				delErrNum ++;
				LOG.info("strId删除失败，collection:{}|id:{}|id_Type:{}", TEST_COLLECTION, id, id.getClass().getTypeName());
			}
		}
		int recordNum =mongoService.countByOptions(TEST_COLLECTION, new HashMap<>());
		LOG.info("------strId删除测试结果, totalTestNum:{}|deleteNum:{}|costTime:{}",
				TEST_COLLECTION, TEST_NUMBER -recordNum, System.currentTimeMillis()-startTime);


		LOG.info("------strId测试结果统计, testNum:{}|queryErrNum:{}|updateErrNum:{}|delErrNum:{}",
				TEST_NUMBER, queryErrNum, updateErrNum, delErrNum);
	}
}