package com.example.mq.wrapper.stock.manager;

import com.example.mq.wrapper.stock.model.dongchai.DongChaiFinanceNoticeDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiFreeShareDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiHolderIncreaseDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiNorthHoldShareDTO;

import java.util.List;

public interface DongChaiDataManager {

    /**
     * 查询业绩预告数据
     *
     * @return
     */
    List<DongChaiFinanceNoticeDTO> queryFinanceNoticeDTO(String reportDate);

    /**
     * 查询股东增减持数据
     *
     * @return
     */
    List<DongChaiHolderIncreaseDTO> queryHolderIncreaseList();

    /**
     * 查询解禁数据
     *
     * @param beforeMonthNum 前N个月的数据
     * @param afterMonthNum 后N个月的数据
     * @return
     */
    List<DongChaiFreeShareDTO> queryFreeShareDTOList(Integer beforeMonthNum, Integer afterMonthNum);

    /**
     * 查询沪港通持股数据
     *
     * @return
     */
    List<DongChaiNorthHoldShareDTO> queryNorthHoldShareDTOList(String simpleCode);

    /**
     * 最近的增减持信息
     *
     * @param code
     * @return
     */
    DongChaiHolderIncreaseDTO getHolderIncreaseDTO(String code);

    /**
     * 解禁信息(往前3个月，往后6个月)
     *
     * @param code
     * @return
     */
    DongChaiFreeShareDTO getFreeShareDTO(String code);

}
