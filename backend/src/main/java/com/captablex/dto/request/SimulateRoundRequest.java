package com.captablex.dto.request;

import com.captablex.domain.ShareClass;
import com.captablex.domain.StakeholderRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload for simulating a new funding round on an existing cap table")
public class SimulateRoundRequest {

    @NotNull(message = "Company ID is required")
    @Schema(description = "UUID of target company", example = "a1b2c3d4-0001-4000-8000-000000000001")
    private UUID companyId;

    @NotNull(message = "Pre-money valuation is required")
    @DecimalMin(value = "0.0001", message = "Pre-money valuation must be greater than zero")
    @Schema(description = "Agreed pre-money company valuation", example = "40000000.00")
    private BigDecimal preMoneyValuation;

    @NotNull(message = "Investment amount is required")
    @DecimalMin(value = "0.0001", message = "Investment amount must be greater than zero")
    @Schema(description = "Capital invested in this round", example = "10000000.00")
    private BigDecimal investmentAmount;

    @NotBlank(message = "Investor name cannot be blank")
    @Schema(description = "Name of the new incoming investor or syndicate", example = "Alpha Ventures")
    private String investorName;

    @NotNull(message = "Investor type is required (FOUNDER, VC, ANGEL, EMPLOYEE)")
    @Schema(description = "Role classification of incoming investor", example = "VC")
    private StakeholderRole investorType;

    @NotNull(message = "Share class is required (COMMON, PREFERRED)")
    @Schema(description = "Class of shares issued to the new investor", example = "PREFERRED")
    private ShareClass shareClass;
}
