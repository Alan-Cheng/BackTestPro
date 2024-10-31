package com.backtestpro.btp.dto;

public class StockInfo {
    private String code;
    private String name;

    public StockInfo(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
