package com.example.mq.core.manager.order.impl;

import com.example.mq.common.exception.ErrorCodeEnum;
import com.example.mq.common.utils.AssertUtils;
import com.example.mq.core.manager.order.PurchaseOrderManager;
import com.example.mq.data.mapper.order.PurchaseOrderMapper;
import com.example.mq.data.mapper.order.model.PurchaseOrderDO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class PurchaseOrderManagerImpl implements PurchaseOrderManager {

    @Resource
    private PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public Integer countPurchaseOrder(PurchaseOrderDO purchaseOrderDO) {
        AssertUtils.assertNotNull(purchaseOrderDO, ErrorCodeEnum.PARAM_ERROR);

        return purchaseOrderMapper.countPurchaseOrder(purchaseOrderDO);
    }

    @Override
    public List<PurchaseOrderDO> queryPurchaseOrderList(PurchaseOrderDO purchaseOrderDO) {
        AssertUtils.assertNotNull(purchaseOrderDO, ErrorCodeEnum.PARAM_ERROR);

        return purchaseOrderMapper.queryPurchaseOrderList(purchaseOrderDO);
    }

    @Override
    public boolean addPurchaseOrder(PurchaseOrderDO purchaseOrderDO) {
        AssertUtils.assertNotNull(purchaseOrderDO, ErrorCodeEnum.PARAM_ERROR);

        if(purchaseOrderDO.getStatus() ==null){
            purchaseOrderDO.setStatus(NumberUtils.INTEGER_ONE);
        }
        if(purchaseOrderDO.getIsDeleted() ==null){
            purchaseOrderDO.setIsDeleted(NumberUtils.INTEGER_ZERO);
        }

        return purchaseOrderMapper.addPurchaseOrder(purchaseOrderDO);
    }

    @Override
    public boolean updatePurchaseOrder(PurchaseOrderDO purchaseOrderDO) {
        AssertUtils.assertNotNull(purchaseOrderDO, ErrorCodeEnum.PARAM_ERROR);
        AssertUtils.assertNotNull(purchaseOrderDO.getId(), ErrorCodeEnum.PARAM_ERROR, "采购单id为空");

        return purchaseOrderMapper.updatePurchaseOrder(purchaseOrderDO);
    }

}
