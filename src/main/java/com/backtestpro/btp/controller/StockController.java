package com.backtestpro.btp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.backtestpro.btp.service.StockService;
import com.backtestpro.btp.service.InvestmentService;
import com.backtestpro.btp.dto.StockData;
import com.backtestpro.btp.dto.StockInfo;
import com.backtestpro.btp.pojo.InvestmentData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.List;


@RestController
@RequestMapping("${app.api-prefix}/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/info")
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

    @GetMapping("/symbol-info")
    public List<StockInfo> getAllStockSymbol() {
        return stockService.getAllStockInfo();
    }
    
}
