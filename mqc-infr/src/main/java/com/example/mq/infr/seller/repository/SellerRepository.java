package com.example.mq.infr.seller.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mq.infr.seller.condition.SellerQueryCondition;
import com.example.mq.infr.seller.model.SellerDO;

public interface SellerRepository {

    Page<SellerDO> pageQueryByCondition(SellerQueryCondition condition);

    void saveSellerDO(SellerDO sellerDO);

    void updateById(SellerDO sellerDO);

}
