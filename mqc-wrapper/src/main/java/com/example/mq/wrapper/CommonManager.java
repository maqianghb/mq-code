package com.example.mq.wrapper;

import com.example.mq.wrapper.stock.enums.FinanceReportTypeEnum;
import com.example.mq.wrapper.stock.manager.LocalDataManager;
import com.example.mq.wrapper.stock.manager.StockIndicatorManager;
import com.example.mq.wrapper.stock.manager.impl.LocalDataManagerImpl;
import com.example.mq.wrapper.stock.manager.impl.StockIndicatorManagerImpl;
import org.junit.Test;

import java.util.List;

public class CommonManager {

    @Test
    public void testQueryAnalysisData(){
        // 参数列表
        Integer reportYear =2023;
        FinanceReportTypeEnum reportTypeEnum =FinanceReportTypeEnum.QUARTER_3;
        String kLineDate ="20240208";

        LocalDataManager localDataManager =new LocalDataManagerImpl();
        List<String> stockCodeList =localDataManager.getLocalStockCodeList();
//        List<String> stockCodeList =StockConstant.TEST_STOCK_CODE_LIST;

        StockIndicatorManager stockIndicatorManager =new StockIndicatorManagerImpl();

        // 指标数据
        stockIndicatorManager.calculateAndSaveAllAnalysisDTO(kLineDate, stockCodeList, reportYear ,reportTypeEnum);

        // 沪港通数据
        stockIndicatorManager.queryAndSaveLatestNorthHoldShares(stockCodeList);
        stockIndicatorManager.queryAndSaveLatestIndustryHoldShares();

    }

    @Test
    public void testQueryDongChaiData(){
        LocalDataManager localDataManager =new LocalDataManagerImpl();

        // 业绩预告数据
        String reportDate ="2023-12-31";
        localDataManager.queryAndSaveFinanceNotice(reportDate);

        // by行业预告数据
        localDataManager.queryAndSaveIndFinanceNotice(reportDate);

        // 股东增减持数据
//        localDataManager.queryAndSaveHolderIncreaseList();

    }

    @Test
    public void testUpdateData() {
        LocalDataManager localDataManager =new LocalDataManagerImpl();

        // 更新本地文件的K线数据
        localDataManager.queryAndUpdateKLineList();

        // 更新本地文件的财务数据
        Boolean updateFinanceData =false;
        if(updateFinanceData){
            localDataManager.queryAndUpdateBalanceData();
            localDataManager.queryAndUpdateIncomeData();
            localDataManager.queryAndUpdateCashFlowData();
            localDataManager.queryAndUpdateIndicatorData();
        }

        // 更新北上持股数据
        localDataManager.queryAndUpdateNorthHoldShareList();

        // 更新业绩预告和增减持数据
//        localDataManager.queryAndSaveFinanceNotice();
//        localDataManager.queryAndSaveHolderIncreaseList();
    }

}
