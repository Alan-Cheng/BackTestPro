package com.backtestpro.btp.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.backtestpro.btp.dto.InvestmentBatchRequest;
import com.backtestpro.btp.dto.InvestmentRequest;
import com.backtestpro.btp.dto.StockData;
import com.backtestpro.btp.pojo.InvestmentData;
import com.backtestpro.btp.pojo.InvestmentPortfolio;

@Service
public class InvestmentService {

    @Autowired
    private StockService stockService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<StockData> filterStockDataByInvestmentDay(List<StockData> stockDataList, String investmentDay,
            int investmentYear) {
        return stockDataList.stream()
                .collect(Collectors.groupingBy(data -> LocalDate.parse(data.getDate(), DATE_FORMATTER).getMonth())) // 按月分组
                .values().stream()
                .map(monthlyData -> findClosestData(monthlyData, investmentDay, investmentYear)) // 找到每月最接近的投资日数据
                .filter(Optional::isPresent) // 过滤出存在的值
                .map(Optional::get) // 获取 Optional 中的值
                .collect(Collectors.toList()); // 收集成列表
    }

    private Optional<StockData> findClosestData(List<StockData> monthlyData, String investmentDay, int investmentYear) {
        int investmentDayInt = Integer.parseInt(investmentDay);
        // 查找是否存在该投资日的股价数据
        Optional<StockData> exactMatch = monthlyData.stream()
                .filter(data -> {
                    LocalDate date = LocalDate.parse(data.getDate(), DATE_FORMATTER);
                    return date.getDayOfMonth() == investmentDayInt && date.getYear() == investmentYear; // 精确匹配投资日和年份
                })
                .findFirst();

        // 如果找到，则返回该数据；否则查找最接近的下一天
        return exactMatch.isPresent() ? exactMatch
                : monthlyData.stream()
                        .filter(data -> {
                            LocalDate date = LocalDate.parse(data.getDate(), DATE_FORMATTER);
                            return date.getYear() == investmentYear && date.getDayOfMonth() > investmentDayInt; // 过滤出大于投资日的日期且年份相同
                        })
                        .min(Comparator.comparing(data -> LocalDate.parse(data.getDate(), DATE_FORMATTER))); // 选取最接近的日期
    }

    public List<InvestmentData> getInvestmentData(InvestmentRequest request) {
        String symbol = request.getSymbol();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        double investmentAmount = request.getInvestmentAmount();
        String investmentDay = request.getInvestmentDay();
        List<StockData> stockDataList = null;
        try {
            stockDataList = stockService.fetchStockData(symbol, startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (!stockDataList.isEmpty()) {
            // 使用 filterStockDataByInvestmentDay 获取每个投资日的数据，若有多年則接續年份
            List<Integer> yearList = Stream
                    .iterate(LocalDate.parse(startDate, DATE_FORMATTER).getYear(), year -> year + 1)
                    .limit(5).collect(Collectors.toList());
            List<StockData> filteredStockData = new ArrayList<>();
            for (int year : yearList) {
                filteredStockData.addAll(filterStockDataByInvestmentDay(stockDataList, investmentDay, year));
            }
            filteredStockData.sort(Comparator.comparing(StockData::getDate)); // 按日期排序

            List<InvestmentData> investmentDataList = new ArrayList<>();
            double totalAmount = 0;
            double totalShares = 0;

            for (StockData stockData : filteredStockData) {
                InvestmentData investmentData = new InvestmentData();
                investmentData.setInvestmentDate(stockData.getDate());
                investmentData.setInvestmentAmount(investmentAmount);

                // 計算總投資金額
                totalAmount += investmentAmount; // 累加投资金额
                investmentData.setTotalAmount(totalAmount);

                // 取得當期價格
                double currentPrice = stockData.getClose();

                // 計算當前股份
                totalShares += investmentAmount / currentPrice;
                investmentData.setShares(totalShares);

                // 計算當前收益
                // (本期購買後擁有的股份)*(本期股價)-(總投入資金)
                double currentReturn = totalShares * currentPrice - totalAmount;
                investmentData.setTotalReturn(currentReturn);

                // 計算當期收益率
                double totalReturnRate = currentReturn / totalAmount;
                investmentData.setTotalReturnRate(totalReturnRate);

                // 设置购买的股票列表
                investmentData.setStock(stockData);

                investmentDataList.add(investmentData);
            }
            return investmentDataList;
        } else {
            return null;
        }
    }

    public InvestmentPortfolio getPortfolioReturnData(InvestmentBatchRequest batchRequest) {
        // 取出所有投資請求
        List<InvestmentRequest> requests = batchRequest.getInvestments();
        // 建立投資組合
        InvestmentPortfolio portfolio = new InvestmentPortfolio();
        // 對每個投資請求進行處理，建立MAP後加入投資組合
        Map<String, List<InvestmentData>> investments = new HashMap<>();
        for (InvestmentRequest request : requests) {
            try {
                List<InvestmentData> investmentData = getInvestmentData(request);
                investments.put(request.getSymbol(), investmentData);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        portfolio.setPortfolio(investments);
        return portfolio;
    }
}
