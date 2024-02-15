package com.example.mq.core.domain.order.convert;

import com.example.mq.client.service.order.model.PurchaseOrderDTO;
import com.example.mq.core.domain.order.model.PurchaseOrder;
import com.example.mq.data.mapper.order.model.PurchaseOrderDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Author: maqiang
 * @CreateTime: 2024-02-15 11:21:46
 * @Description:
 */
@Mapper
public interface PurchaseOrderConvertor {
    PurchaseOrderConvertor INSTANTCE = Mappers.getMapper(PurchaseOrderConvertor.class);

    PurchaseOrder mapToOrder(PurchaseOrderDTO orderDTO);

    PurchaseOrderDO mapToOrderDO(PurchaseOrder order);

    PurchaseOrder mapToOrder(PurchaseOrderDO orderDO);

    PurchaseOrderDTO mapToOrderDTO(PurchaseOrder order);

}
