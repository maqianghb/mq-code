package com.example.mq.service.mapper.seller;


import com.example.mq.service.bean.Seller;

/**
 * @program: mq-code
 * @description: seller映射接口
 * @author: maqiang
 * @create: 2018/9/19
 *
 */
public interface PlatformSellerMapper {

	Seller selectBySellerNo(long sellerNo) throws Exception;

	long insert(Seller seller) throws Exception;

	long updateBySellerNo(Seller seller) throws Exception;

}
