package com.example.mq.core.manager.order;

import com.example.mq.data.mapper.order.model.PurchaseOrderDO;

import java.util.List;

public interface PurchaseOrderManager {

    Integer countPurchaseOrder(PurchaseOrderDO purchaseOrderDO);

    List<PurchaseOrderDO> queryPurchaseOrderList(PurchaseOrderDO purchaseOrderDO);

    boolean addPurchaseOrder(PurchaseOrderDO purchaseOrderDO);

    boolean updatePurchaseOrder(PurchaseOrderDO purchaseOrderDO);

}
