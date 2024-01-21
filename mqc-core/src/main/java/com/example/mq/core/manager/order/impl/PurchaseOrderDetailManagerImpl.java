package com.example.mq.core.manager.order.impl;

import com.example.mq.core.manager.order.PurchaseOrderDetailManager;
import com.example.mq.data.mapper.order.PurchaseOrderDetailMapper;
import com.example.mq.data.mapper.order.model.PurchaseOrderDetailDO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: maqiang
 * @CreateTime: 2024-01-21 20-26
 * @Description:
 */
@Component
public class PurchaseOrderDetailManagerImpl implements PurchaseOrderDetailManager {

    @Resource
    private PurchaseOrderDetailMapper purchaseOrderDetailMapper;

    @Override
    public List<PurchaseOrderDetailDO> queryPurchaseOrderDetails(PurchaseOrderDetailDO purchaseOrderDetailDO) {
        return null;
    }

    @Override
    public boolean batchAddOrderDetail(List<PurchaseOrderDetailDO> detailDOList) {
        return false;
    }

    @Override
    public boolean batchUpdateOrderDetail(List<PurchaseOrderDetailDO> detailDOList) {
        return false;
    }

    @Override
    public boolean batchDeleteOrderDetail(List<PurchaseOrderDetailDO> detailDOList) {
        return false;
    }
}
