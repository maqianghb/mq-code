package com.example.mq.data.mapper.order;

import com.example.mq.data.mapper.order.model.PurchaseOrderDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PurchaseOrderMapper {

    Integer countPurchaseOrder(PurchaseOrderDO purchaseOrderDO);

    List<PurchaseOrderDO> queryPurchaseOrderList(PurchaseOrderDO purchaseOrderDO);

    boolean addPurchaseOrder(PurchaseOrderDO purchaseOrderDO);

    boolean updatePurchaseOrder(PurchaseOrderDO purchaseOrderDO);

}
