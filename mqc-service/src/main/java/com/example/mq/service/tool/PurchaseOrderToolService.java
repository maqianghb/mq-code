package com.example.mq.service.tool;

import com.example.mq.client.common.Result;
import com.example.mq.client.service.order.model.PurchaseOrderDTO;

import java.util.List;

/**
 * @Author: maqiang
 * @CreateTime: 2024-02-15 11:03:21
 * @Description:
 */
public interface PurchaseOrderToolService {

    Result<List<PurchaseOrderDTO>> testQueryOrders(PurchaseOrderDTO purchaseOrderDTO);

}
