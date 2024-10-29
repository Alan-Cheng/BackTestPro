package com.backtestpro.btp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.backtestpro.btp.service.StockService;
import com.backtestpro.btp.service.InvestmentService;
import com.backtestpro.btp.pojo.StockData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.List;


@RestController
public class StockController {

    @Autowired
    private StockService stockService;

    @Autowired
    private InvestmentService investmentService;

    @GetMapping("/stock-data")
    public List<StockData> getHistoricalStockData(
            @RequestParam String symbol,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            return stockService.getStockData(symbol, startDate, endDate);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/regular-investment")
    public Double getRegularInvestmentReturn(
            @RequestParam String symbol,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam double investmentAmount,
            @RequestParam String investmentDate) {

        try {
            return investmentService.calculateInvestmentReturn(symbol, startDate, endDate, investmentAmount, investmentDate);

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
