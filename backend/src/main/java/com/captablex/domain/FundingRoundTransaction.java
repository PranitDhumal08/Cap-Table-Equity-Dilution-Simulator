package com.captablex.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Immutable entity capturing a completed venture capital funding round transaction.
 */
@Entity
@Table(name = "funding_round_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundingRoundTransaction {

    @Id
    @Column(name = "transaction_id", nullable = false, updatable = false)
    private UUID transactionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyProfile company;

    @Column(name = "round_name", nullable = false, length = 100)
    private String roundName;

    @Column(name = "investor_name", nullable = false)
    private String investorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "investor_role", nullable = false, length = 50)
    private StakeholderRole investorRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "share_class", nullable = false, length = 50)
    private ShareClass shareClass;

    @Column(name = "pre_money_valuation", nullable = false, precision = 19, scale = 4)
    private BigDecimal preMoneyValuation;

    @Column(name = "investment_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal investmentAmount;

    @Column(name = "post_money_valuation", nullable = false, precision = 19, scale = 4)
    private BigDecimal postMoneyValuation;

    @Column(name = "price_per_share", nullable = false, precision = 19, scale = 4)
    private BigDecimal pricePerShare;

    @Column(name = "shares_issued", nullable = false, precision = 19, scale = 4)
    private BigDecimal sharesIssued;

    @Column(name = "investor_ownership_pct", nullable = false, precision = 8, scale = 4)
    private BigDecimal investorOwnershipPct;

    @Column(name = "executed_at", nullable = false, updatable = false)
    private OffsetDateTime executedAt;

    @PrePersist
    protected void onCreate() {
        if (this.transactionId == null) {
            this.transactionId = UUID.randomUUID();
        }
        if (this.executedAt == null) {
            this.executedAt = OffsetDateTime.now();
        }
    }
}
