package com.example.mq.wrapper;

import com.example.mq.wrapper.stock.enums.FinanceReportTypeEnum;
import com.example.mq.wrapper.stock.manager.LocalDataManager;
import com.example.mq.wrapper.stock.manager.StockIndicatorManager;
import com.example.mq.wrapper.stock.manager.impl.LocalDataManagerImpl;
import com.example.mq.wrapper.stock.manager.impl.StockIndicatorManagerImpl;

import java.util.List;

public class CommonManager {

    public static void main(String[] args) {
        LocalDataManager localDataManager =new LocalDataManagerImpl();

        // 更新本地文件的K线数据
        localDataManager.queryAndUpdateKLineList();

        // 更新本地文件的财务数据
        localDataManager.queryAndUpdateFinanceList();

        // 参数列表
        Integer reportYear = 2023;
        FinanceReportTypeEnum reportTypeEnum =FinanceReportTypeEnum.QUARTER_3;

        List<String> stockCodeList =localDataManager.getLocalStockCodeList();
//        List<String> stockCodeList =StockConstant.TEST_STOCK_CODE_LIST;

        StockIndicatorManager stockIndicatorManager =new StockIndicatorManagerImpl();
        // 指标数据
//        stockIndicatorManager.calculateAndSaveAllAnalysisDTO(stockCodeList, reportYear ,reportTypeEnum);

        // 沪港通数据
        stockIndicatorManager.queryAndSaveNorthHoldShares(stockCodeList, false);

    }

}
