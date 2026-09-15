package com.captablex.service;

import com.captablex.calculator.FinancialCalculationService;
import com.captablex.calculator.model.ExistingStakeholderShareInput;
import com.captablex.calculator.model.RoundSimulationInput;
import com.captablex.calculator.model.RoundSimulationResult;
import com.captablex.domain.CapTableLedger;
import com.captablex.domain.CompanyProfile;
import com.captablex.domain.FundingRoundTransaction;
import com.captablex.domain.Stakeholder;
import com.captablex.dto.request.ExecuteRoundRequest;
import com.captablex.dto.request.SimulateRoundRequest;
import com.captablex.dto.response.CapTableResponse;
import com.captablex.dto.response.SimulateRoundResponse;
import com.captablex.dto.response.StakeholderOwnershipDto;
import com.captablex.dto.response.TransactionHistoryResponse;
import com.captablex.exception.InvalidFinancialOperationException;
import com.captablex.exception.ResourceNotFoundException;
import com.captablex.mapper.CapTableMapper;
import com.captablex.repository.CapTableLedgerRepository;
import com.captablex.repository.CompanyProfileRepository;
import com.captablex.repository.FundingRoundTransactionRepository;
import com.captablex.repository.StakeholderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.captablex.calculator.FinancialConstants.SHARE_SCALE;
import static com.captablex.calculator.FinancialConstants.ZERO_PERCENT;

@Service
@RequiredArgsConstructor
public class CapTableService {

    private final CompanyProfileRepository companyProfileRepository;
    private final StakeholderRepository stakeholderRepository;
    private final CapTableLedgerRepository capTableLedgerRepository;
    private final FundingRoundTransactionRepository fundingRoundTransactionRepository;
    private final FinancialCalculationService calculationService;
    private final CapTableMapper mapper;

    @Transactional(readOnly = true)
    public CapTableResponse getCapTable(UUID companyId) {
        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        List<CapTableLedger> ledgers = capTableLedgerRepository.findAllByCompanyIdWithStakeholder(companyId);

        BigDecimal totalShares = ledgers.stream()
                .map(CapTableLedger::getSharesOwned)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<StakeholderOwnershipDto> stakeholderDtos = ledgers.stream().map(l -> {
            BigDecimal ownershipPct = totalShares.compareTo(BigDecimal.ZERO) > 0
                    ? calculationService.calculateOwnershipPercentage(l.getSharesOwned(), totalShares)
                    : ZERO_PERCENT;
            return mapper.toStakeholderOwnershipDto(l, ownershipPct);
        }).collect(Collectors.toList());

        return CapTableResponse.builder()
                .companyId(company.getCompanyId())
                .companyName(company.getCompanyName())
                .valuation(company.getCurrentValuation())
                .totalShares(totalShares.setScale(SHARE_SCALE, java.math.RoundingMode.HALF_UP))
                .stakeholders(stakeholderDtos)
                .build();
    }

    /**
     * Simulates a funding round without modifying persistent database state.
     */
    @Transactional(readOnly = true)
    public SimulateRoundResponse simulateRound(SimulateRoundRequest request) {
        CompanyProfile company = companyProfileRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company not found with ID: " + request.getCompanyId()));

        List<CapTableLedger> ledgers = capTableLedgerRepository
                .findAllByCompanyIdWithStakeholder(company.getCompanyId());

        if (ledgers.isEmpty()) {
            throw new InvalidFinancialOperationException(
                    "A funding round simulation cannot be executed for a company with no existing share capitalization.");
        }

        List<ExistingStakeholderShareInput> existingInputs = ledgers.stream()
                .map(mapper::toCalculationInput)
                .collect(Collectors.toList());

        RoundSimulationInput simulationInput = RoundSimulationInput.builder()
                .companyId(company.getCompanyId())
                .preMoneyValuation(request.getPreMoneyValuation())
                .investmentAmount(request.getInvestmentAmount())
                .investorName(request.getInvestorName())
                .investorRole(request.getInvestorType())
                .shareClass(request.getShareClass())
                .existingStakeholders(existingInputs)
                .build();

        RoundSimulationResult result = calculationService.simulateFundingRound(simulationInput);

        return mapper.toSimulateRoundResponse(company.getCompanyId(), result);
    }

    /**
     * Atomically executes and commits a venture funding round into persistent storage.
     * Mints the investor, inserts ledger allocation, updates valuation, and records an audit log.
     */
    @Transactional
    public SimulateRoundResponse executeRound(ExecuteRoundRequest request) {
        CompanyProfile company = companyProfileRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company not found with ID: " + request.getCompanyId()));

        List<CapTableLedger> ledgers = capTableLedgerRepository
                .findAllByCompanyIdWithStakeholder(company.getCompanyId());

        if (ledgers.isEmpty()) {
            throw new InvalidFinancialOperationException(
                    "Cannot execute a funding round for a company with no existing share capitalization.");
        }

        List<ExistingStakeholderShareInput> existingInputs = ledgers.stream()
                .map(mapper::toCalculationInput)
                .collect(Collectors.toList());

        RoundSimulationInput simulationInput = RoundSimulationInput.builder()
                .companyId(company.getCompanyId())
                .preMoneyValuation(request.getPreMoneyValuation())
                .investmentAmount(request.getInvestmentAmount())
                .investorName(request.getInvestorName())
                .investorRole(request.getInvestorType())
                .shareClass(request.getShareClass())
                .existingStakeholders(existingInputs)
                .build();

        RoundSimulationResult result = calculationService.simulateFundingRound(simulationInput);

        // 1. Create and persist new Stakeholder
        Stakeholder newInvestor = Stakeholder.builder()
                .company(company)
                .name(request.getInvestorName().trim())
                .role(request.getInvestorType())
                .build();
        newInvestor = stakeholderRepository.save(newInvestor);

        // 2. Issue newly calculated shares in cap table ledger
        CapTableLedger ledger = CapTableLedger.builder()
                .stakeholder(newInvestor)
                .shareClass(request.getShareClass())
                .sharesOwned(result.getNewSharesIssued())
                .build();
        capTableLedgerRepository.save(ledger);

        // 3. Update company's current valuation to post-money valuation
        company.setCurrentValuation(result.getPostMoneyValuation());
        companyProfileRepository.save(company);

        // 4. Record immutable transaction audit log
        String roundName = (request.getRoundName() != null && !request.getRoundName().isBlank())
                ? request.getRoundName().trim()
                : "Series A Preferred";

        FundingRoundTransaction tx = FundingRoundTransaction.builder()
                .company(company)
                .roundName(roundName)
                .investorName(request.getInvestorName().trim())
                .investorRole(request.getInvestorType())
                .shareClass(request.getShareClass())
                .preMoneyValuation(result.getPreMoneyValuation())
                .investmentAmount(result.getInvestmentAmount())
                .postMoneyValuation(result.getPostMoneyValuation())
                .pricePerShare(result.getPricePerShare())
                .sharesIssued(result.getNewSharesIssued())
                .investorOwnershipPct(result.getNewInvestorOwnershipPercentage())
                .build();
        fundingRoundTransactionRepository.save(tx);

        return mapper.toSimulateRoundResponse(company.getCompanyId(), result);
    }

    /**
     * Retrieves chronological audit history of executed funding transactions.
     */
    @Transactional(readOnly = true)
    public List<TransactionHistoryResponse> getTransactionHistory(UUID companyId) {
        if (!companyProfileRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found with ID: " + companyId);
        }
        return fundingRoundTransactionRepository.findByCompanyCompanyIdOrderByExecutedAtDesc(companyId)
                .stream()
                .map(mapper::toTransactionHistoryResponse)
                .collect(Collectors.toList());
    }
}
