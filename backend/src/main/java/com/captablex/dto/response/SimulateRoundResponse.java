package com.captablex.dto.response;

import com.captablex.domain.ShareClass;
import com.captablex.domain.StakeholderRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Comprehensive result of a simulated venture funding round")
public class SimulateRoundResponse {

    @Schema(description = "UUID of analyzed company")
    private UUID companyId;

    @Schema(description = "Pre-money valuation of the company", example = "40000000.00")
    private BigDecimal preMoneyValuation;

    @Schema(description = "New investment amount injected into company", example = "10000000.00")
    private BigDecimal investmentAmount;

    @Schema(description = "Post-money valuation (Pre-money + Investment)", example = "50000000.00")
    private BigDecimal postMoneyValuation;

    @Schema(description = "Total shares before funding round", example = "1000000.0000")
    private BigDecimal totalPreMoneyShares;

    @Schema(description = "Price per share (Pre-money / Total Pre-money Shares)", example = "40.0000")
    private BigDecimal pricePerShare;

    @Schema(description = "Newly issued shares to investor (Investment / PPS)", example = "250000.0000")
    private BigDecimal newSharesIssued;

    @Schema(description = "Total post-money shares (Pre-money shares + New shares)", example = "1250000.0000")
    private BigDecimal totalPostMoneyShares;

    @Schema(description = "Name of incoming investor", example = "Alpha Ventures")
    private String newInvestorName;

    @Schema(description = "Role classification of incoming investor", example = "VC")
    private StakeholderRole newInvestorType;

    @Schema(description = "Class of shares issued to new investor", example = "PREFERRED")
    private ShareClass newInvestorShareClass;

    @Schema(description = "Percentage ownership acquired by new investor", example = "20.0000")
    private BigDecimal newInvestorOwnershipPercentage;

    @Builder.Default
    @Schema(description = "Detailed dilution analysis for all existing stakeholders")
    private List<StakeholderDilutionDto> stakeholders = new ArrayList<>();

    @Schema(description = "Executive summary of round economics", example = "Alpha Ventures receives approximately 20.0000% ownership after the funding round...")
    private String summary;
}
