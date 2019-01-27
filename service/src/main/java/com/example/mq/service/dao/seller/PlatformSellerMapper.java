package com.example.mq.service.dao.seller;

import java.util.List;

import com.example.mq.service.bean.Customer;

/**
 * @program: crules-management
 * @description: seller映射接口
 * @author: maqiang
 * @create: 2018/9/19
 *
 */
public interface PlatformSellerMapper {

	Customer selectBySellerId(Long sellerId);

}
