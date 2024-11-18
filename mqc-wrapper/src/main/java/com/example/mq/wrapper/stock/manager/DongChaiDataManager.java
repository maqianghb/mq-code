package com.example.mq.wrapper.stock.manager;

import com.example.mq.wrapper.stock.model.dongchai.DongChaiFinanceNoticeDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiFreeShareDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiHolderIncreaseDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiNorthHoldShareDTO;

import java.time.LocalDateTime;
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
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    List<DongChaiFreeShareDTO> queryFreeShareDTOList(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 查询沪港通持股数据
     *
     * @return
     */
    List<DongChaiNorthHoldShareDTO> queryNorthHoldShareDTOList(String stockCode);

    /**
     * by时间范围查询最大的增减持信息
     *
     * @param code
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    DongChaiHolderIncreaseDTO getMaxHolderIncreaseDTO(String code, LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 最大的解禁信息
     *
     * @param code
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    DongChaiFreeShareDTO getMaxFreeShareDTO(String code, LocalDateTime startDateTime, LocalDateTime endDateTime);

}
