package com.captablex.calculator;

import com.captablex.calculator.model.ExistingStakeholderShareInput;
import com.captablex.calculator.model.RoundSimulationInput;
import com.captablex.calculator.model.RoundSimulationResult;
import com.captablex.calculator.model.StakeholderDilutionResult;
import com.captablex.exception.InvalidFinancialOperationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.captablex.calculator.FinancialConstants.CALCULATION_SCALE;
import static com.captablex.calculator.FinancialConstants.HUNDRED;
import static com.captablex.calculator.FinancialConstants.PERCENTAGE_SCALE;
import static com.captablex.calculator.FinancialConstants.ROUNDING_MODE;
import static com.captablex.calculator.FinancialConstants.SHARE_SCALE;
import static com.captablex.calculator.FinancialConstants.ZERO_PERCENT;

/**
 * Pure, deterministic financial calculation engine for startup cap tables
 * and venture capital funding rounds.
 * <p>
 * Implements strict BigDecimal arithmetic using an internal calculation scale of
 * {@value FinancialConstants#CALCULATION_SCALE} and {@link RoundingMode#HALF_UP}
 * to eliminate cumulative rounding errors during intermediate operations.
 */
@Service
public class FinancialCalculationService {

    /**
     * Simulates a funding round based on an explicitly provided pre-money valuation,
     * investment amount, and current stakeholder share distribution.
     * <p>
     * This method is strictly read-only and does not mutate persistent database state.
     *
     * @param input the round simulation parameters and current share ledger
     * @return the calculated post-money capitalization metrics and per-stakeholder dilution
     * @throws InvalidFinancialOperationException if any financial parameter fails validation
     */
    public RoundSimulationResult simulateFundingRound(RoundSimulationInput input) {
        validateSimulationInput(input);

        BigDecimal preMoneyValuation = input.getPreMoneyValuation();
        BigDecimal investmentAmount = input.getInvestmentAmount();

        // 1. Calculate Post-Money Valuation = Pre-Money Valuation + Investment Amount
        BigDecimal postMoneyValuation = preMoneyValuation.add(investmentAmount);

        // 2. Calculate Total Pre-Money Shares = SUM(existing shares)
        BigDecimal totalPreMoneyShares = input.getExistingStakeholders().stream()
                .map(ExistingStakeholderShareInput::getSharesOwned)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPreMoneyShares.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidFinancialOperationException(
                    "Total pre-money shares must be greater than zero to determine price per share.");
        }

        // 3. Calculate Price Per Share (PPS) = Pre-Money Valuation / Total Pre-Money Shares
        BigDecimal pricePerShare = calculatePricePerShare(preMoneyValuation, totalPreMoneyShares);

        // 4. Calculate New Shares Issued = Investment Amount / Price Per Share
        BigDecimal newSharesIssued = calculateNewShares(investmentAmount, pricePerShare);

        // 5. Calculate Total Post-Money Shares = Total Pre-Money Shares + New Shares
        BigDecimal totalPostMoneyShares = totalPreMoneyShares.add(newSharesIssued);

        // 6. Calculate New Investor Ownership % = (New Shares / Total Post-Money Shares) * 100
        BigDecimal newInvestorOwnershipPercentage = calculateOwnershipPercentage(
                newSharesIssued, totalPostMoneyShares);

        // 7. Calculate dilution for each existing stakeholder
        List<StakeholderDilutionResult> stakeholderResults = new ArrayList<>();
        for (ExistingStakeholderShareInput existing : input.getExistingStakeholders()) {
            BigDecimal previousOwnership = calculateOwnershipPercentage(
                    existing.getSharesOwned(), totalPreMoneyShares);
            BigDecimal newOwnership = calculateOwnershipPercentage(
                    existing.getSharesOwned(), totalPostMoneyShares);

            // Dilution in percentage points = Previous % - New %
            BigDecimal dilutionPts = previousOwnership.subtract(newOwnership);

            // Relative Dilution % = ((Previous % - New %) / Previous %) * 100
            BigDecimal relativeDilution;
            if (previousOwnership.compareTo(BigDecimal.ZERO) > 0) {
                relativeDilution = dilutionPts
                        .divide(previousOwnership, CALCULATION_SCALE, ROUNDING_MODE)
                        .multiply(HUNDRED)
                        .setScale(PERCENTAGE_SCALE, ROUNDING_MODE);
            } else {
                relativeDilution = ZERO_PERCENT;
            }

            stakeholderResults.add(StakeholderDilutionResult.builder()
                    .stakeholderId(existing.getStakeholderId())
                    .name(existing.getName())
                    .role(existing.getRole())
                    .shareClass(existing.getShareClass())
                    .sharesOwned(existing.getSharesOwned().setScale(SHARE_SCALE, ROUNDING_MODE))
                    .previousOwnershipPercentage(previousOwnership)
                    .newOwnershipPercentage(newOwnership)
                    .dilutionPercentagePoints(dilutionPts)
                    .relativeDilutionPercentage(relativeDilution)
                    .build());
        }

        String summary = String.format(
                "%s receives approximately %s%% ownership after the funding round, issuing %s new %s shares at %s per share.",
                input.getInvestorName(),
                newInvestorOwnershipPercentage.toPlainString(),
                newSharesIssued.setScale(SHARE_SCALE, ROUNDING_MODE).toPlainString(),
                input.getShareClass(),
                pricePerShare.setScale(4, ROUNDING_MODE).toPlainString()
        );

        return RoundSimulationResult.builder()
                .preMoneyValuation(preMoneyValuation.setScale(SHARE_SCALE, ROUNDING_MODE))
                .investmentAmount(investmentAmount.setScale(SHARE_SCALE, ROUNDING_MODE))
                .postMoneyValuation(postMoneyValuation.setScale(SHARE_SCALE, ROUNDING_MODE))
                .totalPreMoneyShares(totalPreMoneyShares.setScale(SHARE_SCALE, ROUNDING_MODE))
                .pricePerShare(pricePerShare.setScale(4, ROUNDING_MODE))
                .newSharesIssued(newSharesIssued.setScale(SHARE_SCALE, ROUNDING_MODE))
                .totalPostMoneyShares(totalPostMoneyShares.setScale(SHARE_SCALE, ROUNDING_MODE))
                .newInvestorName(input.getInvestorName())
                .newInvestorRole(input.getInvestorRole())
                .newInvestorShareClass(input.getShareClass())
                .newInvestorOwnershipPercentage(newInvestorOwnershipPercentage)
                .stakeholders(stakeholderResults)
                .summary(summary)
                .build();
    }

    /**
     * Calculates the Price Per Share (PPS) = Pre-Money Valuation / Total Pre-Money Shares.
     */
    public BigDecimal calculatePricePerShare(BigDecimal preMoneyValuation, BigDecimal totalShares) {
        if (preMoneyValuation == null || preMoneyValuation.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidFinancialOperationException("Pre-money valuation must be greater than zero.");
        }
        if (totalShares == null || totalShares.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidFinancialOperationException("Total shares must be greater than zero to calculate price per share.");
        }
        return preMoneyValuation.divide(totalShares, CALCULATION_SCALE, ROUNDING_MODE);
    }

    /**
     * Calculates the number of newly issued shares = Investment Amount / Price Per Share.
     */
    public BigDecimal calculateNewShares(BigDecimal investmentAmount, BigDecimal pricePerShare) {
        if (investmentAmount == null || investmentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidFinancialOperationException("Investment amount must be greater than zero.");
        }
        if (pricePerShare == null || pricePerShare.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidFinancialOperationException("Price per share must be greater than zero.");
        }
        return investmentAmount.divide(pricePerShare, CALCULATION_SCALE, ROUNDING_MODE);
    }

    /**
     * Calculates the ownership percentage = (Shares Owned / Total Shares) * 100.
     * Returned with {@value FinancialConstants#PERCENTAGE_SCALE} decimal places.
     */
    public BigDecimal calculateOwnershipPercentage(BigDecimal sharesOwned, BigDecimal totalShares) {
        if (sharesOwned == null || sharesOwned.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidFinancialOperationException("Shares owned cannot be negative.");
        }
        if (totalShares == null || totalShares.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidFinancialOperationException("Total shares must be greater than zero.");
        }
        return sharesOwned
                .divide(totalShares, CALCULATION_SCALE, ROUNDING_MODE)
                .multiply(HUNDRED)
                .setScale(PERCENTAGE_SCALE, ROUNDING_MODE);
    }

    private void validateSimulationInput(RoundSimulationInput input) {
        if (input == null) {
            throw new InvalidFinancialOperationException("Simulation input payload cannot be null.");
        }
        if (input.getPreMoneyValuation() == null || input.getPreMoneyValuation().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidFinancialOperationException("Pre-money valuation must be strictly greater than zero.");
        }
        if (input.getInvestmentAmount() == null || input.getInvestmentAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidFinancialOperationException("Investment amount must be strictly greater than zero.");
        }
        if (input.getInvestorName() == null || input.getInvestorName().trim().isEmpty()) {
            throw new InvalidFinancialOperationException("Investor name cannot be blank.");
        }
        if (input.getExistingStakeholders() == null || input.getExistingStakeholders().isEmpty()) {
            throw new InvalidFinancialOperationException("Cannot simulate a funding round for a company with no existing shareholders.");
        }
    }
}
