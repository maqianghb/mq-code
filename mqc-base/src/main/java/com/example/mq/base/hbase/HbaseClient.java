package com.example.mq.base.hbase;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/6/3
 *
 */
//@Component
public class HbaseClient {
	private static Logger LOG = LoggerFactory.getLogger(HbaseClient.class);

	@Autowired
	private Connection hbaseConnection;

	public boolean existTable(String tableName) throws Exception{
		if(StringUtils.isEmpty(tableName)){
			throw new IllegalArgumentException(" existTable 参数异常！");
		}
		Admin admin =hbaseConnection.getAdmin();
		return admin.tableExists(TableName.valueOf(tableName));
	}

	public long createTable(String tableName, String[] family) throws Exception{
		if(StringUtils.isEmpty(tableName)){
			throw new IllegalArgumentException(" createTable 参数异常！");
		}
		Admin admin =hbaseConnection.getAdmin();
		if(admin.tableExists(TableName.valueOf(tableName))){
			return 1;
		}
		TableDescriptorBuilder tableBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));
		for(int i=0; i<family.length; i++){
			ColumnFamilyDescriptor tmpCFD = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(family[i])).build();
			tableBuilder.setColumnFamily(tmpCFD);
		}
		TableDescriptor table = tableBuilder.build();
		admin.createTable(table);
		return admin.tableExists(TableName.valueOf(tableName)) ? 1 : -1;
	}

	public long putData(String tableName, String rowKey, String columnFamily, Map<String, Object> columnMap) throws IOException{
		if(StringUtils.isEmpty(tableName) || StringUtils.isEmpty(rowKey)
				|| StringUtils.isEmpty(columnFamily) || null ==columnMap){
			throw new IllegalArgumentException(" rowKey 参数异常！");
		}
		Table table = hbaseConnection.getTable(TableName.valueOf(tableName));
		if(null ==table){
			LOG.error(" table is null, tableName:{}", tableName);
			return 0;
		}
		Put put = new Put(rowKey.getBytes());
		for(Map.Entry<String, Object> entry : columnMap.entrySet()){
			String column =null;
			Object value =null;
			if(StringUtils.isEmpty(column =entry.getKey()) || null ==(value =entry.getValue())){
				continue;
			}
			put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value.toString()));
		}
		table.put(put);
		return 1;
	}

	public Result getRow(String tableName, String rowKey) throws Exception{
		if(StringUtils.isEmpty(tableName) || StringUtils.isEmpty(rowKey)){
			throw new IllegalArgumentException(" getRow 参数异常！");
		}
		Table table =hbaseConnection.getTable(TableName.valueOf(tableName));
		Get get =new Get(Bytes.toBytes(rowKey));
		return table.get(get);
	}

	public ResultScanner getResultScan(String tableName, String startRowKey, String stopRowKey) throws Exception {
		if(StringUtils.isEmpty(tableName) || StringUtils.isEmpty(startRowKey) || StringUtils.isEmpty(stopRowKey)){
			throw new IllegalArgumentException(" getRow 参数异常！");
		}
		Table table = hbaseConnection.getTable(TableName.valueOf(tableName));
		Scan scan = new Scan();
		scan.withStartRow(Bytes.toBytes(startRowKey));
		scan.withStopRow(Bytes.toBytes(stopRowKey));
		ResultScanner rs = null;
		try {
			rs = table.getScanner(scan);
		} catch (Exception e){
			LOG.error("getScanner err, tableName:{}|startRowKey:{}|stopRowKey:{}", tableName, startRowKey, stopRowKey ,e);
		}
		return rs;
	}

	public void printResultValue(Result result) throws Exception{
		if(null ==result){
			return ;
		}
		List<Cell> cellList =result.listCells();
		for(Cell cell : cellList){
			String rowKey =Bytes.toString(cell.getRowArray());
			String columnFamily =Bytes.toString(cell.getFamilyArray());
			String qualifier =Bytes.toString(cell.getQualifierArray());
			String value =Bytes.toString(cell.getValueArray());
			LOG.info("rowKey:{}|columnFamily:{}|qualifier:{}|value:{}", rowKey, columnFamily, qualifier, value);
		}
		return;
	}

	public void printResultScanner(ResultScanner scanner) throws Exception{
		if(null ==scanner){
			return ;
		}
		for(Result result : scanner){
			this.printResultValue(result);
		}
	}

}
