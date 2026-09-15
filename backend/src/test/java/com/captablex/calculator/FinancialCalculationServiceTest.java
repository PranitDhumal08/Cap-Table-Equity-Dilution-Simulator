package com.captablex.calculator;

import com.captablex.calculator.model.ExistingStakeholderShareInput;
import com.captablex.calculator.model.RoundSimulationInput;
import com.captablex.calculator.model.RoundSimulationResult;
import com.captablex.calculator.model.StakeholderDilutionResult;
import com.captablex.domain.ShareClass;
import com.captablex.domain.StakeholderRole;
import com.captablex.exception.InvalidFinancialOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FinancialCalculationService Unit Tests")
class FinancialCalculationServiceTest {

    private FinancialCalculationService calculator;

    @BeforeEach
    void setUp() {
        calculator = new FinancialCalculationService();
    }

    @Nested
    @DisplayName("NovaFin Worked Example Tests (Section 23 Specification)")
    class NovaFinScenarioTests {

        @Test
        @DisplayName("Should execute NovaFin Series A simulation with exact mathematical accuracy")
        void testNovaFinSeriesASimulation() {
            // Setup NovaFin: ₹40M pre-money, 1M shares, ₹10M investment
            UUID founderAId = UUID.randomUUID();
            UUID founderBId = UUID.randomUUID();
            UUID employeePoolId = UUID.randomUUID();

            RoundSimulationInput input = RoundSimulationInput.builder()
                    .companyId(UUID.randomUUID())
                    .preMoneyValuation(new BigDecimal("40000000.00"))
                    .investmentAmount(new BigDecimal("10000000.00"))
                    .investorName("Alpha Ventures")
                    .investorRole(StakeholderRole.VC)
                    .shareClass(ShareClass.PREFERRED)
                    .existingStakeholders(List.of(
                            ExistingStakeholderShareInput.builder()
                                    .stakeholderId(founderAId)
                                    .name("Founder A")
                                    .role(StakeholderRole.FOUNDER)
                                    .shareClass(ShareClass.COMMON)
                                    .sharesOwned(new BigDecimal("600000.0000"))
                                    .build(),
                            ExistingStakeholderShareInput.builder()
                                    .stakeholderId(founderBId)
                                    .name("Founder B")
                                    .role(StakeholderRole.FOUNDER)
                                    .shareClass(ShareClass.COMMON)
                                    .sharesOwned(new BigDecimal("300000.0000"))
                                    .build(),
                            ExistingStakeholderShareInput.builder()
                                    .stakeholderId(employeePoolId)
                                    .name("Employee ESOP Pool")
                                    .role(StakeholderRole.EMPLOYEE)
                                    .shareClass(ShareClass.COMMON)
                                    .sharesOwned(new BigDecimal("100000.0000"))
                                    .build()
                    ))
                    .build();

            RoundSimulationResult result = calculator.simulateFundingRound(input);

            // 1. Post-money valuation = 50,000,000
            assertThat(result.getPostMoneyValuation()).isEqualByComparingTo("50000000.0000");

            // 2. Pre-money total shares = 1,000,000
            assertThat(result.getTotalPreMoneyShares()).isEqualByComparingTo("1000000.0000");

            // 3. PPS = 40.0000
            assertThat(result.getPricePerShare()).isEqualByComparingTo("40.0000");

            // 4. New shares = 250,000
            assertThat(result.getNewSharesIssued()).isEqualByComparingTo("250000.0000");

            // 5. Post-money total shares = 1,250,000
            assertThat(result.getTotalPostMoneyShares()).isEqualByComparingTo("1250000.0000");

            // 6. Investor ownership % = 20.0000%
            assertThat(result.getNewInvestorOwnershipPercentage()).isEqualByComparingTo("20.0000");

            // 7. Founder A: 48.0000% (dilution = 12.0000 percentage points, relative = 20.0000%)
            StakeholderDilutionResult founderA = result.getStakeholders().stream()
                    .filter(s -> s.getStakeholderId().equals(founderAId))
                    .findFirst().orElseThrow();
            assertThat(founderA.getPreviousOwnershipPercentage()).isEqualByComparingTo("60.0000");
            assertThat(founderA.getNewOwnershipPercentage()).isEqualByComparingTo("48.0000");
            assertThat(founderA.getDilutionPercentagePoints()).isEqualByComparingTo("12.0000");
            assertThat(founderA.getRelativeDilutionPercentage()).isEqualByComparingTo("20.0000");

            // 8. Founder B: 24.0000% (dilution = 6.0000 percentage points, relative = 20.0000%)
            StakeholderDilutionResult founderB = result.getStakeholders().stream()
                    .filter(s -> s.getStakeholderId().equals(founderBId))
                    .findFirst().orElseThrow();
            assertThat(founderB.getPreviousOwnershipPercentage()).isEqualByComparingTo("30.0000");
            assertThat(founderB.getNewOwnershipPercentage()).isEqualByComparingTo("24.0000");
            assertThat(founderB.getDilutionPercentagePoints()).isEqualByComparingTo("6.0000");
            assertThat(founderB.getRelativeDilutionPercentage()).isEqualByComparingTo("20.0000");

            // 9. Employee Pool: 8.0000% (dilution = 2.0000 percentage points, relative = 20.0000%)
            StakeholderDilutionResult empPool = result.getStakeholders().stream()
                    .filter(s -> s.getStakeholderId().equals(employeePoolId))
                    .findFirst().orElseThrow();
            assertThat(empPool.getPreviousOwnershipPercentage()).isEqualByComparingTo("10.0000");
            assertThat(empPool.getNewOwnershipPercentage()).isEqualByComparingTo("8.0000");
            assertThat(empPool.getDilutionPercentagePoints()).isEqualByComparingTo("2.0000");
            assertThat(empPool.getRelativeDilutionPercentage()).isEqualByComparingTo("20.0000");

            // 10. Total ownership sums to 100%
            BigDecimal totalPostMoneyPct = result.getNewInvestorOwnershipPercentage()
                    .add(founderA.getNewOwnershipPercentage())
                    .add(founderB.getNewOwnershipPercentage())
                    .add(empPool.getNewOwnershipPercentage());
            assertThat(totalPostMoneyPct).isEqualByComparingTo("100.0000");

            assertThat(result.getSummary()).contains("Alpha Ventures").contains("20.0000%");
        }
    }

    @Nested
    @DisplayName("Core Financial Calculation Step Tests")
    class IndividualCalculationTests {

        @Test
        @DisplayName("1. Post-money valuation: Pre-money + Investment")
        void testPostMoneyValuationCalculation() {
            BigDecimal preMoney = new BigDecimal("25000000.00");
            BigDecimal investment = new BigDecimal("5000000.00");

            RoundSimulationInput input = RoundSimulationInput.builder()
                    .preMoneyValuation(preMoney)
                    .investmentAmount(investment)
                    .investorName("Test VC")
                    .investorRole(StakeholderRole.VC)
                    .shareClass(ShareClass.PREFERRED)
                    .existingStakeholders(List.of(
                            ExistingStakeholderShareInput.builder()
                                    .stakeholderId(UUID.randomUUID())
                                    .name("Sole Founder")
                                    .role(StakeholderRole.FOUNDER)
                                    .shareClass(ShareClass.COMMON)
                                    .sharesOwned(new BigDecimal("1000000"))
                                    .build()
                    ))
                    .build();

            RoundSimulationResult result = calculator.simulateFundingRound(input);
            assertThat(result.getPostMoneyValuation()).isEqualByComparingTo("30000000.0000");
        }

        @Test
        @DisplayName("2. PPS calculation: Pre-money / Total pre-money shares")
        void testPPSCalculation() {
            BigDecimal preMoney = new BigDecimal("50000000.00");
            BigDecimal totalShares = new BigDecimal("2000000.00");

            BigDecimal pps = calculator.calculatePricePerShare(preMoney, totalShares);
            assertThat(pps).isEqualByComparingTo("25.0000000000");
        }

        @Test
        @DisplayName("3. New shares calculation: Investment / PPS")
        void testNewSharesCalculation() {
            BigDecimal investment = new BigDecimal("5000000.00");
            BigDecimal pps = new BigDecimal("25.00");

            BigDecimal newShares = calculator.calculateNewShares(investment, pps);
            assertThat(newShares).isEqualByComparingTo("200000.0000000000");
        }

        @Test
        @DisplayName("4. Existing shareholder dilution calculation")
        void testExistingShareholderDilution() {
            BigDecimal existingShares = new BigDecimal("800000");
            BigDecimal preShares = new BigDecimal("1000000");
            BigDecimal postShares = new BigDecimal("1250000");

            BigDecimal prevPct = calculator.calculateOwnershipPercentage(existingShares, preShares);
            BigDecimal newPct = calculator.calculateOwnershipPercentage(existingShares, postShares);

            assertThat(prevPct).isEqualByComparingTo("80.0000");
            assertThat(newPct).isEqualByComparingTo("64.0000");
            assertThat(prevPct.subtract(newPct)).isEqualByComparingTo("16.0000");
        }

        @Test
        @DisplayName("5. New investor ownership calculation")
        void testNewInvestorOwnership() {
            BigDecimal newShares = new BigDecimal("250000");
            BigDecimal totalPostShares = new BigDecimal("1250000");

            BigDecimal ownership = calculator.calculateOwnershipPercentage(newShares, totalPostShares);
            assertThat(ownership).isEqualByComparingTo("20.0000");
        }

        @Test
        @DisplayName("6. Ownership percentages for uneven distribution")
        void testUnevenOwnershipPercentages() {
            BigDecimal shares = new BigDecimal("333333");
            BigDecimal total = new BigDecimal("1000000");

            BigDecimal pct = calculator.calculateOwnershipPercentage(shares, total);
            assertThat(pct).isEqualByComparingTo("33.3333");
        }

        @Test
        @DisplayName("7. Zero or negative pre-money valuation rejection")
        void testZeroValuationRejection() {
            assertThatThrownBy(() -> calculator.calculatePricePerShare(BigDecimal.ZERO, new BigDecimal("1000000")))
                    .isInstanceOf(InvalidFinancialOperationException.class)
                    .hasMessageContaining("Pre-money valuation must be greater than zero");

            assertThatThrownBy(() -> calculator.calculatePricePerShare(new BigDecimal("-100"), new BigDecimal("1000000")))
                    .isInstanceOf(InvalidFinancialOperationException.class)
                    .hasMessageContaining("Pre-money valuation must be greater than zero");
        }

        @Test
        @DisplayName("8. Zero or negative investment amount rejection")
        void testZeroInvestmentRejection() {
            assertThatThrownBy(() -> calculator.calculateNewShares(BigDecimal.ZERO, new BigDecimal("40.00")))
                    .isInstanceOf(InvalidFinancialOperationException.class)
                    .hasMessageContaining("Investment amount must be greater than zero");

            assertThatThrownBy(() -> calculator.calculateNewShares(new BigDecimal("-5000"), new BigDecimal("40.00")))
                    .isInstanceOf(InvalidFinancialOperationException.class)
                    .hasMessageContaining("Investment amount must be greater than zero");
        }

        @Test
        @DisplayName("9. Empty cap table rejection")
        void testEmptyCapTableRejection() {
            RoundSimulationInput input = RoundSimulationInput.builder()
                    .preMoneyValuation(new BigDecimal("40000000"))
                    .investmentAmount(new BigDecimal("10000000"))
                    .investorName("Test VC")
                    .investorRole(StakeholderRole.VC)
                    .shareClass(ShareClass.PREFERRED)
                    .existingStakeholders(Collections.emptyList())
                    .build();

            assertThatThrownBy(() -> calculator.simulateFundingRound(input))
                    .isInstanceOf(InvalidFinancialOperationException.class)
                    .hasMessageContaining("Cannot simulate a funding round for a company with no existing shareholders");
        }

        @Test
        @DisplayName("10. BigDecimal rounding behavior & precision tolerance")
        void testRoundingBehaviorAndPrecision() {
            // Fractional PPS test: ₹10,000,000 valuation across 3,000,000 shares = ₹3.3333333333/share
            BigDecimal preMoney = new BigDecimal("10000000.00");
            BigDecimal totalShares = new BigDecimal("3000000.00");
            BigDecimal pps = calculator.calculatePricePerShare(preMoney, totalShares);
            
            assertThat(pps).isEqualByComparingTo("3.3333333333");

            // Investment of ₹1,000,000 at PPS 3.3333333333 = 300000.0000030000 shares
            BigDecimal newShares = calculator.calculateNewShares(new BigDecimal("1000000.00"), pps);
            assertThat(newShares).isEqualByComparingTo("300000.0000030000");

            // Ownership sum tolerance check: sum of 3 equal founders after 25% dilution
            RoundSimulationInput input = RoundSimulationInput.builder()
                    .preMoneyValuation(new BigDecimal("30000000.00"))
                    .investmentAmount(new BigDecimal("10000000.00"))
                    .investorName("VC One")
                    .investorRole(StakeholderRole.VC)
                    .shareClass(ShareClass.PREFERRED)
                    .existingStakeholders(List.of(
                            ExistingStakeholderShareInput.builder()
                                    .stakeholderId(UUID.randomUUID())
                                    .name("Founder 1")
                                    .role(StakeholderRole.FOUNDER)
                                    .shareClass(ShareClass.COMMON)
                                    .sharesOwned(new BigDecimal("1000000"))
                                    .build(),
                            ExistingStakeholderShareInput.builder()
                                    .stakeholderId(UUID.randomUUID())
                                    .name("Founder 2")
                                    .role(StakeholderRole.FOUNDER)
                                    .shareClass(ShareClass.COMMON)
                                    .sharesOwned(new BigDecimal("1000000"))
                                    .build(),
                            ExistingStakeholderShareInput.builder()
                                    .stakeholderId(UUID.randomUUID())
                                    .name("Founder 3")
                                    .role(StakeholderRole.FOUNDER)
                                    .shareClass(ShareClass.COMMON)
                                    .sharesOwned(new BigDecimal("1000000"))
                                    .build()
                    ))
                    .build();

            RoundSimulationResult result = calculator.simulateFundingRound(input);
            BigDecimal totalPct = result.getNewInvestorOwnershipPercentage();
            for (StakeholderDilutionResult s : result.getStakeholders()) {
                totalPct = totalPct.add(s.getNewOwnershipPercentage());
            }

            // Sum should equal 100.0000% within tolerance of 0.0001
            BigDecimal diff = totalPct.subtract(new BigDecimal("100.0000")).abs();
            assertThat(diff).isLessThanOrEqualTo(new BigDecimal("0.0002"));
        }
    }
}
