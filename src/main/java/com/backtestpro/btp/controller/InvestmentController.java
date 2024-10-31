package com.backtestpro.btp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.backtestpro.btp.dto.InvestmentRequest;
import com.backtestpro.btp.pojo.InvestmentData;
import com.backtestpro.btp.pojo.InvestmentPortfolio;
import com.backtestpro.btp.service.InvestmentService;

@RestController
@RequestMapping("/investment")
public class InvestmentController {

    @Autowired
    private InvestmentService investmentService;

    @PostMapping("/regular")
    public List<InvestmentData> getRegularInvestmentReturn(@RequestBody InvestmentRequest request) {
        String symbol = request.getSymbol();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        double investmentAmount = request.getInvestmentAmount();
        String investmentDay = request.getInvestmentDay();
        try {
            return investmentService.getInvestmentData(symbol, startDate, endDate, investmentAmount, investmentDay);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/regular/multiple")
    public InvestmentPortfolio getRegularMultipleReturn(

    ) {
        return null;
    }

}
