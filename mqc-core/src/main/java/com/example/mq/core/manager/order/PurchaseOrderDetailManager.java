package com.example.mq.core.manager.order;

import com.example.mq.data.mapper.order.model.PurchaseOrderDetailDO;

import java.util.List;

public interface PurchaseOrderDetailManager {

    List<PurchaseOrderDetailDO> queryPurchaseOrderDetails(PurchaseOrderDetailDO purchaseOrderDetailDO);

    boolean batchAddOrderDetail(List<PurchaseOrderDetailDO> detailDOList);

    boolean batchUpdateOrderDetail(List<PurchaseOrderDetailDO> detailDOList);

    boolean batchDeleteOrderDetail(List<PurchaseOrderDetailDO> detailDOList);

}
