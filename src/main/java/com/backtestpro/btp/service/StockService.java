package com.backtestpro.btp.service;

import java.util.Map;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;

import org.springframework.stereotype.Service;

import com.backtestpro.btp.pojo.StockData;
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
}
