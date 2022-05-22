package com.example.mq.base.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

/**
 * @program: mq-code
 * @description: mongoService
 * @author: maqiang
 * @create: 2018/11/15
 *
 */

public interface MongoService {

	/**
	 * 获取某个集合对象
	 * @param collection
	 * @return
	 */
	MongoCollection<Document> getCollection(String collection);



	/**
	 * 向某个集合插入document
	 * @param json
	 * @param collection
	 */
	void insert(String json, String collection);

	/**
	 * 向某个集合根据id插入document
	 * @param id
	 * @param json
	 * @param collection
	 */
	void insertById(Object id, String json, String collection);

	/**
	 * 向集合根据key匹配到的记录更新document
	 * @param key
	 * @param value
	 * @param json
	 * @param collection
	 * @return
	 */
	long saveOrUpdate(String key, Object value, String json, String collection);

	/**
	 * 根据id进行插入或者更新
	 * @param id
	 * @param json
	 * @param collection
	 * @return
	 */
	long saveOrUpdateById(Object id, String json, String collection);

	/**
	 * 批量更新document
	 * @param json
	 * @param collection
	 * @return
	 */
	long batchUpdate(String json, Map<String, Object> options, String collection);

	/**
	 * 根据主键id删除document
	 * 返回值：1为删除成功，0为删除失败
	 * @param id
	 * @param collection
	 * @return
	 */
	long deleteById(Object id, String collection);

	long deleteByObjectId(ObjectId id, String collection);

	/**
	 *
	 * @param collectionName
	 * @param keys
	 * @return
	 */
	Integer countByOptions(String collectionName, Map<String, Object> keys);

	/**
	 * 查询集合中所有的document
	 * @param collectionName
	 * @return
	 */
	List<Document> getAll(String collectionName);

	/**
	 * 对某个集合根据id查询document
	 * @param id
	 * @param collection
	 * @return
	 */
	Document getById(Object id, String collection);

	/**
	 * 多条件查询
	 * @param collection
	 * @param options
	 * @return
	 */
	List<Document> getDocumentsByOptions(String collection, Map<String, Object> options);

	/**
	 *
	 * @param collectionName
	 * @param keys
	 * @param sort
	 * @return
	 */
	List<Document> getDocumentsAndSortByOptions(String collectionName, Map<String, Object> keys,
			Map<String, Integer> sort);


	/**
	 *
	 * @param iterable
	 * @return
	 */
	List<Document> iter(FindIterable<Document> iterable);



}
