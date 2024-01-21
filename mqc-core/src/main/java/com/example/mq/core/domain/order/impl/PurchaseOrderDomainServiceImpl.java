package com.example.mq.core.domain.order.impl;

import com.example.mq.core.domain.order.PurchaseOrderDomainService;
import com.example.mq.core.domain.order.model.PurchaseOrder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PurchaseOrderDomainServiceImpl implements PurchaseOrderDomainService {
    @Override
    public Integer countPurchaseOrder(PurchaseOrder purchaseOrder) {
        return null;
    }

    @Override
    public List<PurchaseOrder> queryPurchaseOrderList(PurchaseOrder purchaseOrder) {
        return null;
    }

    @Override
    public Boolean addPurchaseOrder(PurchaseOrder purchaseOrder) {
        return null;
    }

    @Override
    public Boolean updatePurchaseOrder(PurchaseOrder purchaseOrder) {
        return null;
    }
}
