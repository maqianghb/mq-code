package com.example.mq.base.hbase.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;

import com.example.mq.base.hbase.HbaseClient;
import com.example.mq.base.hbase.HbaseService;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/6/3
 *
 */
//@Service
public class HbaseServiceImpl implements HbaseService {
	private static Logger LOG = LoggerFactory.getLogger(HbaseServiceImpl.class);

	@Autowired
	private HbaseClient hbaseClient;

	@Override
	public List<String> queryData(String tableName, String startRowKey, String stopRowKey, byte[] family) throws Exception {
		if(StringUtils.isEmpty(tableName) || StringUtils.isEmpty(startRowKey) || StringUtils.isEmpty(stopRowKey)){
			throw new IllegalArgumentException(" queryData 参数为空！");
		}
//		ResultScanner scanner =hbaseClient.getResultScan(tableName, startRowKey, stopRowKey);
		ResultScanner scanner =null;
		if(null ==scanner){
			LOG.error(" scanner is null, tableName:{}startRowKey:{}|stopRowKey:{}", tableName, startRowKey, stopRowKey);
			return null;
		}
		List<String> values =new ArrayList<>();
		try {
			Result result =null;
			while (null != (result = scanner.next())){
				for(Cell cell : result.listCells()){
					String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
					String value =Bytes.toString(CellUtil.cloneValue(cell));
					LOG.info("qualifier:{}|value:{}", qualifier, value);
				}
				NavigableMap<byte[], byte[]> familyMap = result.getFamilyMap(family);
				byte[] lastBytes =null;
				String lastStr =null;
				if(null ==familyMap || null ==(lastBytes =familyMap.values().iterator().next())
						|| StringUtils.isEmpty(lastStr =new String(lastBytes))){
					continue;
				}
				values.add(lastStr);
			}
		}catch (Exception e){
			LOG.error("convert data error, ", e);
		}finally {
			scanner.close();
		}
		return values;
	}

}
