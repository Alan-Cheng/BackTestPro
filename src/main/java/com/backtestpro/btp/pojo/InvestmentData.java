package com.backtestpro.btp.pojo;

import java.util.List;
public class InvestmentData {

    // 投资日期
    private String investmentDate;
    
    // 每期投资金额
    private double investmentAmount;
    
    // 累计资产总额
    private double totalAmount;
    
    //累計股份
    private double shares;

    //累積報酬
    private double totalReturn;

    //當前收益率
    private double totalReturnRate;

    //購買的股票
    private StockData stock;

    // Getter 和 Setter 方法

    public String getInvestmentDate() {
        return investmentDate;
    }

    public void setInvestmentDate(String investmentDate) {
        this.investmentDate = investmentDate;
    }

    public double getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(double investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getShares() {
        return shares;
    }

    public void setShares(double shares) {
        this.shares = shares;
    }

    public StockData getStock() {
        return stock;
    }

    public void setStock(StockData stock) {
        this.stock = stock;
    }

    public double getTotalReturn() {
        return totalReturn;
    }

    public void setTotalReturn(double totalReturn) {
        this.totalReturn = totalReturn;
    }

    public double getTotalReturnRate() {
        return totalReturnRate;
    }

    public void setTotalReturnRate(double totalReturnRate) {
        this.totalReturnRate = totalReturnRate;
    }
}