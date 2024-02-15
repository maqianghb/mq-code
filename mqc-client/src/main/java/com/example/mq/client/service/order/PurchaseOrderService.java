package com.example.mq.client.service.order;

import com.example.mq.client.common.Result;
import com.example.mq.client.service.order.model.PurchaseOrderDTO;

import java.util.List;

/**
 * @Author: maqiang
 * @CreateTime: 2024-02-15 11:13:36
 * @Description:
 */
public interface PurchaseOrderService {

    Result<List<PurchaseOrderDTO>> queryOrderList(PurchaseOrderDTO purchaseOrderDTO);

}
