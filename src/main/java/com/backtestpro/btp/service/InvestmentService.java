package com.backtestpro.btp.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.Comparator;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backtestpro.btp.pojo.InvestmentData;
import com.backtestpro.btp.pojo.StockData;

@Service
public class InvestmentService {

    @Autowired
    private StockService stockService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<StockData> filterStockDataByInvestmentDay(List<StockData> stockDataList, String investmentDay) {
        return stockDataList.stream()
            .collect(Collectors.groupingBy(data -> LocalDate.parse(data.getDate(), DATE_FORMATTER).getMonth())) // 按月分组
            .values().stream()
            .map(monthlyData -> findClosestData(monthlyData, investmentDay)) // 找到每月最接近的投资日数据
            .filter(Optional::isPresent) // 过滤出存在的值
            .map(Optional::get) // 获取 Optional 中的值
            .collect(Collectors.toList()); // 收集成列表
    }

    private Optional<StockData> findClosestData(List<StockData> monthlyData, String investmentDay) {
        int investmentDayInt = Integer.parseInt(investmentDay);
        // 查找是否存在该投资日的股价数据
        Optional<StockData> exactMatch = monthlyData.stream()
            .filter(data -> {
                LocalDate date = LocalDate.parse(data.getDate(), DATE_FORMATTER);
                return date.getDayOfMonth() == investmentDayInt; // 精确匹配投资日
            })
            .findFirst();

        // 如果找到，则返回该数据；否则查找最接近的下一天
        return exactMatch.isPresent() ? exactMatch : monthlyData.stream()
            .filter(data -> {
                LocalDate date = LocalDate.parse(data.getDate(), DATE_FORMATTER);
                return date.getDayOfMonth() > investmentDayInt; // 过滤出大于投资日的日期
            })
            .min(Comparator.comparing(data -> LocalDate.parse(data.getDate(), DATE_FORMATTER))); // 选取最接近的日期
    }
    
    public List<InvestmentData> getInvestmentData(String symbol, String startDate, String endDate, double investmentAmount, String investmentDay) {
        List<StockData> stockDataList = stockService.getStockData(symbol, startDate, endDate);
        List<StockData> filteredStockData = filterStockDataByInvestmentDay(stockDataList, investmentDay);

        return Stream.iterate(0, i -> i + 1)
            .limit(filteredStockData.size())
            .map(i -> {
                StockData stockData = filteredStockData.get(i);
                InvestmentData investmentData = new InvestmentData();
                BigDecimal investmentAmountBD = BigDecimal.valueOf(investmentAmount);
                investmentData.setInvestmentDate(LocalDate.parse(stockData.getDate(), DATE_FORMATTER).atStartOfDay());
                investmentData.setInvestmentAmount(investmentAmountBD);
                BigDecimal totalAmountBD = investmentAmountBD.multiply(BigDecimal.valueOf(i + 1));
                investmentData.setTotalAmount(totalAmountBD);
                BigDecimal totalProfitBD = totalAmountBD.subtract(investmentAmountBD.multiply(BigDecimal.valueOf(i + 1)));
                investmentData.setTotalProfit(totalProfitBD);
                investmentData.setReturnRate(investmentData.getTotalProfit().divide(investmentData.getTotalAmount()));
                return investmentData;
            })
            .collect(Collectors.toList());
    }
}
