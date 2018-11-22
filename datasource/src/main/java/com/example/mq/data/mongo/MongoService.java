package com.example.mq.data.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: mq-code
 * @description: mongoService
 * @author: maqiang
 * @create: 2018/11/15
 *
 */
@Component
public class MongoService {

	/**
	 * mongo默认id的key
	 */
	private final String DEF_ID_KEY ="_id";

    @Autowired
    @Qualifier("mongoDatabase")
    private MongoDatabase dbClient;

    /**
     * 获取某个集合对象
     * @param collection
     * @return
     */
    public MongoCollection<Document> getCollection(String collection) {
        MongoCollection<Document> docs=null;
        try {
            docs = dbClient.getCollection(collection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return docs;
    }

    /**
     * 查询集合中所有的document
     * @param collectionName
     * @return
     */
    public List<Document> getAll(String collectionName){
        List<Document> list=null;
        try{
            MongoCollection<Document> collection=dbClient.getCollection(collectionName);
            FindIterable<Document> iterable=collection.find();
            MongoCursor<Document> cursor=iterable.iterator();
            list=new ArrayList<Document>();

            while (cursor.hasNext()){
                list.add(cursor.next());
                //输出集合中所有document
                // System.out.println(cursor.next());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 向某个集合插入document
     * @param json
     * @param collection
     */
    public void insert(String json, String collection) {
        MongoCollection<Document> col = dbClient.getCollection(collection);
        Document doc = Document.parse(json);
        col.insertOne(doc);
    }

    /**
     * 向某个集合根据id插入document
     * @param id
     * @param json
     * @param collection
     */
    public void insert(Long id, String json, String collection) {
        MongoCollection<Document> col = dbClient.getCollection(collection);
        Document doc = Document.parse(json);
        Document document = doc.append(DEF_ID_KEY, id);
        col.insertOne(document);
    }

    /**
     * 向某个集合根据id插入document
     * @param id
     * @param json
     * @param collection
     */
    public void insert(String id, String json, String collection) {
        MongoCollection<Document> col = dbClient.getCollection(collection);
        Document doc = Document.parse(json);
        Document document = doc.append(DEF_ID_KEY, id);
        col.insertOne(document);
    }

    /**
     * 批量更新document
     * @param json
     * @param collection
     * @return
     */
    public long batchUpdate(String json,Map<String,Object> options,String collection) {
        Document document=null;
        try{
            MongoCollection<Document> col = dbClient.getCollection(collection);
            BasicDBObject condition = new BasicDBObject();
            for(Map.Entry<String,Object> entry:options.entrySet()){
                condition.append(entry.getKey(),entry.getValue());
            }
            UpdateResult result = col.updateMany(condition,Document.parse(json));
            return result.getModifiedCount();
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 向某个集合根据id更新document
     * @param id
     * @param json
     * @param collection
     * @return
     */
    public long upsert(Object id, String json, String collection) {
        return upsert(DEF_ID_KEY,id,json,collection);
    }

    /**
     * 向某个集合根据id更新document
     * @param id
     * @param json
     * @param collection
     * @return
     */
    public long upsert(String key, Object id, String json, String collection) {
        Document document=null;
        try{
            MongoCollection<Document> col = dbClient.getCollection(collection);
            document= col.findOneAndUpdate(Filters.eq(key, id), new Document("$set", Document.parse(json)),
                    new FindOneAndUpdateOptions().upsert(true));
        }catch (Exception e){
            e.printStackTrace();
        }

        if (document != null) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 对某个集合根据主键删除document
     * @param id
     * @param collection
     * @return
     */
    public long deleteById(Long id,String collection){
        MongoCollection<Document> col = dbClient.getCollection(collection);
        DeleteResult result=col.deleteOne(Filters.eq(DEF_ID_KEY,id));
        long count=result.getDeletedCount();
        return count;
    }

	/**
	 * 根据主键id删除document
	 * @param id
	 * @param collection
	 * @return
	 */
    public long deleteById(String id, String collection){
		MongoCollection<Document> col = dbClient.getCollection(collection);
		DeleteResult result = col.deleteOne(Filters.eq(DEF_ID_KEY, id));
		long count = result.getDeletedCount();
		return count;
	}

    /**
     * 对某个集合根据id查询document
     * @param id
     * @param connection
     * @return
     */
    public Document getById(Object id, String connection){
        Document doc=null;
        try{
            MongoCollection<Document> col=dbClient.getCollection(connection);
            MongoCursor<Document> cursor=col.find(Filters.eq(DEF_ID_KEY,id)).iterator();

            if(cursor.hasNext()){
                doc=cursor.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return doc;
    }
    /**
     * 多条件查询
     * @param collection
     * @param options
     * @return
     */
    public  List<Document> getDocumentsByOptions(String collection, Map<String,Object> options){
        List<Document> documents=new ArrayList<Document>();
        try{
            MongoCollection<Document> col=dbClient.getCollection(collection);
            BasicDBObject condition=new BasicDBObject();
            for(Map.Entry<String,Object> entry:options.entrySet()){
                condition.append(entry.getKey(),entry.getValue());
            }
            FindIterable<Document> findIterable=col.find(condition);
            MongoCursor<Document> mongoCursor=findIterable.iterator();
            while(mongoCursor.hasNext()){
                documents.add(mongoCursor.next());
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return documents;
    }

	public  List<Document> getDocumentsAndSortByOptions(String collectionName, Map<String,Object> keys,Map<String,Integer> sort){
		List<Document> documents=new ArrayList<Document>();
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
		}catch(Exception e){
			e.printStackTrace();
		}
		return documents;
	}

    /**
     *
     * @param iterable
     * @return
     */
    public List<Document> iter(FindIterable<Document> iterable){
        List<Document> documents=new ArrayList<Document>();
        try{
            MongoCursor<Document> mongoCursor=iterable.iterator();
            while(mongoCursor.hasNext()){
                documents.add(mongoCursor.next());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return  documents;
    }

    public  Integer countByOptions(String collectionName, Map<String,Object> keys){
        List<Document> documents=new ArrayList<Document>();
        try {
            MongoCollection<Document> col = dbClient.getCollection(collectionName);
            BasicDBObject condition = new BasicDBObject();
            for (String key : keys.keySet()) {
                condition.put(key, keys.get(key));
            }
            long count = col.count(condition);
            return (int) count;
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public  List<Document> getDocumentsByOptionsSortLimit(String collectionName, Map<String,Object> keys,Map<String,Integer> sort,Integer skip,Integer limit){
        List<Document> documents=new ArrayList<Document>();
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
        }catch(Exception e){
            e.printStackTrace();
        }
        return documents;
    }


    public  List<Document> getDocumentsByOptionsSortAndLimit(String collectionName, Document queryCondition,Document sortCondition,Integer skip,Integer limit){
        List<Document> documents=new ArrayList<Document>();
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
        }catch(Exception e){
            e.printStackTrace();
        }
        return documents;
    }

    public  Document getCollectionById(long key, String collectionName) {
        MongoCollection<Document> order = this.getCollection(collectionName);
        BasicDBObject condition = new BasicDBObject();
        condition.append(DEF_ID_KEY, key);
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
