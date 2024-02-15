package com.example.mq.service.order.impl;

import com.example.mq.client.common.Result;
import com.example.mq.client.service.order.PurchaseOrderService;
import com.example.mq.client.service.order.model.PurchaseOrderDTO;
import com.example.mq.common.exception.ErrorCodeEnum;
import com.example.mq.common.utils.AssertUtils;
import com.example.mq.core.domain.order.PurchaseOrderDomainService;
import com.example.mq.core.domain.order.convert.PurchaseOrderConvertor;
import com.example.mq.core.domain.order.model.PurchaseOrder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: maqiang
 * @CreateTime: 2024-02-15 11:18:29
 * @Description:
 */
@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Resource
    private PurchaseOrderDomainService purchaseOrderDomainService;

    @Override
    public Result<List<PurchaseOrderDTO>> queryOrderList(PurchaseOrderDTO purchaseOrderDTO) {
        AssertUtils.assertNotNull(purchaseOrderDTO, ErrorCodeEnum.PARAM_ERROR);

        PurchaseOrder purchaseOrder = PurchaseOrderConvertor.INSTANTCE.mapToOrder(purchaseOrderDTO);
        List<PurchaseOrder> purchaseOrderList = purchaseOrderDomainService.queryPurchaseOrderList(purchaseOrder);
        if(CollectionUtils.isEmpty(purchaseOrderList)){
            return Result.fail("purchaseOrderList is empty.");
        }

        List<PurchaseOrderDTO> orderDTOList = purchaseOrderList.stream()
                .map(order -> PurchaseOrderConvertor.INSTANTCE.mapToOrderDTO(order))
                .collect(Collectors.toList());
        return Result.success(orderDTOList);
    }

}
