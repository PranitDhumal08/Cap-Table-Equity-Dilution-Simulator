package com.captablex.calculator.model;

import com.captablex.domain.ShareClass;
import com.captablex.domain.StakeholderRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the complete output of a simulated funding round.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoundSimulationResult {
    private BigDecimal preMoneyValuation;
    private BigDecimal investmentAmount;
    private BigDecimal postMoneyValuation;
    private BigDecimal totalPreMoneyShares;
    private BigDecimal pricePerShare;
    private BigDecimal newSharesIssued;
    private BigDecimal totalPostMoneyShares;
    
    private String newInvestorName;
    private StakeholderRole newInvestorRole;
    private ShareClass newInvestorShareClass;
    private BigDecimal newInvestorOwnershipPercentage;
    
    @Builder.Default
    private List<StakeholderDilutionResult> stakeholders = new ArrayList<>();
    
    private String summary;
}
