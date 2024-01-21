package com.example.mq.data.mapper.order;

import com.example.mq.data.mapper.order.model.PurchaseOrderDetailDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: maqiang
 * @CreateTime: 2024-01-21 20:56:11
 * @Description:
 */
@Mapper
public interface PurchaseOrderDetailMapper {

    Integer queryCount(PurchaseOrderDetailDO purchaseOrderDetailDO);

    List<PurchaseOrderDetailDO> queryDetailList(PurchaseOrderDetailDO purchaseOrderDetailDO);

    boolean batchAddDetail(List<PurchaseOrderDetailDO> detailDOList);

    boolean batchUpdateDetail(List<PurchaseOrderDetailDO> detailDOList);

    boolean batchDeleteDetail(@Param("poCode") String poCode);


}
