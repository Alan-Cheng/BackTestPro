package com.backtestpro.btp.dto;

import java.util.List;

public class InvestmentBatchRequest {
    private List<InvestmentRequest> investments;

    public List<InvestmentRequest> getInvestments() {
        return investments;
    }

    public void setInvestments(List<InvestmentRequest> investments) {
        this.investments = investments;
    }
}
