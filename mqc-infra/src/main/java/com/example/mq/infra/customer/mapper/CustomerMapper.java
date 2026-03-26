package com.example.mq.infra.customer.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mq.infra.customer.model.CustomerDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @program: mq-code
 * @description: customer映射接口
 * @author: maqiang
 * @create: 2018/9/19
 */
@Mapper
public interface CustomerMapper extends BaseMapper<CustomerDO> {

	/**
	 * 批量查询id
	 * @param customerTypeList
	 * @param startCreateTime
	 * @param endCreateTime
	 * @param lastMaxId
	 * @param limitNum
	 * @return
	 */
	List<Long> batchQueryIdForCheck(@Param("customerTypeList") List<String> customerTypeList
			, @Param("startCreateTime") Date startCreateTime, @Param("endCreateTime") Date endCreateTime
			, @Param("lastMaxId") long lastMaxId, @Param("limitNum") int limitNum);

}
