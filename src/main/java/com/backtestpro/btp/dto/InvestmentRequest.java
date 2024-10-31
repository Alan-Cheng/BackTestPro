package com.backtestpro.btp.dto;

public class InvestmentRequest {
    private String symbol;            // 股票代碼
    private String startDate;         // 開始日期
    private String endDate;           // 結束日期
    private double investmentAmount;   // 投資金額
    private String investmentDay;      // 投資日期

    // Getter 和 Setter
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(double investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public String getInvestmentDay() {
        return investmentDay;
    }

    public void setInvestmentDay(String investmentDay) {
        this.investmentDay = investmentDay;
    }
}

