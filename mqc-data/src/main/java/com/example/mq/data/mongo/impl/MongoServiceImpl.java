package com.example.mq.data.mongo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.example.mq.data.mongo.MongoService;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/15
 *
 */
@Service
public class MongoServiceImpl implements MongoService{
	private static final Logger LOG = LoggerFactory.getLogger(MongoService.class);

	/**
	 * mongo默认id的key
	 */
	private final String DEF_ID_KEY = "_id";

	@Autowired
	@Qualifier("mongoDatabase")
	private MongoDatabase dbClient;


	@Override
	public MongoCollection<Document> getCollection(String collection) {
		MongoCollection<Document> docs = null;
		try {
			docs = dbClient.getCollection(collection);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return docs;
	}

	@Override
	public void insert(String json, String collection) {
		MongoCollection<Document> col = dbClient.getCollection(collection);
		Document doc = Document.parse(json);
		col.insertOne(doc);
	}


	@Override
	public void insertById(Object id, String json, String collection) {
		MongoCollection<Document> col = dbClient.getCollection(collection);
		Document doc = Document.parse(json);
		Document document = doc.append(DEF_ID_KEY, id);
		col.insertOne(document);
	}

	@Override
	public long saveOrUpdate(String key, Object value, String json, String collection) {
		Document document = null;
		try {
			MongoCollection<Document> col = dbClient.getCollection(collection);
			document = col.findOneAndUpdate(Filters.eq(key, value), new Document("$set", Document.parse(json)),
					new FindOneAndUpdateOptions().upsert(true));
		} catch (Exception e) {
			LOG.error("update err, key:{}|value:{}|json:{}|collection:{}", key, value, json, collection);
		}
		if(Objects.isNull(document)){
			return 0;
		}
		return 1;
	}

	@Override
	public long saveOrUpdateById(Object id, String json, String collection) {
		return saveOrUpdate(DEF_ID_KEY, id, json, collection);
	}

	@Override
	public long batchUpdate(String json, Map<String, Object> options, String collection) {
		Document document = null;
		try {
			MongoCollection<Document> col = dbClient.getCollection(collection);
			BasicDBObject condition = new BasicDBObject();
			for (Map.Entry<String, Object> entry : options.entrySet()) {
				condition.append(entry.getKey(), entry.getValue());
			}
			UpdateResult result = col.updateMany(condition, Document.parse(json));
			return result.getModifiedCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public long deleteById(Object id, String collection) {
		if(null ==id || StringUtils.isEmpty(collection)){
			throw new IllegalArgumentException("参数为空！");
		}
		DeleteResult result = null;
		try {
			MongoCollection<Document> col = dbClient.getCollection(collection);
			result = col.deleteOne(Filters.eq(DEF_ID_KEY, id));
		} catch (Exception e) {
			LOG.error("deleteById err, collection:{}|id:{}", collection, id);
		}
		long count = result.getDeletedCount();
		return count;
	}

	@Override
	public long deleteByObjectId(ObjectId id, String collection) {
		return deleteById(id, collection);
	}

	@Override
	public Integer countByOptions(String collectionName, Map<String, Object> keys) {
		List<Document> documents = new ArrayList<Document>();
		try {
			MongoCollection<Document> col = dbClient.getCollection(collectionName);
			BasicDBObject condition = new BasicDBObject();
			for (String key : keys.keySet()) {
				condition.put(key, keys.get(key));
			}
			long count = col.count(condition);
			return (int) count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public List<Document> getAll(String collectionName) {
		List<Document> list = null;
		try {
			MongoCollection<Document> collection = dbClient.getCollection(collectionName);
			FindIterable<Document> iterable = collection.find();
			MongoCursor<Document> cursor = iterable.iterator();
			list = new ArrayList<Document>();

			while (cursor.hasNext()) {
				list.add(cursor.next());
				//输出集合中所有document
				// System.out.println(cursor.next());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public Document getById(Object id, String collection) {
		if(null ==id || StringUtils.isEmpty(collection)){
			throw new IllegalArgumentException("参数为空！");
		}
		Document doc = null;
		try {
			MongoCollection<Document> col = dbClient.getCollection(collection);
			MongoCursor<Document> cursor = col.find(Filters.eq(DEF_ID_KEY, id)).iterator();
			if (cursor.hasNext()) {
				doc = cursor.next();
			}
		} catch (Exception e) {
			LOG.error(" getById err, collection:{}|id:{}", collection, id);
		}
		return doc;
	}

	@Override
	public List<Document> getDocumentsByOptions(String collection, Map<String, Object> options) {
		List<Document> documents = new ArrayList<Document>();
		try {
			MongoCollection<Document> col = dbClient.getCollection(collection);
			BasicDBObject condition = new BasicDBObject();
			for (Map.Entry<String, Object> entry : options.entrySet()) {
				condition.append(entry.getKey(), entry.getValue());
			}
			FindIterable<Document> findIterable = col.find(condition);
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				documents.add(mongoCursor.next());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return documents;
	}

	@Override
	public List<Document> getDocumentsAndSortByOptions(String collectionName, Map<String, Object> keys,
			Map<String, Integer> sort) {
		List<Document> documents = new ArrayList<Document>();
		try {
			MongoCollection<Document> col = dbClient.getCollection(collectionName);
			BasicDBObject condition = new BasicDBObject();
			for (String key : keys.keySet()) {
				condition.put(key, keys.get(key));
			}
			BasicDBObject sortContion = new BasicDBObject();
			for (String key : sort.keySet()) {
				sortContion.put(key, sort.get(key));
			}
			FindIterable<Document> findIterable = col.find(condition).sort(sortContion);
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				documents.add(mongoCursor.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documents;
	}

	@Override
	public List<Document> iter(FindIterable<Document> iterable) {
		List<Document> documents = new ArrayList<Document>();
		try {
			MongoCursor<Document> mongoCursor = iterable.iterator();
			while (mongoCursor.hasNext()) {
				documents.add(mongoCursor.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documents;
	}

	public List<Document> getDocumentsByOptionsSortLimit(String collectionName, Map<String, Object> keys, Map<String, Integer> sort, Integer skip, Integer limit) {
		List<Document> documents = new ArrayList<Document>();
		try {
			MongoCollection<Document> col = dbClient.getCollection(collectionName);
			BasicDBObject condition = new BasicDBObject();
			BasicDBObject sortContion = new BasicDBObject();
			for (String key : keys.keySet()) {
				condition.put(key, keys.get(key));
			}
			for (String key : sort.keySet()) {
				sortContion.put(key, sort.get(key));
			}
			FindIterable<Document> findIterable = col.find(condition).sort(sortContion);
			if (limit != null) {
				findIterable = findIterable.limit(limit);
			}
			if (skip != null) {
				findIterable = findIterable.skip(skip);
			}
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				documents.add(mongoCursor.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documents;
	}

	public List<Document> getDocumentsByOptionsSortAndLimit(String collectionName, Document queryCondition, Document sortCondition, Integer skip, Integer limit) {
		List<Document> documents = new ArrayList<Document>();
		try {
			MongoCollection<Document> col = dbClient.getCollection(collectionName);
			FindIterable<Document> findIterable = col.find(queryCondition).sort(sortCondition);
			if (limit != null) {
				findIterable = findIterable.limit(limit);
			}
			if (skip != null) {
				findIterable = findIterable.skip(skip);
			}
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				documents.add(mongoCursor.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documents;
	}

	public Document getCollectionById(long id, String collectionName) {
		MongoCollection<Document> order = this.getCollection(collectionName);
		BasicDBObject condition = new BasicDBObject();
		condition.append(DEF_ID_KEY, id);
		List<Document> documents = new ArrayList<Document>();
		FindIterable<Document> findIterable = order.find(condition);
		MongoCursor<Document> mongoCursor = findIterable.iterator();
		while (mongoCursor.hasNext()) {
			documents.add(mongoCursor.next());
		}
		if (org.springframework.util.CollectionUtils.isEmpty(documents)) {
			return null;
		}

		return documents.get(0);
	}
}
