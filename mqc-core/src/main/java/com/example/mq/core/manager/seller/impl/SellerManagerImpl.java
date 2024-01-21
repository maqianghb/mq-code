package com.example.mq.core.manager.seller.impl;

import com.example.mq.core.manager.seller.SellerManager;
import com.example.mq.data.mapper.seller.model.SellerDO;
import com.example.mq.data.mapper.seller.SellerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class SellerManagerImpl implements SellerManager {

    @Resource
    private SellerMapper sellerMapper;

    @Override
    public Integer countSeller(SellerDO sellerDO) {
        return sellerMapper.countSeller(sellerDO);
    }
}
