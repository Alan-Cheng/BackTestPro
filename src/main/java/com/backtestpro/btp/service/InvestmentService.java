package com.backtestpro.btp.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backtestpro.btp.pojo.StockData;

@Service
public class InvestmentService {

    @Autowired
    private StockService stockService;

    public Double calculateInvestmentReturn(String symbol, String startDate, String endDate, double investmentAmount,
            String investmentDate) throws IOException {
        List<StockData> stockDataList = stockService.getStockData(symbol, startDate, endDate);
        if (stockDataList == null || stockDataList.isEmpty()) {
            return 0.0;
        }

        double totalShares = 0;
        double totalInvestment = 0;
        // 尚未完成投報率邏輯
        for (StockData stockData : stockDataList) {
            double closePrice = stockData.getClose();
            if (stockData.getDate().equals(investmentDate)) {
                totalShares = investmentAmount / closePrice;
                totalInvestment = investmentAmount;
                break;
            }
        }

        if (totalShares == 0) {
            return 0.0;
        }

        double currentPrice = stockDataList.get(stockDataList.size() - 1).getClose();
        double currentInvestment = totalShares * currentPrice;
        return (currentInvestment - totalInvestment) / totalInvestment;
    }
}
