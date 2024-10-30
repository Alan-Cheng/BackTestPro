package com.backtestpro.btp.service;

import java.util.Map;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.backtestpro.btp.pojo.StockData;
import com.backtestpro.btp.pojo.StockInfo;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class StockService {

    public List<StockData> getStockData(String symbol, String startDate, String endDate) {
        List<StockData> stockDataList = new ArrayList<>();
        try {
            // 获取 Python 脚本的输入流
            InputStream is = getClass().getResourceAsStream("/python/stock_data.py");

            // 创建临时文件
            File tempFile = File.createTempFile("stock_data", ".py");
            tempFile.deleteOnExit(); // JVM 退出时删除临时文件

            // 将脚本内容写入临时文件
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            // 通过 ProcessBuilder 执行临时文件
            ProcessBuilder processBuilder = new ProcessBuilder("python", tempFile.getAbsolutePath(), symbol, startDate,
                    endDate);
            processBuilder.redirectErrorStream(true); // 合并错误流
            Process process = processBuilder.start();

            // 读取脚本输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // 等待脚本完成
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // 将输出的 JSON 转换为 List<StockData>
                ObjectMapper mapper = new ObjectMapper();
                stockDataList = mapper.readValue(output.toString(), mapper.getTypeFactory().constructCollectionType(List.class, StockData.class));
            } else {
                System.err.println("Python script execution failed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stockDataList;
    }

    public List<StockInfo> getAllStockInfo() {
        String urlString = "https://openapi.twse.com.tw/v1/exchangeReport/STOCK_DAY_ALL";
        List<StockInfo> stockList = new ArrayList<>(); // 在方法外部声明

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
}
