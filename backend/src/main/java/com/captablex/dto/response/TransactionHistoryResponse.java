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
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Audit trail record of a committed venture capital funding round")
public class TransactionHistoryResponse {

    @Schema(description = "Unique transaction hash/UUID")
    private UUID transactionId;

    @Schema(description = "Target company UUID")
    private UUID companyId;

    @Schema(description = "Financing round name", example = "Series A Preferred")
    private String roundName;

    @Schema(description = "Investor entity name", example = "Alpha Ventures")
    private String investorName;

    @Schema(description = "Investor category", example = "VC")
    private StakeholderRole investorRole;

    @Schema(description = "Share class minted", example = "PREFERRED")
    private ShareClass shareClass;

    @Schema(description = "Pre-money valuation", example = "40000000.00")
    private BigDecimal preMoneyValuation;

    @Schema(description = "Capital injected", example = "10000000.00")
    private BigDecimal investmentAmount;

    @Schema(description = "Resulting post-money valuation", example = "50000000.00")
    private BigDecimal postMoneyValuation;

    @Schema(description = "Effective price per share", example = "40.0000")
    private BigDecimal pricePerShare;

    @Schema(description = "New shares allocated", example = "250000.0000")
    private BigDecimal sharesIssued;

    @Schema(description = "Investor equity ownership percentage", example = "20.0000")
    private BigDecimal investorOwnershipPct;

    @Schema(description = "Timestamp when transaction was permanently committed to database")
    private OffsetDateTime executedAt;
}
