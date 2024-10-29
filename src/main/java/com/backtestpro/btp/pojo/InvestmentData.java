package com.backtestpro.btp.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvestmentData {

    // 投资日期
    private LocalDateTime investmentDate;
    
    // 每期投资金额
    private BigDecimal investmentAmount;
    
    // 累计资产总额
    private BigDecimal totalAmount;
    
    // 当前总收益
    private BigDecimal totalProfit;
    
    // 当前收益率
    private BigDecimal returnRate;

    // Getter 和 Setter 方法

    public LocalDateTime getInvestmentDate() {
        return investmentDate;
    }

    public void setInvestmentDate(LocalDateTime investmentDate) {
        this.investmentDate = investmentDate;
    }

    public BigDecimal getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(BigDecimal investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    public BigDecimal getReturnRate() {
        return returnRate;
    }

    public void setReturnRate(BigDecimal returnRate) {
        this.returnRate = returnRate;
    }
}
