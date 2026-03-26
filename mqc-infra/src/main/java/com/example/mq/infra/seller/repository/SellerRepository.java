package com.example.mq.infra.seller.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mq.infra.seller.condition.SellerQueryCondition;
import com.example.mq.infra.seller.model.SellerDO;

public interface SellerRepository {

    Page<SellerDO> pageQueryByCondition(SellerQueryCondition condition);

    void saveSellerDO(SellerDO sellerDO);

    void updateById(SellerDO sellerDO);

}
