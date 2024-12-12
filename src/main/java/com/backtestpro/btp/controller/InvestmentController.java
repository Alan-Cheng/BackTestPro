package com.backtestpro.btp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.backtestpro.btp.dto.InvestmentBatchRequest;
import com.backtestpro.btp.dto.InvestmentRequest;
import com.backtestpro.btp.pojo.InvestmentData;
import com.backtestpro.btp.pojo.InvestmentPortfolio;
import com.backtestpro.btp.service.InvestmentService;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/investment")
@CrossOrigin(origins = "http://localhost:4200")
public class InvestmentController {

    @Autowired
    private InvestmentService investmentService;

    @PostMapping("/regular")
    public List<InvestmentData> getRegularInvestmentReturn(@RequestBody InvestmentRequest request) {
        try {
            return investmentService.getInvestmentData(request);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/regular/multiple")
    public InvestmentPortfolio getRegularMultipleReturn(@RequestBody InvestmentBatchRequest batchRequest) {
        try {
            return investmentService.getPortfolioReturnData(batchRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/pie")
    public InvestmentPortfolio pieTest() {
        return null;
    }
}
