package com.captablex.mapper;

import com.captablex.calculator.model.ExistingStakeholderShareInput;
import com.captablex.calculator.model.RoundSimulationResult;
import com.captablex.calculator.model.StakeholderDilutionResult;
import com.captablex.domain.CapTableLedger;
import com.captablex.domain.CompanyProfile;
import com.captablex.domain.Stakeholder;
import com.captablex.dto.response.CompanyResponse;
import com.captablex.dto.response.LedgerResponse;
import com.captablex.dto.response.SimulateRoundResponse;
import com.captablex.dto.response.StakeholderDilutionDto;
import com.captablex.dto.response.StakeholderOwnershipDto;
import com.captablex.dto.response.StakeholderResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CapTableMapper {

    public CompanyResponse toCompanyResponse(CompanyProfile entity) {
        return CompanyResponse.builder()
                .companyId(entity.getCompanyId())
                .companyName(entity.getCompanyName())
                .currentValuation(entity.getCurrentValuation())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public StakeholderResponse toStakeholderResponse(Stakeholder entity) {
        return StakeholderResponse.builder()
                .stakeholderId(entity.getStakeholderId())
                .companyId(entity.getCompany().getCompanyId())
                .name(entity.getName())
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public LedgerResponse toLedgerResponse(CapTableLedger entity) {
        return LedgerResponse.builder()
                .ledgerId(entity.getLedgerId())
                .stakeholderId(entity.getStakeholder().getStakeholderId())
                .shareClass(entity.getShareClass())
                .sharesOwned(entity.getSharesOwned())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ExistingStakeholderShareInput toCalculationInput(CapTableLedger ledger) {
        return ExistingStakeholderShareInput.builder()
                .stakeholderId(ledger.getStakeholder().getStakeholderId())
                .name(ledger.getStakeholder().getName())
                .role(ledger.getStakeholder().getRole())
                .shareClass(ledger.getShareClass())
                .sharesOwned(ledger.getSharesOwned())
                .build();
    }

    public StakeholderOwnershipDto toStakeholderOwnershipDto(CapTableLedger ledger, BigDecimal ownershipPct) {
        return StakeholderOwnershipDto.builder()
                .stakeholderId(ledger.getStakeholder().getStakeholderId())
                .name(ledger.getStakeholder().getName())
                .role(ledger.getStakeholder().getRole())
                .shareClass(ledger.getShareClass())
                .shares(ledger.getSharesOwned())
                .ownershipPercentage(ownershipPct)
                .build();
    }

    public SimulateRoundResponse toSimulateRoundResponse(UUID companyId, RoundSimulationResult result) {
        return SimulateRoundResponse.builder()
                .companyId(companyId)
                .preMoneyValuation(result.getPreMoneyValuation())
                .investmentAmount(result.getInvestmentAmount())
                .postMoneyValuation(result.getPostMoneyValuation())
                .totalPreMoneyShares(result.getTotalPreMoneyShares())
                .pricePerShare(result.getPricePerShare())
                .newSharesIssued(result.getNewSharesIssued())
                .totalPostMoneyShares(result.getTotalPostMoneyShares())
                .newInvestorName(result.getNewInvestorName())
                .newInvestorType(result.getNewInvestorRole())
                .newInvestorShareClass(result.getNewInvestorShareClass())
                .newInvestorOwnershipPercentage(result.getNewInvestorOwnershipPercentage())
                .stakeholders(result.getStakeholders().stream()
                        .map(this::toStakeholderDilutionDto)
                        .collect(Collectors.toList()))
                .summary(result.getSummary())
                .build();
    }

    public StakeholderDilutionDto toStakeholderDilutionDto(StakeholderDilutionResult res) {
        return StakeholderDilutionDto.builder()
                .stakeholderId(res.getStakeholderId())
                .name(res.getName())
                .role(res.getRole())
                .shareClass(res.getShareClass())
                .shares(res.getSharesOwned())
                .previousOwnershipPercentage(res.getPreviousOwnershipPercentage())
                .newOwnershipPercentage(res.getNewOwnershipPercentage())
                .dilutionPercentagePoints(res.getDilutionPercentagePoints())
                .relativeDilutionPercentage(res.getRelativeDilutionPercentage())
                .build();
    }
}
