package com.backtestpro.btp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StockData {
    @JsonProperty("日期")
    private String date;

    @JsonProperty("成交股數")
    private long volume;

    @JsonProperty("成交金額")
    private String transactionAmount;

    @JsonProperty("開盤價")
    private double open;

    @JsonProperty("最高價")
    private double high;

    @JsonProperty("最低價")
    private double low;

    @JsonProperty("收盤價")
    private double close;

    @JsonProperty("漲跌價差")
    private String priceChange; // 涨跌价差，使用 String 类型以便处理 '+/-' 符号

    @JsonProperty("成交筆數")
    private long transactionCount;

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public String getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(String priceChange) {
        this.priceChange = priceChange;
    }

    public long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(long transactionCount) {
        this.transactionCount = transactionCount;
    }
}
