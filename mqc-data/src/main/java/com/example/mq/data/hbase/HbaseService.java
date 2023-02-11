package com.example.mq.data.hbase;

import java.util.List;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/6/3
 *
 */

public interface HbaseService {

	/**
	 * 查询数据
	 * @param tableName
	 * @param startRowKey
	 * @param stopRowKey
	 * @param family
	 * @return
	 * @throws Exception
	 */
	List<String> queryData(String tableName, String startRowKey, String stopRowKey, byte[] family) throws Exception;

}
