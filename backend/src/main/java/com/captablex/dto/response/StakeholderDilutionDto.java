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
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Stakeholder dilution metrics before and after the simulated round")
public class StakeholderDilutionDto {

    private UUID stakeholderId;

    @Schema(description = "Stakeholder name", example = "Founder A")
    private String name;

    @Schema(description = "Role in company", example = "FOUNDER")
    private StakeholderRole role;

    @Schema(description = "Share class", example = "COMMON")
    private ShareClass shareClass;

    @Schema(description = "Shares owned (unchanged by round)", example = "600000.0000")
    private BigDecimal shares;

    @Schema(description = "Ownership % prior to new round", example = "60.0000")
    private BigDecimal previousOwnershipPercentage;

    @Schema(description = "Ownership % post funding round", example = "48.0000")
    private BigDecimal newOwnershipPercentage;

    @Schema(description = "Absolute dilution in percentage points (Previous % - New %)", example = "12.0000")
    private BigDecimal dilutionPercentagePoints;

    @Schema(description = "Relative equity dilution % ((Previous % - New %) / Previous % * 100)", example = "20.0000")
    private BigDecimal relativeDilutionPercentage;
}
