package com.example.mq.core.domain.order.impl;

import com.example.mq.common.exception.ErrorCodeEnum;
import com.example.mq.common.utils.AssertUtils;
import com.example.mq.core.domain.order.PurchaseOrderDomainService;
import com.example.mq.core.domain.order.convert.PurchaseOrderConvertor;
import com.example.mq.core.domain.order.model.PurchaseOrder;
import com.example.mq.core.manager.order.PurchaseOrderManager;
import com.example.mq.data.mapper.order.model.PurchaseOrderDO;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PurchaseOrderDomainServiceImpl implements PurchaseOrderDomainService {

    @Resource
    private PurchaseOrderManager purchaseOrderManager;

    @Override
    public Integer countPurchaseOrder(PurchaseOrder purchaseOrder) {
        AssertUtils.assertNotNull(purchaseOrder, ErrorCodeEnum.PARAM_ERROR);

        PurchaseOrderDO purchaseOrderDO = PurchaseOrderConvertor.INSTANTCE.mapToOrderDO(purchaseOrder);
        Integer count = purchaseOrderManager.countPurchaseOrder(purchaseOrderDO);
        return count;
    }

    @Override
    public List<PurchaseOrder> queryPurchaseOrderList(PurchaseOrder purchaseOrder) {
        AssertUtils.assertNotNull(purchaseOrder, ErrorCodeEnum.PARAM_ERROR);

        PurchaseOrderDO purchaseOrderDO = PurchaseOrderConvertor.INSTANTCE.mapToOrderDO(purchaseOrder);
        List<PurchaseOrderDO> purchaseOrderDOList = purchaseOrderManager.queryPurchaseOrderList(purchaseOrderDO);

        return Optional.ofNullable(purchaseOrderDOList).orElse(Lists.newArrayList()).stream()
                .map(orderDO -> PurchaseOrderConvertor.INSTANTCE.mapToOrder(orderDO))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean addPurchaseOrder(PurchaseOrder purchaseOrder) {
        AssertUtils.assertNotNull(purchaseOrder, ErrorCodeEnum.PARAM_ERROR);

        PurchaseOrderDO purchaseOrderDO = PurchaseOrderConvertor.INSTANTCE.mapToOrderDO(purchaseOrder);
        return purchaseOrderManager.addPurchaseOrder(purchaseOrderDO);
    }

    @Override
    public Boolean updatePurchaseOrder(PurchaseOrder purchaseOrder) {
        AssertUtils.assertNotNull(purchaseOrder, ErrorCodeEnum.PARAM_ERROR);
        AssertUtils.assertNotNull(purchaseOrder.getId(), ErrorCodeEnum.PARAM_ERROR);

        PurchaseOrderDO purchaseOrderDO = PurchaseOrderConvertor.INSTANTCE.mapToOrderDO(purchaseOrder);
        return purchaseOrderManager.updatePurchaseOrder(purchaseOrderDO);
    }
}
