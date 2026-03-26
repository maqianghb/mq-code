package com.example.mq.domain.seller.impl;

import com.example.mq.domain.seller.SellerDomainService;
import com.example.mq.domain.seller.model.SellerEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: maqiang
 * @CreateTime: 2026-03-26 11:56:57
 * @Description:
 */
@Component
@Slf4j
public class SellerDomainServiceImpl implements SellerDomainService {

    @Override
    public Long saveSeller(SellerEntity sellerEntity) {
        return null;
    }

}
