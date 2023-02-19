package com.example.mq.wrapper.stock.model;

import lombok.Data;

@Data
public class AnalyseIndicatorDTO extends BaseStockFinanceDTO {

    /**
     * 资产负债率
     */
    private Double asset_liab_ratio;

    /**
     * 净资产收益率TTM
     */
    private Double avg_roe_ttm;

}
