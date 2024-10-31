package com.backtestpro.btp.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvestmentPortfolio {
    // Key 是股票代碼，Value 是該股票的多期 InvestmentData
    private Map<String, List<InvestmentData>> investments = new HashMap<>();

    public Map<String, List<InvestmentData>> getInvestments() {
        return investments;
    }

    public void setInvestments(Map<String, List<InvestmentData>> investments) {
        this.investments = investments;
    }

    // 加入單筆投資數據
    public void addInvestmentData(String symbol, InvestmentData data) {
        investments.computeIfAbsent(symbol, k -> new ArrayList<>()).add(data);
    }

    // 取得特定股票的所有投資資料
    public List<InvestmentData> getInvestmentDataByStock(String symbol) {
        return investments.getOrDefault(symbol, Collections.emptyList());
    }
}

