package com.backtestpro.btp.service;

import java.util.Map;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.net.http.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backtestpro.btp.dto.StockData;
import com.backtestpro.btp.dto.StockInfo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class StockService {

    // //把這個方法註解掉，改用Java送請求
    // public List<StockData> getStockData(String symbol, String startDate, String endDate) {
    //     List<StockData> stockDataList = new ArrayList<>();
    //     try {
    //         // 获取 Python 脚本的输入流
    //         InputStream is = getClass().getResourceAsStream("/python/stock_data.py");

    //         // 创建临时文件
    //         File tempFile = File.createTempFile("stock_data", ".py");
    //         tempFile.deleteOnExit(); // JVM 退出时删除临时文件

    //         // 将脚本内容写入临时文件
    //         try (FileOutputStream out = new FileOutputStream(tempFile)) {
    //             byte[] buffer = new byte[1024];
    //             int bytesRead;
    //             while ((bytesRead = is.read(buffer)) != -1) {
    //                 out.write(buffer, 0, bytesRead);
    //             }
    //         }

    //         // 通过 ProcessBuilder 执行临时文件
    //         ProcessBuilder processBuilder = new ProcessBuilder("python", tempFile.getAbsolutePath(), symbol, startDate,
    //                 endDate);
    //         processBuilder.redirectErrorStream(true); // 合并错误流
    //         Process process = processBuilder.start();

    //         // 读取脚本输出
    //         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    //         StringBuilder output = new StringBuilder();
    //         String line;
    //         while ((line = reader.readLine()) != null) {
    //             output.append(line);
    //         }

    //         // 等待脚本完成
    //         int exitCode = process.waitFor();
    //         if (exitCode == 0) {
    //             // 将输出的 JSON 转换为 List<StockData>
    //             ObjectMapper mapper = new ObjectMapper();
    //             stockDataList = mapper.readValue(output.toString(), mapper.getTypeFactory().constructCollectionType(List.class, StockData.class));
    //         } else {
    //             System.err.println("Python script execution failed.");
    //         }

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return stockDataList;
    // }

    @Value("${app.twse-urls.stock-info}")
    private String stockInfoUrl;

    @Value("${app.twse-urls.stock-data}")
    private String stockDataUrl;

    @Value("${app.twse-urls.taiex-data}")
    private String taiexDataUrl;


    public List<StockInfo> getAllStockInfo() {
        
        String urlString = this.stockInfoUrl;
        List<StockInfo> stockList = new ArrayList<>();

        try {
            // 创建 URL 对象
            URL url = new URL(urlString);
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // 检查响应代码
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 解析 JSON 响应
                JSONArray jsonArray = new JSONArray(response.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String code = jsonObject.getString("Code");
                    String name = jsonObject.getString("Name"); // 假设股票名称的字段为 "Name"
                    stockList.add(new StockInfo(code, name));
                }
            } else {
                System.out.println("GET request failed. Response Code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stockList; // 返回股票信息列表
    }


    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<StockData> fetchStockData(String symbol, String startDate, String endDate) throws IOException, InterruptedException, ParseException {
        
        List<StockData> stockDataList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat twseDateFormat = new SimpleDateFormat("yyyyMM01");

        java.util.Date start = dateFormat.parse(startDate);
        java.util.Date end = dateFormat.parse(endDate);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(start);

        String preUrl = symbol == "TAIEX" ? taiexDataUrl : stockDataUrl;

        while (!cal.getTime().after(end)) {
            String date = twseDateFormat.format(cal.getTime());
            List<StockData> monthlyData = fetchMonthlyStockData(preUrl, symbol, date);
            stockDataList.addAll(monthlyData);

            // 移動到下個月的第一天
            cal.add(java.util.Calendar.MONTH, 1);
            cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        }

        return stockDataList;
    }

    private static List<StockData> fetchMonthlyStockData(String preUrl, String symbol, String date) throws IOException, InterruptedException {

        String url = symbol.equals("TAIEX")
            ? preUrl + "?response=json&date=" + date
            : preUrl + "?response=json&date=" + date + "&stockNo=" + symbol;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode rootNode = objectMapper.readTree(response.body());

        if (rootNode.get("data") == null) {
            return new ArrayList<>();
        }
    
        List<StockData> stockDataList = new ArrayList<>();
    
        // 判断是否为 TAIEX 数据
        if (symbol.equals("TAIEX")) {
            List<String> fields = objectMapper.convertValue(rootNode.get("fields"), new TypeReference<>() {});
            List<List<String>> data = objectMapper.convertValue(rootNode.get("data"), new TypeReference<>() {});
    
            for (List<String> row : data) {
                StockData stockData = new StockData();
                stockData.setSymbol(symbol);
    
                for (int i = 0; i < fields.size(); i++) {
                    String field = fields.get(i);
                    String value = row.get(i).replace(",", "");
    
                    switch (field) {
                        case "Date":
                            stockData.setDate(value.replace("/", "-"));
                            break;
                        case "Opening Index":
                            stockData.setOpen(Double.parseDouble(value));
                            break;
                        case "Highest Index":
                            stockData.setHigh(Double.parseDouble(value));
                            break;
                        case "Lowest Index":
                            stockData.setLow(Double.parseDouble(value));
                            break;
                        case "Closing Index":
                            stockData.setClose(Double.parseDouble(value));
                            break;
                    }
                }
    
                stockDataList.add(stockData);
            }
        } else {
            List<List<String>> data = objectMapper.convertValue(rootNode.get("data"), new TypeReference<>() {});
    
            for (List<String> row : data) {
                StockData stockData = new StockData();
                stockData.setSymbol(symbol);
                stockData.setDate(convertROCDateToAD(row.get(0)));
                stockData.setVolume(Long.parseLong(row.get(1).replace(",", "")));
                stockData.setTransactionAmount(row.get(2).replace(",", ""));
                stockData.setOpen(Double.parseDouble(row.get(3).replace(",", "")));
                stockData.setHigh(Double.parseDouble(row.get(4).replace(",", "")));
                stockData.setLow(Double.parseDouble(row.get(5).replace(",", "")));
                stockData.setClose(Double.parseDouble(row.get(6).replace(",", "")));
                stockData.setPriceChange(row.get(7));
                stockData.setTransactionCount(Long.parseLong(row.get(8).replace(",", "")));
                
                stockDataList.add(stockData);
            }
        }
        return stockDataList;
    }

    private static String convertROCDateToAD(String rocDate) {
        String[] parts = rocDate.split("/");
        int year = Integer.parseInt(parts[0]) + 1911;
        return year + "-" + parts[1] + "-" + parts[2];
    }

    public List<StockData> fetchTAIEXData(String startDate, String endDate) throws IOException, InterruptedException, ParseException {
        return fetchStockData("TAIEX", startDate, endDate);
    }
}
