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
import java.util.UUID;

/**
 * Encapsulates all necessary parameters to simulate a hypothetical funding round.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoundSimulationInput {
    private UUID companyId;
    private BigDecimal preMoneyValuation;
    private BigDecimal investmentAmount;
    private String investorName;
    private StakeholderRole investorRole;
    private ShareClass shareClass;
    
    @Builder.Default
    private List<ExistingStakeholderShareInput> existingStakeholders = new ArrayList<>();
}
