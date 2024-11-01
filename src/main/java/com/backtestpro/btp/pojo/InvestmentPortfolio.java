package com.backtestpro.btp.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvestmentPortfolio {
    // Key 是股票代碼，Value 是該股票的多期 InvestmentData
    private Map<String, List<InvestmentData>> portfolio = new HashMap<>();

    public Map<String, List<InvestmentData>> getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Map<String, List<InvestmentData>> portfolio) {
        this.portfolio = portfolio;
    }

    // 加入單筆投資數據
    public void addInvestmentDataToPortfolio(String symbol, InvestmentData data) {
        portfolio.computeIfAbsent(symbol, k -> new ArrayList<>()).add(data);
    }

    // 取得特定股票的所有投資資料
    public List<InvestmentData> getInvestmentDataBySymbol(String symbol) {
        return portfolio.getOrDefault(symbol, Collections.emptyList());
    }
}

