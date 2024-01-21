package com.example.mq.core.domain.order;

import com.example.mq.core.domain.order.model.PurchaseOrder;

import java.util.List;

public interface PurchaseOrderDomainService {

    /**
     * by条件查询采购单数量
     *
     * @param purchaseOrder
     * @return
     */
    Integer countPurchaseOrder(PurchaseOrder purchaseOrder);

    /**
     * by条件查询采购单数据
     *
     * @param purchaseOrder
     * @return
     */
    List<PurchaseOrder> queryPurchaseOrderList(PurchaseOrder purchaseOrder);

    /**
     * 新增采购单
     *
     * @param purchaseOrder
     * @return
     */
    Boolean addPurchaseOrder(PurchaseOrder purchaseOrder);

    /**
     * 更新采购单
     *
     * @param purchaseOrder
     * @return
     */
    Boolean updatePurchaseOrder(PurchaseOrder purchaseOrder);

}
