package com.example.mq.test.stock.manager;

import com.example.mq.test.stock.model.dongchai.*;

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
     * 查询最近的质押数据
     * @param stockCode
     * @return
     */
    List<DongChaiPledgeDataDTO> queryLatestPledgeRate(String stockCode);

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
